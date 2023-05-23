package com.epam.esm.core.service.impl.auth.auth0;

import com.epam.esm.core.service.UserService;
import com.epam.esm.core.service.impl.auth.BaseJwtAuthConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.epam.esm.core.util.CoreConstants.*;

@Service
public class Auth0JwtAuthConverterService extends BaseJwtAuthConverterService {
    private final JwtGrantedAuthoritiesConverter authoritiesConverter;

    @Autowired
    public Auth0JwtAuthConverterService(UserService userService) {
        super(userService);
        this.authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix(ROLE_AUTHORITY_PREFIX);
        authoritiesConverter.setAuthoritiesClaimName("permissions");
    }

    public Collection<GrantedAuthority> convertAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
        addRolesFromClaim(authorities, jwt, AUTH0_ROLE_CLAIM);

        String email = jwt.getClaim(AUTH0_EMAIL_CLAIM);
        addRoleAndUserIdToAuthoritiesByEmail(authorities, email);
        return authorities;
    }
}
