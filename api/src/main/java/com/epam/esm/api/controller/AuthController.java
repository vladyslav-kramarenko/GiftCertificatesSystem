package com.epam.esm.api.controller;

import com.auth0.exception.Auth0Exception;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.service.UserService;
import com.epam.esm.core.service.impl.Auth0LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final Auth0LoginService auth0LoginService;

    @Autowired
    public AuthController(UserService userService, Auth0LoginService auth0LoginService) {
        this.userService = Objects.requireNonNull(userService);
        this.auth0LoginService = Objects.requireNonNull(auth0LoginService);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) throws JSONException, Auth0Exception {
        return auth0LoginService.authenticateUser(email, password);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName
    ) throws JSONException {
        String auth0UserId = "";
        try {
            auth0UserId = auth0LoginService.registerUser(email, password, firstName, lastName);

            User newUser = userService.createUser(auth0UserId, email, firstName, lastName);

            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            if (!auth0UserId.isEmpty()) auth0LoginService.deleteUser(auth0UserId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
        }
    }
}
