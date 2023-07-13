package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.RoleService;
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
 * Implementation of the {@link UserService} interface that provides the business logic for working with users.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository must be initialised");
        this.roleService = Objects.requireNonNull(roleService, "RoleService must be initialised");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) {
        user.setRole(roleService.getRoleByName(ROLE_USER));
        return userRepository.save(user);
    }

    @Override
    public User createUser(String auth0UserId, String email, String firstName, String lastName) {
        User user = new User();
        user.setAuth0UserId(auth0UserId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(roleService.getRoleByName(ROLE_USER));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByAuth0UserId(String auth0UserId) {
        return userRepository.findByAuth0UserId(auth0UserId);
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
