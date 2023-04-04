package com.epam.esm.dao.impl.tag;

import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.dao.impl.tag.TagSqlQueries.GET_ALL_TAGS;
import static com.epam.esm.util.Utilities.getOrderByClause;

@Repository
public class TagDaoImpl implements TagDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Tag create(Tag tag) throws DbException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        TagSqlQueries.CREATE_TAG,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, tag.getName());
                return ps;
            }, keyHolder);
            tag.setId(getKey(keyHolder));
            return tag;
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Tag with name '" + tag.getName() + "' already exists");
        } catch (Exception e) {
            throw new DbException("Error while creating a Tag" + Arrays.toString(e.getStackTrace()));
        }
    }

    private long getKey(KeyHolder keyHolder) throws DbException {
        Object keyObject = keyHolder.getKeys().getOrDefault("GENERATED_KEY", null);
        if (keyObject != null) {
            BigInteger key = (BigInteger) keyObject;
            return key.longValue();
        } else {
            keyObject = keyHolder.getKeys().get("id");
            if (keyObject == null) throw new DbException("Generated key for new tag not found");
            return (long) keyObject;
        }
    }

    @Override
    public Optional<Tag> getById(long id) throws DbException {
        try {
            List<Tag> tags = jdbcTemplate.query(
                    TagSqlQueries.GET_TAG_BY_ID,
                    ps -> ps.setLong(1, id),
                    new TagRowMapper()
            );
            return !tags.isEmpty() ? tags.stream().findFirst() : Optional.empty();
        } catch (Exception e) {
            throw new DbException("Error while get Tag with id = " + id + ": " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public Optional<Tag> getByName(String name) throws DbException {
        try {
            List<Tag> tags = jdbcTemplate.query(
                    TagSqlQueries.GET_TAG_BY_NAME,
                    ps -> ps.setString(1, name),
                    new TagRowMapper()
            );
            return !tags.isEmpty() ? tags.stream().findFirst() : Optional.empty();
        } catch (Exception e) {
            throw new DbException("Error while get Tag with name = " + name + ": " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public List<Tag> getAll() throws DbException {
        return getAll(null);
    }

    @Override
    public List<Tag> getAll(Sort sort) throws DbException {
        String sql = getOrderByClause(GET_ALL_TAGS, sort);
        try {
            return jdbcTemplate.query(sql, new TagRowMapper());
        } catch (Exception e) {
            throw new DbException("Error while getting all Tags: " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public boolean delete(long id) throws DbException {
        try {
            return (jdbcTemplate.update(TagSqlQueries.DELETE_TAG_BY_ID, id) > 0);
        } catch (Exception e) {
            throw new DbException("Error while deleting a Tag with id = " + id + ": " + Arrays.toString(e.getStackTrace()));
        }
    }
}
