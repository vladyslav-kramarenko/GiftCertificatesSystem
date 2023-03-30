package com.epam.esm.dao;


import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    Tag create(Tag tag)throws DbException;

    Optional<Tag> getById(long id);
    Optional<Tag> getByName(String name);

    List<Tag> getAll();

    void delete(long id);
}