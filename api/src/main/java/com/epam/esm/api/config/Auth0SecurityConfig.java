package com.epam.esm.api.config;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


import java.util.*;

@Profile("prod")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Auth0SecurityConfig extends BaseSecurityConfig {
    private final UserService userService;

    @Autowired
    public Auth0SecurityConfig(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
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
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                )
                );
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::convertAuthorities);
        return jwtAuthenticationConverter;
    }

    private Collection<GrantedAuthority> convertAuthorities(Jwt jwt) {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("permissions");
        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        String email = jwt.getClaim("https://gift-certificates-system-api/email");
        if (email != null) {
            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase()));
            }
        }
        return authorities;
    }
}