package com.epam.esm.core.service.impl.auth.local;

import com.epam.esm.core.entity.AuthenticationResponse;
import com.epam.esm.core.entity.RefreshToken;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.RefreshTokenRepository;
import com.epam.esm.core.service.AuthService;
import com.epam.esm.core.service.UserService;
import com.epam.esm.core.service.impl.PasswordEncoderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static com.epam.esm.core.util.CoreConstants.USER_ID_AUTHORITY_PREFIX;

@Service
public class LocalAuthServiceImpl implements AuthService {
    private final PasswordEncoderService passwordEncoderService;
    private final LocalJwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(LocalAuthServiceImpl.class);

    @Autowired
    public LocalAuthServiceImpl(
            UserService userService,
            PasswordEncoderService passwordEncoderService,
            LocalJwtTokenService jwtTokenService,
            RefreshTokenRepository refreshTokenRepository) {
        this.passwordEncoderService = Objects.requireNonNull(passwordEncoderService);
        this.jwtTokenService = Objects.requireNonNull(jwtTokenService);
        this.refreshTokenRepository = Objects.requireNonNull(refreshTokenRepository);
        this.userService = Objects.requireNonNull(userService);
    }

    public ResponseEntity<?> authenticateUser(String email, String password) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoderService.matches(password, userOptional.get().getPassword())) {
            return generateResponseTokens(userOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    private ResponseEntity<?> generateResponseTokens(User user) {
        String accessToken = jwtTokenService.generateAccessToken(user, generateAuthoritiesList(user));
        String refreshToken = jwtTokenService.generateRefreshToken();

        saveRefreshToken(refreshToken, user.getId());

        return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
    }

    private List<SimpleGrantedAuthority> generateAuthoritiesList(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName().toUpperCase()));
        authorities.add(new SimpleGrantedAuthority(USER_ID_AUTHORITY_PREFIX + user.getId()));
        return authorities;
    }

    private void saveRefreshToken(String refreshToken, Long userId) {
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(refreshToken);
        newRefreshToken.setUserId(userId);
        newRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public ResponseEntity<?> validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);

        if (refreshTokenOptional.isPresent()) {
            Long userId = refreshTokenOptional.get().getUserId();
            refreshTokenRepository.delete(refreshTokenOptional.get());
            Optional<User> userOptional = userService.findById(userId);
            return generateResponseTokens(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
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
