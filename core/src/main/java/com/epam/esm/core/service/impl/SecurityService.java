package com.epam.esm.core.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.epam.esm.core.util.CoreConstants.*;

@Service
public class SecurityService {

    public boolean allowedByIdOrManagerOrAdmin(Long userIdFromRequest, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return isAdmin(authorities) || isManager(authorities) || isUserIdInAuthorities(userIdFromRequest, authorities);
    }

    public boolean allowedByIdOrAdmin(Long userIdFromRequest, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return isAdmin(authorities) || isUserIdInAuthorities(userIdFromRequest, authorities);
    }

    private boolean isUserIdInAuthorities(Long userIdFromRequest, Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .anyMatch(role -> role.getAuthority().equals(ROLE_AUTHORITY_PREFIX + USER_ID_AUTHORITY_PREFIX + userIdFromRequest));
    }

    private boolean isAdmin(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .anyMatch(role -> role.getAuthority().equals(ROLE_AUTHORITY_PREFIX + ROLE_ADMIN));
    }

    private boolean isManager(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .anyMatch(role -> role.getAuthority().equals(ROLE_AUTHORITY_PREFIX + ROLE_MANAGER));
    }
}
