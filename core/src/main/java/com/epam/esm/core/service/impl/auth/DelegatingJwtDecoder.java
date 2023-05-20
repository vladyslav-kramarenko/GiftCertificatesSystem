package com.epam.esm.core.service.impl.auth;

import com.nimbusds.jose.shaded.gson.Gson;
import io.jsonwebtoken.JwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Base64;
import java.util.Map;

public class DelegatingJwtDecoder implements JwtDecoder {
    private final Map<String, JwtDecoder> decoders;

    public DelegatingJwtDecoder(Map<String, JwtDecoder> decoders) {
        this.decoders = decoders;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        String issuer = getIssuer(token);
        JwtDecoder decoder = decoders.get(issuer);
        if (decoder == null) {
            throw new JwtException("No decoder registered for issuer: " + issuer);
        }
        return decoder.decode(token);
    }

    private String getIssuer(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        Map<String, Object> claims = new Gson().fromJson(payload, Map.class);
        return (String) claims.get("iss");
    }
}
