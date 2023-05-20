package com.epam.esm.core.service.impl.auth.local;

import com.epam.esm.core.dto.CustomUserDetails;
import com.epam.esm.core.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.esm.core.util.CoreConstants.*;

@Service
public class LocalJwtTokenService {
    private static final Logger logger = LoggerFactory.getLogger(LocalJwtTokenService.class);
    protected Key key;
    public LocalJwtTokenService() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    public Key getKey() {
        return key;
    }

    public String generateToken(User user, List<SimpleGrantedAuthority> userAuthorities) {
        long expirationTimeLong = 1 * ONE_HOUR;
        return generateToken(user, userAuthorities, expirationTimeLong);
    }
    public String generateToken(User user, List<SimpleGrantedAuthority> userAuthorities, long expirationTimeLong) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getEmail());
        List<String> authorities = userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", authorities);
        claims.put("user_id", user.getId());
        Date now = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(now.getTime() + expirationTimeLong);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(GIFT_CERTIFICATE_SERVICE_TOKEN_ISSUER)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token has expired");
        } catch (UnsupportedJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported JWT token");
        } catch (MalformedJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Malformed JWT token");
        }
    }
    public String generateRefreshToken(User user, List<SimpleGrantedAuthority> userAuthorities) {
        long expirationTimeLong = 7 * ONE_DAY;
        return generateToken(user, userAuthorities, expirationTimeLong);
    }
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String email = claims.getSubject();
        Long userId = 0L;
        try {
            userId = claims.get("user_id", Long.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("roles")).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());
        UserDetails userDetails = new CustomUserDetails(email, "", authorities, userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
