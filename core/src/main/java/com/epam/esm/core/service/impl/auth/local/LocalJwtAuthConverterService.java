package com.epam.esm.core.service.impl.auth.local;

import com.epam.esm.core.service.UserService;
import com.epam.esm.core.service.impl.auth.BaseJwtAuthConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.epam.esm.core.util.CoreConstants.*;

@Service
public class LocalJwtAuthConverterService extends BaseJwtAuthConverterService {
    private static final Logger logger = LoggerFactory.getLogger(LocalJwtAuthConverterService.class);
    public LocalJwtAuthConverterService(UserService userService) {
        super(userService);
    }

    public Collection<GrantedAuthority> convertAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Long userId = jwt.getClaim(GIFT_CERTIFICATE_SERVICE_USER_ID_CLAIM);
        if (userId == null) {
            logger.info("user_id claim is null => searching user id by email");
            String email = jwt.getSubject();
            addRoleAndUserIdToAuthoritiesByEmail(authorities, email);
        }
        addRolesFromClaim(authorities, jwt, GIFT_CERTIFICATE_SERVICE_ROLES_CLAIM);

        return authorities;
    }
}