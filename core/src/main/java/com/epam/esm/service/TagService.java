package com.epam.esm.service;

import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

/**
 * A service interface for performing CRUD operations on tags in the system.
 */
public interface TagService {

    /**
     * Retrieves a tag by ID.
     *
     * @param id the ID of the tag to retrieve.
     * @return an Optional containing the tag with the specified ID, or an empty Optional if no such tag exists.
     * @throws ServiceException if there was an error retrieving the tag.
     */
    Optional<Tag> getTagById(Long id) throws ServiceException;

    /**
     * Creates a new tag.
     *
     * @param tag the tag to create.
     * @return the created tag.
     * @throws ServiceException if there was an error creating the tag.
     */
    Tag createTag(Tag tag) throws ServiceException;

    /**
     * Deletes a tag by ID.
     *
     * @param id the ID of the tag to delete.
     * @return true if the tag was deleted, false otherwise.
     * @throws ServiceException if there was an error deleting the tag.
     */
    boolean deleteTag(Long id) throws ServiceException;

    /**
     * Retrieves a list of all tags, sorted according to the specified sort parameters.
     *
     * @param sortParams an array of sort parameters, specifying the order in which to sort the tags.
     * @return a list of all tags, sorted according to the specified sort parameters.
     * @throws ServiceException if there was an error retrieving the tags.
     */
    List<Tag> getTags(String[] sortParams) throws ServiceException;
}
