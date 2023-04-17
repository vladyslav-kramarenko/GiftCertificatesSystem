package com.epam.esm.core.service;

import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;

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
     * @throws ServiceException if there was an error deleting the tag.
     */
    void deleteTag(Long id) throws ServiceException;


    List<Tag> getTags(int page, int size, String[] sortParams) throws ServiceException;
}
