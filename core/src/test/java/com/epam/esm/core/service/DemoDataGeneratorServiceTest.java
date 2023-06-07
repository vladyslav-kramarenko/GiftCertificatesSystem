package com.epam.esm.core.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.impl.DemoDataGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class DemoDataGeneratorServiceTest {

    private DemoDataGeneratorService demoDataService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private GiftCertificateRepository giftCertificateRepository;
    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        demoDataService = new DemoDataGeneratorService(
                userRepository,
                tagRepository,
                giftCertificateRepository,
                orderService
        );
    }

    @Test
    public void generateUsersTest() {
        int userCount = 5;
        Set<User> result = demoDataService.generateUsers(userCount);
        assertEquals(userCount, result.size());
    }

    @Test
    public void generateTagsTest() {
        int tagsCount = 5;
        List<Tag> existingTags = new ArrayList<>();
        Set<Tag> result;
        result = demoDataService.generateTags(tagsCount, existingTags);
        assertEquals(tagsCount, result.size());
    }

    @Test
    public void saveEntitiesBatchTest() {
        List<User> users = demoDataService.generateUsers(5).stream().toList();
        int batchSize = 2;

        List<User>bunch1=users.subList(0, 2);
        when(userRepository.saveAll(bunch1)).thenReturn(bunch1);
        List<User>bunch2=users.subList(2, 4);
        when(userRepository.saveAll(bunch2)).thenReturn(bunch2);
        List<User>bunch3=users.subList(4, 5);
        when(userRepository.saveAll(bunch3)).thenReturn(bunch3);

        List<User> result = demoDataService.saveEntitiesBatch(userRepository, users, batchSize);
        verify(userRepository, times(3)).saveAll(anyList());  // Should be called three times: 2 batches of 2 and 1 batch of 1
        assertEquals(users.size(), result.size());
    }

    @Test
    public void generateUserEmailsTest() {
        int n = 5;
        List<User> users = demoDataService.generateUsers(5).stream().toList();

        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        int result = demoDataService.generateUserEmails(n);

        verify(userRepository, times(n)).save(any(User.class));  // Verify that userRepository.save was called n times
        assertEquals(n, result);
        for (User user : users) {
            assertNotNull(user.getEmail());  // Assert that all users have been updated with an email
        }
    }
}

