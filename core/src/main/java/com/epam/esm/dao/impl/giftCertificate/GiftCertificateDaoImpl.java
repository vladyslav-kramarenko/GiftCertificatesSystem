package com.epam.esm.dao.impl.giftCertificate;

import com.epam.esm.dao.impl.AbstractDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.impl.tag.TagSqlQueries;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

import static com.epam.esm.dao.impl.giftCertificate.GiftCertificateSqlQueries.*;
import static com.epam.esm.dao.impl.tag.TagSqlQueries.*;
import static com.epam.esm.util.Utilities.*;

/**
 * Repository class for GiftCertificate objects, implementing the GiftCertificateDao interface.
 * Provides methods for CRUD operations on GiftCertificate instances in the database.
 */
@Repository
public class GiftCertificateDaoImpl extends AbstractDao<GiftCertificate, Long> implements GiftCertificateDao {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateDaoImpl.class);

    /**
     * Constructs a GiftCertificateDaoImpl instance with the given JdbcTemplate.
     *
     * @param jdbcTemplate the JdbcTemplate to be used for database operations
     */
    @Autowired
    public GiftCertificateDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new GiftCertificateRowMapper());
    }

    /**
     * Creates a new GiftCertificate in the database and returns the created object.
     *
     * @param giftCertificate the GiftCertificate instance to be created in the database
     * @return the created GiftCertificate instance
     * @throws DbException if there is an error while creating the gift certificate in the database
     */
    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) throws DbException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        CREATE_CERTIFICATE,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, giftCertificate.getName());
                ps.setString(2, giftCertificate.getDescription());
                ps.setBigDecimal(3, giftCertificate.getPrice());
                ps.setInt(4, giftCertificate.getDuration());
                return ps;
            }, keyHolder);
            giftCertificate.setId(getKey(keyHolder));
            GiftCertificate newGiftCertificate = getById(giftCertificate.getId()).get();
            giftCertificate.setLastUpdateDate(newGiftCertificate.getLastUpdateDate());
            giftCertificate.setCreateDate(newGiftCertificate.getCreateDate());
            return giftCertificate;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new DbException("Error while creating new gift certificate: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Adds a Tag to the GiftCertificate in the database.
     *
     * @param giftCertificate the GiftCertificate instance to which the tag will be added
     * @param tag             the Tag instance to be added to the gift certificate
     * @throws IllegalArgumentException if the gift certificate already has the specified tag
     */
    @Override
    public void addTagToCertificate(GiftCertificate giftCertificate, Tag tag) {
        if (!hasCertificateTag(giftCertificate, tag)) {
            getJdbcTemplate().update(
                    TagSqlQueries.ADD_TAG_TO_CERTIFICATE,
                    giftCertificate.getId(),
                    tag.id()
            );
        } else {
            throw new IllegalArgumentException("Certificate with id " + giftCertificate.getId() + " already tag: " + tag);
        }
    }

    /**
     * Checks if the given GiftCertificate has the specified Tag.
     *
     * @param giftCertificate the GiftCertificate instance to check for the tag
     * @param tag             the Tag instance to check for in the gift certificate
     * @return true if the gift certificate has the specified tag, false otherwise
     */
    private boolean hasCertificateTag(GiftCertificate giftCertificate, Tag tag) {
        List<Integer> counts = getJdbcTemplate().query(
                COUNT_CERTIFICATE_TAGS_BY_CERTIFICATE_ID_AND_TAG_ID,
                (rs, rowNum) -> rs.getInt(1),
                giftCertificate.getId(),
                tag.id()
        );
        return counts.size() > 0 && counts.get(0) != 0;
    }

    @Override
    protected String getSelectByIdSql() {
        return GET_CERTIFICATE_WITH_TAGS_BY_ID;
    }

    @Override
    protected String getSelectAllSql() {
        return GET_ALL_CERTIFICATES_WITH_TAGS;
    }

    @Override
    protected String getDeleteByIdSql() {
        return DELETE_CERTIFICATE_BY_ID;
    }

    /**
     * Deletes all tags associated with the specified GiftCertificate by its ID.
     *
     * @param giftCertificateId the ID of the GiftCertificate to delete the tags from
     */
    public void deleteAllTagsForCertificateById(Long giftCertificateId) {
        getJdbcTemplate().update(
                DELETE_CERTIFICATE_TAGS_BY_CERTIFICATE_ID,
                giftCertificateId
        );
    }

    @Override
    public Optional<GiftCertificate> update(GiftCertificate giftCertificate) throws DbException {
        try {
            getJdbcTemplate().update(
                    UPDATE_CERTIFICATE,
                    giftCertificate.getName(),
                    giftCertificate.getDescription(),
                    giftCertificate.getPrice(),
                    giftCertificate.getDuration(),
                    giftCertificate.getId()
            );
            return getById(giftCertificate.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new DbException("Got error while trying to update certificate: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Retrieves all GiftCertificate instances from the database, sorted according to the provided Sort object.
     *
     * @param sort the Sort object specifying the desired sorting
     * @return a List of GiftCertificate instances sorted as specified
     * @throws DbException if there is an error while retrieving the gift certificates from the database
     */
    @Override
    public List<GiftCertificate> getAll(Sort sort) throws DbException {
        String sql = getOrderByClause(getSelectAllSql(), sort);
        try {
            GiftCertificateRowCallbackHandler handler = new GiftCertificateRowCallbackHandler();
            getJdbcTemplate().query(sql, handler);
            return handler.getCertificates();
        } catch (Exception e) {
            throw new DbException("Error while getting all entities: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Retrieves all GiftCertificate instances associated with the specified Tag ID.
     *
     * @param tagId the ID of the Tag to retrieve the associated gift certificates for
     * @return a List of GiftCertificate instances associated with the specified Tag ID
     */
    @Override
    public List<GiftCertificate> getCertificatesByTagId(Long tagId) {
        GiftCertificateRowCallbackHandler handler = new GiftCertificateRowCallbackHandler();
        getJdbcTemplate().query(
                SELECT_CERTIFICATES_BY_TAG_ID,
                ps -> ps.setLong(1, tagId),
                handler);
        return handler.getCertificates();
    }

    /**
     * Retrieves all GiftCertificate instances matching the specified search term and sorted according to the provided Sort object.
     *
     * @param searchTerm the search term to filter the gift certificates by
     * @param sort       the Sort object specifying the desired sorting
     * @return a List of GiftCertificate instances matching the search term and sorted as specified
     * @throws DbException if there is an error while retrieving the gift certificates from the database
     */
    @Override
    public List<GiftCertificate> getAllWithSearchQuery(String searchTerm, Sort sort) throws DbException {
        try {
            String sql = "{CALL search_gift_certificates_sort(?, ?)}";
            GiftCertificateRowCallbackHandler handler = new GiftCertificateRowCallbackHandler();
            getJdbcTemplate().query(
                    sql,
                    ps -> {
                        ps.setString(1, searchTerm == null ? "" : searchTerm);
                        ps.setString(2, concatSort(sort, "id asc"));
                    },
                    handler);
            return handler.getCertificates();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new DbException("Got error while trying to get all certificates");
        }
    }
}
