package com.epam.esm.dao;


import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    Tag create(Tag tag)throws DbException;

    Optional<Tag> getById(long id) throws DbException;
    Optional<Tag> getByName(String name) throws DbException;

    List<Tag> getAll() throws DbException;
    List<Tag> getAll(Sort sort) throws DbException;

    boolean delete(long id) throws DbException;
}