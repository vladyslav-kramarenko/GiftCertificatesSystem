package com.epam.esm.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String idToken;

    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
