package com.aiarchitect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a software architecture project.
 * Maps to the "projects" table in the database.
 *
 * A Project is the central entity in the system. It:
 * 1. Belongs to one User (Many-to-One)
 * 2. Has one ArchitectureReport (One-to-One)
 * 3. Can have many ExportHistory entries (One-to-Many)
 *
 * === RELATIONSHIP OWNERSHIP ===
 *
 * In JPA, one side of a relationship must "own" it, meaning it has
 * the actual foreign key column in the database.
 *
 * - Project OWNS the relationship with User (has user_id FK column)
 * - Project OWNS the relationship with ArchitectureReport (report has project_id FK)
 * - Project OWNS the relationship with ExportHistory (export has project_id FK)
 *
 * @ManyToOne  — Many Projects belong to One User.
 *               @JoinColumn specifies the FK column name in this table.
 *               fetch = LAZY means the User object is NOT loaded
 *               automatically — only when explicitly accessed.
 */
@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_projects_user_id", columnList = "user_id"),
        @Index(name = "idx_projects_title", columnList = "title"),
        @Index(name = "idx_projects_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Auto-generated title extracted from the project idea.
     * Example: "E-commerce Backend" from "Build an E-commerce Backend with..."
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * The exact text the user typed as their project idea.
     * Stored as TEXT (not VARCHAR) because ideas can be several paragraphs.
     *
     * @Lob tells JPA to use the database's large object type (TEXT in MySQL).
     * columnDefinition = "TEXT" ensures MySQL creates a TEXT column, not TINYTEXT.
     */
    @Lob
    @Column(name = "original_idea", nullable = false)
    private String originalIdea;

    /**
     * Processing status of the architecture generation.
     * Values: PENDING → PROCESSING → COMPLETED → FAILED
     *
     * We track status because AI generation can take 10-30 seconds.
     * This allows the frontend to show a progress indicator.
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ---- Relationships ----

    /**
     * Many Projects → One User.
     *
     * @ManyToOne: This is the "many" side — many projects belong to one user.
     * @JoinColumn: Creates a "user_id" column in the projects table that
     *              references the "id" column in the users table.
     * fetch = LAZY: The User object is loaded only when you call project.getUser().
     *               Without LAZY, every time you load a project, JPA would also
     *               load the entire User object, which wastes memory.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * One Project → One ArchitectureReport.
     *
     * mappedBy = "project": The ArchitectureReport entity has the FK column.
     * cascade = ALL: Saving/deleting a Project cascades to its Report.
     * orphanRemoval: If we set this to null, the old report is deleted.
     */
    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private ArchitectureReport architectureReport;

    /**
     * One Project → Many ExportHistory entries.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ExportHistory> exportHistories = new ArrayList<>();

    // ---- Helper Methods ----

    public void setArchitectureReport(ArchitectureReport report) {
        this.architectureReport = report;
        if (report != null) {
            report.setProject(this);
        }
    }

    public void addExportHistory(ExportHistory export) {
        exportHistories.add(export);
        export.setProject(this);
    }
}
