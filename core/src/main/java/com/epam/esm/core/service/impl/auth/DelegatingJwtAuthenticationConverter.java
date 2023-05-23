package com.epam.esm.core.service.impl.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class DelegatingJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Map<String, Converter<Jwt, AbstractAuthenticationToken>> converters;

    public DelegatingJwtAuthenticationConverter(Map<String, Converter<Jwt, AbstractAuthenticationToken>> converters) {
        this.converters = converters;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String issuer = jwt.getClaimAsString("iss");
        Converter<Jwt, AbstractAuthenticationToken> converter = converters.get(issuer);
        if (converter == null) {
            throw new IllegalArgumentException("No converter found for issuer: " + jwt.getIssuer());
        }
        return converter.convert(jwt);
    }
}
