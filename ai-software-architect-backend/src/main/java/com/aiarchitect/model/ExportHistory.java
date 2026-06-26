package com.aiarchitect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity tracking every time a user exports an architecture report.
 * Maps to the "export_history" table in the database.
 *
 * WHY TRACK EXPORTS:
 * 1. Analytics: Know which export formats are most popular (PDF vs MD vs JSON)
 * 2. Usage limits: Could implement rate limiting on exports if needed
 * 3. Audit trail: Know when users downloaded sensitive architecture data
 *
 * This is the simplest entity — just records the project ID, format, and timestamp.
 */
@Entity
@Table(name = "export_history", indexes = {
        @Index(name = "idx_export_history_project_id", columnList = "project_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The format that was exported.
     * Values: "pdf", "markdown", "json"
     * Uses the constants from AppConstants.EXPORT_FORMAT_*
     */
    @Column(nullable = false, length = 20)
    private String format;

    @CreationTimestamp
    @Column(name = "exported_at", nullable = false, updatable = false)
    private LocalDateTime exportedAt;

    // ---- Relationship ----

    /**
     * Many ExportHistory entries → One Project.
     * A project can be exported multiple times in different formats.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    private Project project;
}
