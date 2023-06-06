package com.epam.esm.core.service;

import com.epam.esm.core.service.impl.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;

import static com.epam.esm.core.util.CoreConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityServiceTest {

    private SecurityService securityService;
    private SimpleGrantedAuthority authorityAdmin;
    private SimpleGrantedAuthority authorityManager;
    private SimpleGrantedAuthority authorityUser;
    private SimpleGrantedAuthority authorityID;
    private final String login = "user";
    private final String password = "password";

    @BeforeEach
    public void setup() {
        authorityAdmin = new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + ROLE_ADMIN);
        authorityManager = new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + ROLE_MANAGER);
        authorityUser = new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + ROLE_USER);
        authorityID = new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + USER_ID_AUTHORITY_PREFIX + 1);
        securityService = new SecurityService();
    }

    @Test
    public void testAllowedByIdOrManagerOrAdmin() {
        Authentication authAdmin = new UsernamePasswordAuthenticationToken(login, password, Collections.singletonList(authorityAdmin));
        assertTrue(securityService.allowedByIdOrManagerOrAdmin(1L, authAdmin));

        Authentication authManager = new UsernamePasswordAuthenticationToken(login, password, Collections.singletonList(authorityManager));
        assertTrue(securityService.allowedByIdOrManagerOrAdmin(1L, authManager));

        Authentication authUser = new UsernamePasswordAuthenticationToken(login, password, Arrays.asList(authorityUser, authorityID));

        assertTrue(securityService.allowedByIdOrManagerOrAdmin(1L, authUser));
        assertFalse(securityService.allowedByIdOrManagerOrAdmin(2L, authUser));
    }

    @Test
    public void testAllowedByIdOrAdmin() {
        Authentication authAdmin = new UsernamePasswordAuthenticationToken(login, password, Collections.singletonList(authorityAdmin));
        assertTrue(securityService.allowedByIdOrAdmin(1L, authAdmin));

        Authentication authUser = new UsernamePasswordAuthenticationToken(login, password, Arrays.asList(authorityUser, authorityID));
        assertTrue(securityService.allowedByIdOrAdmin(1L, authUser));
        assertFalse(securityService.allowedByIdOrAdmin(2L, authUser));
    }
}
