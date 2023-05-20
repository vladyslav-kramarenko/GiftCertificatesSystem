package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.core.util.CoreConstants.*;

@Service
public class SecurityService {
    private final UserService userService;

    @Autowired
    public SecurityService(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    public boolean isUserAllowedToGetInfo(Long userIdFromRequest, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Map<String, Object> claims = jwt.getClaims();
            String issuer = (String) claims.get("iss");

            if (issuer.equals(AUTH0_TOKEN_ISSUER)) {
                return isUserAllowedFromAuth0Token(userIdFromRequest, claims);
            } else if (issuer.equals(GIFT_CERTIFICATE_SERVICE_TOKEN_ISSUER)) {
                return isUserAllowedFromLocalToken(userIdFromRequest, claims);
            }
        }
        return false;
    }

    private boolean isUserAllowedFromAuth0Token(Long userIdFromRequest, Map<String, Object> claims) {
        String userEmail = (String) claims.get(AUTH0_EMAIL_CLAIM);
        return isUserAllowedFromUserEmail(userIdFromRequest, userEmail);
    }

    private boolean isUserAllowedFromLocalToken(Long userIdFromRequest, Map<String, Object> claims) {
        String userEmail = (String) claims.get("sub");
        ArrayList<String> roles = (ArrayList<String>) claims.get("roles");
        boolean isAdminOrManager = roles.stream()
                .anyMatch(role -> role.equals("ROLE_MANAGER") || role.equals("ROLE_ADMIN"));
        if (isAdminOrManager) {
            return true;
        }
        Long userIdFromToken = (Long) claims.get("user_id");
        if (userIdFromToken == null) {
            return isUserAllowedFromUserEmail(userIdFromRequest, userEmail);
        } else
            return userIdFromRequest.equals(userIdFromToken);
    }

    public boolean isUserAllowedFromUserEmail(Long userIdFromRequest, String userEmail) {
        if (!userEmail.isEmpty()) {
            Optional<User> optionalUser = userService.findByEmail(userEmail);
            if (optionalUser.isPresent()) {
                String role = optionalUser.get().getRole().getName();
                boolean isAdminOrManager = role.equals("MANAGER") || role.equals("ROLE_ADMIN");
                if (isAdminOrManager) {
                    return true;
                }
                Long userIdFromDB = optionalUser.get().getId();
                return userIdFromRequest.equals(userIdFromDB);
            }
        }
        return false;
    }
}
