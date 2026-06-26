package com.aiarchitect.config;

import com.aiarchitect.security.JwtAuthenticationFilter;
import com.aiarchitect.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for securing REST endpoints with JWT.
 *
 * This configuration defines:
 * 1. BCrypt password encoder for hashing passwords.
 * 2. AuthenticationManager bean for credentials verification.
 * 3. Security filter chain that locks down endpoints, disables CSRF (stateless API),
 *    enforces JWT authentication filter, and permits access to public URLs (auth, swagger).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * BCrypt password encoder bean.
     * Generates a secure salt and hashes passwords computationally to prevent brute-force attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean, which acts as the core interface
     * for validating user credentials during the login flow.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Defines the HTTP security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT-based APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Allow H2 console to render inside frames
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))

                // Stateless session — do not create or use HTTP sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure route access permissions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AppConstants.PUBLIC_URLS).permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                );

        // Add the JWT authentication filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
