package com.epam.esm.core.service;

import com.epam.esm.core.service.impl.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncoderServiceTest {

    private PasswordEncoderService passwordEncoderService;

    @BeforeEach
    public void setUp() {
        passwordEncoderService = new PasswordEncoderService();
    }

    @Test
    public void testEncode() {
        String rawPassword = "password";
        String encodedPassword = passwordEncoderService.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(new BCryptPasswordEncoder().matches(rawPassword, encodedPassword));
    }

    @Test
    public void testMatches() {
        String rawPassword = "password";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        assertTrue(passwordEncoderService.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoderService.matches("wrongPassword", encodedPassword));
    }
}
