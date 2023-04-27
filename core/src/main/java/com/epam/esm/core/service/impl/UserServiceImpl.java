package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.TagService;
import com.epam.esm.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.core.util.CoreConstants.*;
import static com.epam.esm.core.util.SortUtilities.createSort;

/**
 * Implementation of the {@link TagService} interface that provides the business logic for working with tags.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository must be initialised");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers(int page, int size, String[] sortParams) {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_USER_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));
        return userRepository.findAll(pageable).toList();
    }
}
