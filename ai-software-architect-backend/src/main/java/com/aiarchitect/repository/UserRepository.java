package com.aiarchitect.repository;

import com.aiarchitect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User database operations.
 *
 * === HOW SPRING DATA JPA REPOSITORIES WORK ===
 *
 * This is one of the most powerful features of Spring Boot.
 * By simply EXTENDING JpaRepository, we get ALL of these methods for FREE:
 *
 *   save(User user)          → INSERT or UPDATE
 *   findById(Long id)        → SELECT * FROM users WHERE id = ?
 *   findAll()                → SELECT * FROM users
 *   deleteById(Long id)      → DELETE FROM users WHERE id = ?
 *   count()                  → SELECT COUNT(*) FROM users
 *   existsById(Long id)      → SELECT COUNT(*) > 0 FROM users WHERE id = ?
 *
 * We don't write ANY implementation code. Spring generates it at runtime
 * by analyzing the method names. This is called "query derivation."
 *
 * JpaRepository<User, Long> means:
 *   - User = the entity type this repository manages
 *   - Long = the type of the entity's primary key (@Id field)
 *
 * === CUSTOM QUERY METHODS ===
 *
 * Beyond the built-in methods, we can define custom queries by following
 * Spring's naming convention. For example:
 *
 *   findByEmail(String email)
 *   Spring sees: find + By + Email
 *   It generates: SELECT * FROM users WHERE email = ?
 *
 *   existsByEmail(String email)
 *   Spring sees: exists + By + Email
 *   It generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
 *
 * No SQL, no implementation class, no boilerplate. Just a method signature.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     * Used during login to locate the user, then verify their password.
     *
     * Returns Optional<User> instead of User because the email might not exist.
     * This forces the caller to handle the "not found" case explicitly,
     * preventing NullPointerExceptions.
     *
     * Generated SQL: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if an email is already registered.
     * Used during registration to prevent duplicate accounts.
     *
     * Returns boolean (not Optional) because we just need yes/no.
     *
     * Generated SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);

    /**
     * Check if a username is already taken.
     * Used during registration to ensure unique usernames.
     *
     * Generated SQL: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);
}
