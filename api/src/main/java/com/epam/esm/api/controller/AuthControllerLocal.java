package com.epam.esm.api.controller;

import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.impl.auth.local.LocalAuthServiceImpl;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthControllerLocal {
    private final AuthService authService;

    @Autowired
    public AuthControllerLocal(LocalAuthServiceImpl authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
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

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return ResponseEntity.ok(jwt.getClaims());
        } else if (principal instanceof UserDetails userDetails) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
