package com.epam.esm.api.config;

import com.epam.esm.core.service.impl.auth.DelegatingJwtAuthenticationConverter;
import com.epam.esm.core.service.impl.auth.DelegatingJwtDecoder;
import com.epam.esm.core.service.impl.auth.auth0.Auth0JwtAuthConverterService;
import com.epam.esm.core.service.impl.auth.local.LocalJwtAuthConverterService;
import com.epam.esm.core.service.impl.auth.local.LocalJwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.epam.esm.core.util.CoreConstants.AUTH0_TOKEN_ISSUER;
import static com.epam.esm.core.util.CoreConstants.GIFT_CERTIFICATE_SERVICE_TOKEN_ISSUER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends BaseSecurityConfig {
    @Value("${auth0.domain}")
    private String auth0Domain;
    private final Auth0JwtAuthConverterService authJwtConverterService;
    private final LocalJwtAuthConverterService localJwtConverterService;
    private final LocalJwtTokenService localJwtTokenService;

    @Autowired
    public SecurityConfig(Auth0JwtAuthConverterService authJwtConverterService, LocalJwtAuthConverterService localJwtConverterService, LocalJwtTokenService localJwtTokenService) {
        this.authJwtConverterService = Objects.requireNonNull(authJwtConverterService, "Auth0JwtAuthConverterService must be initialised");
        this.localJwtConverterService = Objects.requireNonNull(localJwtConverterService, "Auth0JwtAuthConverterService must be initialised");
        this.localJwtTokenService = Objects.requireNonNull(localJwtTokenService, "LocalJwtTokenService must be initialised");
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
                                                .decoder(delegatingJwtDecoder())
                                                .jwtAuthenticationConverter(delegatingJwtAuthenticationConverter())
                                ));
        return http.build();
    }

    @Bean
    public DelegatingJwtDecoder delegatingJwtDecoder() {
        Map<String, JwtDecoder> decoders = new HashMap<>();
        decoders.put(AUTH0_TOKEN_ISSUER, auth0JwtDecoder());
        decoders.put(GIFT_CERTIFICATE_SERVICE_TOKEN_ISSUER, localJwtDecoder());
        return new DelegatingJwtDecoder(decoders);
    }

    @Bean
    public DelegatingJwtAuthenticationConverter delegatingJwtAuthenticationConverter() {
        Map<String, Converter<Jwt, AbstractAuthenticationToken>> converters = new HashMap<>();
        converters.put(AUTH0_TOKEN_ISSUER, authJwtConverterService.jwtAuthenticationConverter());
        converters.put(GIFT_CERTIFICATE_SERVICE_TOKEN_ISSUER, localJwtConverterService.jwtAuthenticationConverter());
        return new DelegatingJwtAuthenticationConverter(converters);
    }

    private JwtDecoder auth0JwtDecoder() {
        String jwkSetUri = "https://" + auth0Domain + "/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    private JwtDecoder localJwtDecoder() {
        SecretKey secretKey = (SecretKey) localJwtTokenService.getKey();
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
