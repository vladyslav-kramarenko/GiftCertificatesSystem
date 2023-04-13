package com.epam.esm.dao.impl.tag;

import com.epam.esm.dao.impl.AbstractDao;
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
import java.util.Arrays;
import java.util.Optional;

import static com.epam.esm.dao.impl.tag.TagSqlQueries.*;
import static com.epam.esm.util.Utilities.getKey;

@Repository
public class TagDaoImpl extends AbstractDao<Tag, Long> implements TagDao {

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new TagRowMapper());
    }

    @Override
    public Tag create(Tag tag) throws DbException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        TagSqlQueries.CREATE_TAG,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, tag.name());
                return ps;
            }, keyHolder);
//            tag.setId(getKey(keyHolder));
            return new Tag(getKey(keyHolder), tag.name());
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Tag with name '" + tag.name() + "' already exists");
        } catch (Exception e) {
            throw new DbException("Error while creating a Tag" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected String getSelectByIdSql() {
        return GET_TAG_BY_ID;
    }

    @Override
    protected String getSelectAllSql() {
        return GET_ALL_TAGS;
    }

    @Override
    protected String getDeleteByIdSql() {
        return DELETE_TAG_BY_ID;
    }

    @Override
    public Optional<Tag> getByName(String name) throws DbException {
        return getByStringAttribute(name, TagSqlQueries.GET_TAG_BY_NAME);
    }

    @Override
    public Optional<Tag> update(Tag obj) throws DbException {
        throw new DbException("Update method for tags is not supported");
    }
}
