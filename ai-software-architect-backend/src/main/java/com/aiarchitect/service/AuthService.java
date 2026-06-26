package com.aiarchitect.service;

import com.aiarchitect.dto.request.LoginRequest;
import com.aiarchitect.dto.request.RegisterRequest;
import com.aiarchitect.dto.response.AuthResponse;
import com.aiarchitect.model.User;
import com.aiarchitect.repository.UserRepository;
import com.aiarchitect.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class handling all user registration and authentication business logic.
 *
 * This class coordinates:
 * 1. BCrypt password hashing when registering a new user.
 * 2. Uniqueness checks for email and username before registration.
 * 3. Delegating credentials validation to Spring Security's AuthenticationManager during login.
 * 4. Generating JWT tokens via {@link JwtTokenProvider} upon successful login.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Registers a new user account in the database.
     *
     * @param registerRequest DTO containing username, email, and password.
     * @return the saved User entity.
     * @throws IllegalArgumentException if the email or username is already registered.
     */
    @Transactional
    public User register(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email {} is already in use", registerRequest.getEmail());
            throw new IllegalArgumentException("Email address is already in use");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed: Username {} is already taken", registerRequest.getUsername());
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role("ROLE_USER") // Default role assigned to standard users
                .build();

        return userRepository.save(user);
    }

    /**
     * Authenticates a user's credentials and returns a signed JWT.
     *
     * @param loginRequest DTO containing email and password.
     * @return AuthResponse containing the JWT token and basic user details.
     */
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Attempting to authenticate user: {}", loginRequest.getEmail());

        // Delegate credential checking to Spring Security (which calls CustomUserDetailsService under the hood)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Store authentication object in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Retrieve full user details to build JWT claims and response body
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("Authentication succeeded but user not found in DB for email: {}", loginRequest.getEmail());
                    return new IllegalArgumentException("User not found with email: " + loginRequest.getEmail());
                });

        // Generate token
        String token = tokenProvider.generateToken(
                user.getEmail(),
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        log.info("User {} successfully authenticated. Token generated.", loginRequest.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
