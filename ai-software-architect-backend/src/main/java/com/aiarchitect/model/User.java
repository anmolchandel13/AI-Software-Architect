package com.aiarchitect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a registered user of the application.
 * Maps to the "users" table in the database.
 *
 * === ANNOTATION GUIDE (read this carefully) ===
 *
 * @Entity      — Tells JPA "this class maps to a database table."
 *                Without this, Hibernate ignores the class entirely.
 *
 * @Table       — Customizes the table name. We use "users" instead of the
 *                default "user" because "user" is a reserved keyword in MySQL.
 *
 * @Id          — Marks the primary key field.
 *
 * @GeneratedValue(IDENTITY) — The database auto-generates the ID.
 *                IDENTITY means MySQL's AUTO_INCREMENT handles it.
 *
 * @Column      — Customizes the column: nullable, unique constraints,
 *                length limits. Without @Column, JPA uses defaults.
 *
 * @CreationTimestamp — Hibernate automatically sets this to the current
 *                      time when the entity is first saved (INSERT).
 *
 * @UpdateTimestamp   — Hibernate automatically updates this to the current
 *                      time on every UPDATE.
 *
 * @OneToMany   — Defines the relationship: one User has many Projects.
 *                mappedBy = "user" means the Project entity owns the
 *                relationship (it has the foreign key column).
 *                cascade = ALL means if we delete a User, all their
 *                Projects are deleted too (cascading delete).
 *                orphanRemoval = true means if we remove a Project from
 *                the user's list, it gets deleted from the database.
 *
 * === LOMBOK ANNOTATIONS ===
 *
 * @Data        — Generates getters, setters, toString, equals, hashCode.
 * @Builder     — Generates a builder pattern: User.builder().username("john").build()
 * @NoArgsConstructor — Required by JPA (it creates entities via reflection).
 * @AllArgsConstructor — Needed by @Builder for construction.
 *
 * @ToString.Exclude on 'projects' — Prevents infinite loops when printing.
 *                Without this, printing a User prints its Projects,
 *                each Project prints its User, creating infinite recursion.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_users_email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Stores the BCrypt hash of the password, NOT the plain-text password.
     * BCrypt hashes are always 60 characters long, but we use 255 for safety.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User role for access control. Defaults to "ROLE_USER".
     * Possible values: ROLE_USER, ROLE_ADMIN
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "ROLE_USER";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One User → Many Projects relationship.
     *
     * mappedBy = "user" → The Project entity owns this relationship
     *                      (it has the user_id foreign key column).
     * cascade = ALL     → Operations on User cascade to Projects
     *                      (save, update, delete propagate).
     * orphanRemoval     → Removing a Project from this list deletes it
     *                      from the database automatically.
     * fetch = LAZY      → Projects are NOT loaded when you fetch a User.
     *                      They're loaded only when you explicitly access
     *                      this field. This prevents loading hundreds of
     *                      projects every time you just need a user's email.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Project> projects = new ArrayList<>();

    // ---- Helper Methods ----
    // These maintain both sides of the bidirectional relationship.
    // Always use these instead of directly manipulating the lists.

    public void addProject(Project project) {
        projects.add(project);
        project.setUser(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setUser(null);
    }
}
