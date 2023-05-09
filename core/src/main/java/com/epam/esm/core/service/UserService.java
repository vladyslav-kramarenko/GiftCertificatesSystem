package com.epam.esm.core.service;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * A service interface for performing CRUD operations on users in the system.
 */
public interface UserService {

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve.
     * @return an Optional containing the tag with the specified ID, or an empty Optional if no such user exists.
     * @throws ServiceException if there was an error retrieving the user.
     */
    Optional<User> getUserById(Long id) throws ServiceException;

    Optional<User> findByEmail(String email);

    /**
     * Creates a new tag.
     *
     * @param user the tag to create.
     * @return the created tag.
     * @throws ServiceException if there was an error creating the user.
     */
    User createUser(User user) throws ServiceException;

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete.
     * @throws ServiceException if there was an error deleting the user.
     */
    void deleteUser(Long id) throws ServiceException;

    User createUser(String auth0UserId, String email, String firstName, String lastName);

    Optional<User> findByAuth0UserId(String auth0UserId);

    List<User> getUsers(int page, int size, String[] sortParams) throws ServiceException;
}
