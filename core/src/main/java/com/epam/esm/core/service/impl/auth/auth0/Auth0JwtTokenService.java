package com.epam.esm.core.service.impl.auth.auth0;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
@Service
public class Auth0JwtTokenService {
    private final UserService userService;

    @Autowired
    public Auth0JwtTokenService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
    }

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
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
        addRoleToAuthoritiesByEmail(authorities,email);
        return authorities;
    }

    private void addRoleToAuthoritiesByEmail(Collection<GrantedAuthority> authorities, String email){
        if (email != null) {
            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase()));
            }
        }
    }
}
