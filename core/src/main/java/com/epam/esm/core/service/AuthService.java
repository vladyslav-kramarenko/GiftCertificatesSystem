package com.epam.esm.core.service;

import com.epam.esm.core.entity.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(String email, String password) throws Exception;

    ResponseEntity<?> validateRefreshToken(String token) throws Exception;

    User registerUser(String email, String password, String firstName, String lastName) throws Exception;
}
