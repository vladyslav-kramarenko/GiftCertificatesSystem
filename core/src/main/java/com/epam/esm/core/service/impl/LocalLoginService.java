package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Profile("dev")
@Service
public class LocalLoginService implements AuthService {
    private final PasswordEncoderService passwordEncoderService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    @Autowired
    public LocalLoginService(
            UserService userService,
            PasswordEncoderService passwordEncoderService,
            JwtTokenService jwtTokenService) {
        this.passwordEncoderService = Objects.requireNonNull(passwordEncoderService);
        this.jwtTokenService = Objects.requireNonNull(jwtTokenService);
        this.userService = userService;
    }
    public ResponseEntity<String> authenticateUser(String email, String password) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoderService.matches(password, userOptional.get().getPassword())) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (userOptional.get().isAdmin()) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            else authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            String token = jwtTokenService.generateToken(createUserDetails(userOptional.get(), authorities));
            return ResponseEntity.ok().body(token);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }
    private UserDetails createUserDetails(User user, List<SimpleGrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public User registerUser(String email, String password, String firstName, String lastName) throws ServiceException {
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        String encodedPassword = passwordEncoderService.encode(password);
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        return userService.createUser(newUser);
    }
}
