package com.epam.esm.core.service.impl.auth.local;

import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.UserService;
import com.epam.esm.core.service.impl.PasswordEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LocalAuthServiceImpl implements AuthService {
    private final PasswordEncoderService passwordEncoderService;
    private final LocalJwtTokenService jwtTokenService;
    private final UserService userService;

    @Autowired
    public LocalAuthServiceImpl(
            UserService userService,
            PasswordEncoderService passwordEncoderService,
            LocalJwtTokenService jwtTokenService) {
        this.passwordEncoderService = Objects.requireNonNull(passwordEncoderService);
        this.jwtTokenService = Objects.requireNonNull(jwtTokenService);
        this.userService = userService;
    }

    public ResponseEntity<String> authenticateUser(String email, String password) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoderService.matches(password, userOptional.get().getPassword())) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userOptional.get().getRole().getName().toUpperCase()));
//            authorities.add(new SimpleGrantedAuthority("USER_ID_" + userOptional.get().getId()));
            String token = jwtTokenService.generateToken(userOptional.get(), authorities);
            return ResponseEntity.ok().body(token);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
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
