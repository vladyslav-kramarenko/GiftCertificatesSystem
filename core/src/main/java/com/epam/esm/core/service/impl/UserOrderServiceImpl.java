package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.UserOrderRepository;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.OrderService;
import com.epam.esm.core.service.TagService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.core.util.CoreConstants.*;
import static com.epam.esm.core.util.SortUtilities.createSort;
import static com.epam.esm.core.util.Utilities.validateId;

/**
 * Implementation of the {@link TagService} interface that provides the business logic for working with tags.
 */
@Service
public class UserOrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(UserOrderServiceImpl.class);
    private final UserRepository userRepository;
    private final UserOrderRepository orderRepository;

    @Autowired
    public UserOrderServiceImpl(UserRepository userRepository, UserOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserOrder> getOrderById(Long id) throws ServiceException {
        validateId(id);
        try {
            Optional<UserOrder> orderOpt = orderRepository.findById(id);
            if (orderOpt.isPresent()) {
                UserOrder order = orderOpt.get();
                Hibernate.initialize(order.getOrderGiftCertificates());
                return Optional.of(order);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while get order with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserOrder createOrder(UserOrder order) throws ServiceException {
//        validateOrder(order);
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while creating an order");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOrder(Long id) throws ServiceException {
        validateId(id);
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while deleting order with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserOrder> getOrders(int page, int size, String[] sortParams) throws ServiceException {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_ORDER_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));

        try {
            return orderRepository.findAll(pageable).toList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while getting all orders");
        }
    }
}
