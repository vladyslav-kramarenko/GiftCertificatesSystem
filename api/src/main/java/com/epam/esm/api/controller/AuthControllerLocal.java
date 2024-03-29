package com.epam.esm.api.controller;

import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.impl.auth.local.LocalAuthServiceImpl;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthControllerLocal {
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerLocal.class);
    private final AuthService authService;

    @Autowired
    public AuthControllerLocal(LocalAuthServiceImpl authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam @NotNull @Email String email,
            @RequestParam @NotNull String password
    ) throws Exception {
        return ResponseEntity.ok(authService.authenticateUser(email, password));
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(
            @RequestParam(name = "refreshToken") String refreshToken
    ) {
        authService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok().build();
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

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestParam(name = "refreshToken") String refreshToken
    ) throws Exception {
        logger.debug("receive refresh token: " + refreshToken);
        return ResponseEntity.ok(authService.validateRefreshToken(refreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            try {
                Map<String, Object> claims = new HashMap<>(jwt.getClaims());
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                if (!authorities.isEmpty()) {
                    List<String> authoritiesList = authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    claims.put("authorities", authoritiesList);
                }
                return ResponseEntity.ok(claims);
            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else if (principal instanceof UserDetails userDetails) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
