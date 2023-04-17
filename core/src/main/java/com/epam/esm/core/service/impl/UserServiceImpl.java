package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.UserOrderRepository;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.TagService;
import com.epam.esm.core.service.UserService;
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
import static com.epam.esm.core.util.UserUtils.validateUser;
import static com.epam.esm.core.util.Utilities.validateId;

/**
 * Implementation of the {@link TagService} interface that provides the business logic for working with tags.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserOrderRepository orderRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> getUserById(Long id) throws ServiceException {
        validateId(id);
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Hibernate.initialize(user.getOrders());
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while get user with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) throws ServiceException {
        validateUser(user);
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while creating a user");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long id) throws ServiceException {
        validateId(id);
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while deleting user with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers(int page, int size, String[] sortParams) throws ServiceException {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_USER_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));

        try {
            return userRepository.findAll(pageable).toList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while getting all users");
        }
    }
}
