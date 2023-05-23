package com.epam.esm.core.service.impl.auth;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.core.util.CoreConstants.ROLE_AUTHORITY_PREFIX;
import static com.epam.esm.core.util.CoreConstants.USER_ID_AUTHORITY_PREFIX;

public abstract class BaseJwtAuthConverterService {
    protected final UserService userService;

    public BaseJwtAuthConverterService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
    }

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::convertAuthorities);
        return jwtAuthenticationConverter;
    }

    public abstract Collection<GrantedAuthority> convertAuthorities(Jwt jwt);

    protected void addRoleAndUserIdToAuthoritiesByEmail(Collection<GrantedAuthority> authorities, String email) {
        if (email != null) {
            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                authorities.add(new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + user.getRole().getName().toUpperCase()));
                authorities.add(new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + USER_ID_AUTHORITY_PREFIX + user.getId()));
            }
        }
    }

    protected void addRolesFromClaim(Collection<GrantedAuthority> authorities, Jwt jwt, String claimName) {
        List<String> roles = jwt.getClaim(claimName);
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(ROLE_AUTHORITY_PREFIX + role.toUpperCase()));
        }
    }
}
