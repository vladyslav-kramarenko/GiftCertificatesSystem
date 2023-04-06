package com.epam.esm.dao;


import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * Data access object for {@link Tag} entity.
 */
public interface TagDao {

    /**
     * Creates a new tag in the database.
     *
     * @param tag the tag to create.
     * @return the created tag.
     * @throws DbException if an error occurred while accessing the database.
     */
    Tag create(Tag tag) throws DbException;

    /**
     * Retrieves a tag by its ID from the database.
     *
     * @param id the ID of the tag to retrieve.
     * @return an {@code Optional} containing the retrieved tag, or an empty {@code Optional} if no tag was found.
     * @throws DbException if an error occurred while accessing the database.
     */
    Optional<Tag> getById(Long id) throws DbException;

    /**
     * Retrieves a tag by its name from the database.
     *
     * @param name the name of the tag to retrieve.
     * @return an {@code Optional} containing the retrieved tag, or an empty {@code Optional} if no tag was found.
     * @throws DbException if an error occurred while accessing the database.
     */
    Optional<Tag> getByName(String name) throws DbException;

    /**
     * Retrieves all tags from the database.
     *
     * @return a list of all tags.
     * @throws DbException if an error occurred while accessing the database.
     */
    List<Tag> getAll() throws DbException;

    /**
     * Retrieves all tags from the database, sorted according to the specified {@code Sort} object.
     *
     * @param sort the sorting criteria.
     * @return a list of all tags, sorted according to the specified criteria.
     * @throws DbException if an error occurred while accessing the database.
     */
    List<Tag> getAll(Sort sort) throws DbException;

    /**
     * Deletes a tag by its ID from the database.
     *
     * @param id the ID of the tag to delete.
     * @return {@code true} if the tag was deleted, {@code false} if no tag was found with the specified ID.
     * @throws DbException if an error occurred while accessing the database.
     */
    boolean delete(Long id) throws DbException;
}