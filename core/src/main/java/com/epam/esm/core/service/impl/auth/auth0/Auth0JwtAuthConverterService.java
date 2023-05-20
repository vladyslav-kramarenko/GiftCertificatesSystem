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

import static com.epam.esm.core.util.CoreConstants.AUTH0_EMAIL_CLAIM;

@Service
public class Auth0JwtAuthConverterService {

    private final UserService userService;
    private final JwtGrantedAuthoritiesConverter authoritiesConverter;
    @Autowired
    public Auth0JwtAuthConverterService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
        this.authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("permissions");
    }
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::convertAuthorities);
        return jwtAuthenticationConverter;
    }
    private Collection<GrantedAuthority> convertAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);

        String email = jwt.getClaim(AUTH0_EMAIL_CLAIM);
        addRoleToAuthoritiesByEmail(authorities,email);
//        addRoleAndUserIdToAuthoritiesByEmail(authorities,email);
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
    private void addRoleAndUserIdToAuthoritiesByEmail(Collection<GrantedAuthority> authorities, String email){
        if (email != null) {
            Optional<User> optionalUser = userService.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase()));
                authorities.add(new SimpleGrantedAuthority("USER_ID_" + user.getId()));
            }
        }
    }
}
