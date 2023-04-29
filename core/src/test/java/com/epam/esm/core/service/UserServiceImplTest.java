package com.epam.esm.core.service;

import com.epam.esm.core.CoreTestApplication;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = CoreTestApplication.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    public void getUserById_success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertEquals(user, foundUser.orElse(null));
    }

    @Test
    public void createUser_success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user, createdUser);
    }

    @Test
    public void deleteUser_success() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(anyLong());
    }

    @Test
    public void getUsers_success() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(user)));

        int page = 0;
        int size = 10;
        String[] sortParams = {"id","asc"};
        List<User> users = userService.getUsers(page, size, sortParams);

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }
}