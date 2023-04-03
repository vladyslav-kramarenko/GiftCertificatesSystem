package com.epam.esm.dao;


import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    Tag create(Tag tag)throws DbException;

    Optional<Tag> getById(long id) throws DbException;
    Optional<Tag> getByName(String name) throws DbException;

    List<Tag> getAll() throws DbException;

    void delete(long id) throws DbException;
}