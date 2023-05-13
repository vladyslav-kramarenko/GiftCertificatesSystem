package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.Role;
import com.epam.esm.core.repository.RoleRepository;
import com.epam.esm.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    @Cacheable(value = "roles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Cacheable(value = "role", key = "#name")
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
