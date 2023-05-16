package com.epam.esm.api.config;

import com.epam.esm.core.service.impl.Auth0JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


import java.util.*;

@Profile("prod")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Auth0SecurityConfig extends BaseSecurityConfig {
    private final Auth0JwtTokenService auth0JwtAuthenticationConverter;

    @Autowired
    public Auth0SecurityConfig(Auth0JwtTokenService auth0JwtAuthenticationConverter) {
        this.auth0JwtAuthenticationConverter = Objects.requireNonNull(auth0JwtAuthenticationConverter, "UserService must be initialised");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureCommon(http);
        http.authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .jwt(jwtConfigurer ->
                                        jwtConfigurer
                                                .jwtAuthenticationConverter(auth0JwtAuthenticationConverter.jwtAuthenticationConverter())
                                )
                );
        return http.build();
    }
}