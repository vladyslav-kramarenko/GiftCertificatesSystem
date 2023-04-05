package com.epam.esm.dao.impl.giftCertificate;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.tag.TagSqlQueries;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
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
import static com.epam.esm.util.Utilities.getKey;
import static com.epam.esm.util.Utilities.getOrderByClause;

@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDao tagDao;

    @Autowired
    public GiftCertificateDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public GiftCertificate create(GiftCertificate giftCertificate) throws DbException {
        createGiftCertificate(giftCertificate);
        addTags(giftCertificate);
        Optional<GiftCertificate> createdGiftCertificate = getById(giftCertificate.getId());
        if (createdGiftCertificate.isEmpty())
            throw new DbException("can't find created gift certificate by id: " + giftCertificate.getId());
        return createdGiftCertificate.get();
    }

    private void createGiftCertificate(GiftCertificate giftCertificate) throws DbException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
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
        } catch (Exception e) {
            throw new DbException("Error while creating new gift certificate: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void addTags(GiftCertificate giftCertificate) throws DbException {
        List<Tag> tags = giftCertificate.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                Optional<Tag> existingTag = tagDao.getByName(tag.getName());
                if (existingTag.isPresent()) {
                    tag.setId(existingTag.get().getId());
                    addTagToCertificate(giftCertificate, tag);
                } else {
                    createTagAndAddToCertificate(giftCertificate, tag);
                }
            }
        }
    }

    private void addTagToCertificate(GiftCertificate giftCertificate, Tag tag) {
        if (!hasCertificateTag(giftCertificate, tag)) {
            jdbcTemplate.update(
                    TagSqlQueries.ADD_TAG_TO_CERTIFICATE,
                    giftCertificate.getId(),
                    tag.getId()
            );
        }
    }

    private boolean hasCertificateTag(GiftCertificate giftCertificate, Tag tag) {
        List<Integer> counts = jdbcTemplate.query(
                COUNT_CERTIFICATE_TAGS_BY_CERTIFICATE_ID_AND_TAG_ID,
                (rs, rowNum) -> rs.getInt(1),
                giftCertificate.getId(),
                tag.getId()
        );
        return counts.size() > 0 && counts.get(0) != 0;
    }

    private void createTagAndAddToCertificate(GiftCertificate giftCertificate, Tag tag) throws DbException {
        tagDao.create(tag);
        jdbcTemplate.update(
                TagSqlQueries.ADD_TAG_TO_CERTIFICATE,
                giftCertificate.getId(),
                tag.getId()
        );
    }

    public Optional<GiftCertificate> getById(long id) {
        List<GiftCertificate> certificates = jdbcTemplate.query(
                GET_CERTIFICATE_WITH_TAGS_BY_ID,
                ps -> ps.setLong(1, id),
                new GiftCertificateRowMapper()
        );
        if (certificates.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(certificates.get(0));
    }

    public List<GiftCertificate> getAll() {
        return getAll(null);
    }

    public List<GiftCertificate> getAll(Sort sort) {
        String sql = getOrderByClause(GET_ALL_CERTIFICATES_WITH_TAGS, sort);
        GiftCertificateRowCallbackHandler handler = new GiftCertificateRowCallbackHandler();
        jdbcTemplate.query(sql, handler);
        return handler.getCertificates();
    }

    public List<GiftCertificate> getAllWithSearchQuery(String searchQuery, Sort sort) {//TODO change to search with saved procedure in sql
        if (searchQuery == null || searchQuery.isEmpty()) return getAll(sort);
        String sql = getOrderByClause(GET_ALL_CERTIFICATES_WITH_TAGS_AND_NAME_OR_DESCRIPTION_SEARCH, sort);
        String wildcardSearchQuery = "%" + searchQuery + "%";
        GiftCertificateRowCallbackHandler handler = new GiftCertificateRowCallbackHandler();
        jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setString(1, wildcardSearchQuery);
                    ps.setString(2, wildcardSearchQuery);
                },
                handler);
        return handler.getCertificates();
    }

    public GiftCertificate update(GiftCertificate giftCertificate) throws DbException {
        updateMainCertificateData(giftCertificate);

        // Delete all existing tags for the certificate
        jdbcTemplate.update(
                DELETE_CERTIFICATE_TAGS_BY_CERTIFICATE_ID,
                giftCertificate.getId()
        );

        // Add new tags for the certificate
        addTags(giftCertificate);
        return giftCertificate;
    }

    private void updateMainCertificateData(GiftCertificate giftCertificate) throws DbException {
        try {
            jdbcTemplate.update(
                    UPDATE_CERTIFICATE,
                    giftCertificate.getName(),
                    giftCertificate.getDescription(),
                    giftCertificate.getPrice(),
                    giftCertificate.getDuration(),
                    giftCertificate.getId()
            );
        } catch (Exception e) {
            throw new DbException("Got error while trying to update certificate: " + Arrays.toString(e.getStackTrace()));
        }
    }

    public boolean delete(long id) throws DbException {
        try {
            int deleteCount = jdbcTemplate.update(DELETE_CERTIFICATE_BY_ID, id);
            return deleteCount > 0;
        } catch (Exception e) {
            throw new DbException("Got error while trying to delete certificate by id = " + id + ": " + Arrays.toString(e.getStackTrace()));
        }
    }
}
