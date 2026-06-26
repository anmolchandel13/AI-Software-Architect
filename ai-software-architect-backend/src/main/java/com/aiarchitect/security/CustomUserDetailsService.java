package com.aiarchitect.security;

import com.aiarchitect.model.User;
import com.aiarchitect.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 *
 * Spring Security uses this service to load user credentials and roles during
 * authentication and authorization verification. Since we authenticate using the user's
 * EMAIL (not username), our loadUserByUsername method actually accepts an email string.
 *
 * This class connects Spring Security directly to our MySQL database via UserRepository.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user from the database by email and converts it to a Spring Security UserDetails object.
     *
     * @param email the email identifying the user whose data is required.
     * @return UserDetails containing credentials and authorities.
     * @throws UsernameNotFoundException if user with the given email does not exist.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Note: Spring Security User needs: username (we use email), password (hash), and authorities/roles.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
