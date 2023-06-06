package com.epam.esm.core.service;

import com.epam.esm.core.entity.Role;
import com.epam.esm.core.repository.RoleRepository;
import com.epam.esm.core.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static com.epam.esm.core.util.CoreConstants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class RoleServiceImplTest {

    private RoleServiceImpl roleService;
    private Role role1;
    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        roleService = new RoleServiceImpl(roleRepository);
        role1 = new Role();
        role1.setId(1L);
        role1.setName(ROLE_AUTHORITY_PREFIX + ROLE_USER);
    }

    @Test
    public void testGetAllRoles() {
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(ROLE_AUTHORITY_PREFIX + ROLE_ADMIN);

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> roles = roleService.getAllRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
    }

    @Test
    public void testGetRoleByName() {

        when(roleRepository.findByName(anyString())).thenReturn(role1);

        Role role = roleService.getRoleByName(ROLE_AUTHORITY_PREFIX + ROLE_USER);

        assertNotNull(role);
        assertEquals(ROLE_AUTHORITY_PREFIX + ROLE_USER, role.getName());
    }
}

