package com.epam.esm.api.controller;

import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.impl.auth.auth0.Auth0AuthServiceImpl;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth0")
public class AuthControllerAuth0 {
    private final AuthService authService;

    @Autowired
    public AuthControllerAuth0(Auth0AuthServiceImpl authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam @NotNull @Email String email,
            @RequestParam @NotNull String password
    ) throws Exception {
        return authService.authenticateUser(email, password);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(
            @RequestParam(name = "email") @Email @NotNull String email,
            @RequestParam(name = "password") @NotNull String password,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(email, password, firstName, lastName));
    }
}
