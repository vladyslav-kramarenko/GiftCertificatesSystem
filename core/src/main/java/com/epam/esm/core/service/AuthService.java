package com.epam.esm.core.service;

import com.epam.esm.core.entity.AuthenticationResponse;
import com.epam.esm.core.entity.User;

public interface AuthService {
    AuthenticationResponse authenticateUser(String email, String password) throws Exception;

    AuthenticationResponse validateRefreshToken(String token) throws Exception;

    User registerUser(String email, String password, String firstName, String lastName) throws Exception;

    void deleteRefreshToken(String refreshToken);
}
