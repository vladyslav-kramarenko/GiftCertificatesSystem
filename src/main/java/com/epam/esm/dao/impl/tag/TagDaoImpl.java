package com.epam.esm.dao.impl.tag;

import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
            tag.setId((Long) keyHolder.getKeys().get("id"));
            return tag;
        } catch (DuplicateKeyException e) {
            throw new DbException("Tag with name '" + tag.getName() + "' already exists");
        }
    }

    public Optional<Tag> getById(long id) {
        List<Tag> tags = jdbcTemplate.query(
                TagSqlQueries.GET_TAG_BY_ID,
                ps -> ps.setLong(1, id),
                new TagRowMapper()
        );
        return tags.stream().findFirst();
    }

    @Override
    public Optional<Tag> getByName(String name) {
        List<Tag> tags = jdbcTemplate.query(
                TagSqlQueries.GET_TAG_BY_NAME,
                ps -> ps.setString(1, name),
                new TagRowMapper()
        );
        return tags.stream().findFirst();
    }

    public List<Tag> getAll() {
        return jdbcTemplate.query(
                TagSqlQueries.GET_ALL_TAGS,
                new TagRowMapper()
        );
    }

    public void delete(long id) {
        jdbcTemplate.update(TagSqlQueries.DELETE_TAG_BY_ID, id);
    }
}
