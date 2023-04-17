package com.epam.esm.core.service;

import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * A service interface for performing CRUD operations on tags in the system.
 */
public interface OrderService {

    /**
     * Retrieves a tag by ID.
     *
     * @param id the ID of the order to retrieve.
     * @return an Optional containing the order with the specified ID, or an empty Optional if no such tag exists.
     * @throws ServiceException if there was an error retrieving the order.
     */
    Optional<UserOrder> getOrderById(Long id) throws ServiceException;

    /**
     * Creates a new tag.
     *
     * @param order the tag to create.
     * @return the created tag.
     * @throws ServiceException if there was an error creating the tag.
     */
    UserOrder createOrder(UserOrder order) throws ServiceException;

    /**
     * Deletes a tag by ID.
     *
     * @param id the ID of the tag to delete.
     * @throws ServiceException if there was an error deleting the tag.
     */
    void deleteOrder(Long id) throws ServiceException;


    List<UserOrder> getOrders(int page, int size, String[] sortParams) throws ServiceException;
}
