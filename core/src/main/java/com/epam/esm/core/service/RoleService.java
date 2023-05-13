package com.epam.esm.core.service;

import com.epam.esm.core.entity.Role;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface RoleService {
    @Cacheable(value = "roles")
    List<Role> getAllRoles();


    @Cacheable(value = "role", key = "#name")
    Role getRoleByName(String name);
}
