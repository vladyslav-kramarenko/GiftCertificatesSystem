package com.epam.esm.dao.impl;

import com.epam.esm.exception.DbException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.Utilities.getOrderByClause;

public abstract class AbstractDao<T, ID> {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<T> rowMapper;

    public AbstractDao(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public abstract T create(T obj) throws DbException;
    public Optional<T> getById(ID id) throws DbException {
        try {
            String sql = getSelectByIdSql();
            T entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(entity);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DbException("Error while getting entity with id = " + id + ": " + Arrays.toString(e.getStackTrace()));
        }
    }

    public boolean delete(ID id) throws DbException {
        try {
            return (jdbcTemplate.update(getDeleteByIdSql(), id) > 0);
        } catch (Exception e) {
            throw new DbException("Error while deleting entity with id = " + id + ": " + Arrays.toString(e.getStackTrace()));
        }
    }

    protected abstract String getSelectByIdSql();

    protected abstract String getSelectAllSql();

    protected abstract String getDeleteByIdSql();

    public List<T> getAll() throws DbException {
        return getAll(null);
    }

    public List<T> getAll(Sort sort) throws DbException {
        String sql = getOrderByClause(getSelectAllSql(), sort);
        try {
            return jdbcTemplate.query(sql, rowMapper);
        } catch (Exception e) {
            throw new DbException("Error while getting all entities: " + Arrays.toString(e.getStackTrace()));
        }
    }

    public Optional<T> getByStringAttribute(String attribute, String query) throws DbException {
        try {
            List<T> tags = jdbcTemplate.query(
                    query,
                    ps -> ps.setString(1, attribute),
                    rowMapper
            );
            return !tags.isEmpty() ? tags.stream().findFirst() : Optional.empty();
        } catch (Exception e) {
            throw new DbException("Error while get entity with attribute = " + attribute + ": " + Arrays.toString(e.getStackTrace()));
        }
    }

    public abstract Optional<T> update(T obj) throws DbException;

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}