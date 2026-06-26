package com.aiarchitect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity storing the complete AI-generated architecture report.
 * Maps to the "architecture_reports" table in the database.
 *
 * === WHY EACH SECTION IS A SEPARATE COLUMN ===
 *
 * We could store the entire AI response as one giant TEXT column.
 * But splitting it into separate columns gives us:
 *
 * 1. SELECTIVE LOADING: Query only the sections you need
 *    (e.g., just the ER diagram for rendering, without loading 18 other sections)
 *
 * 2. INDIVIDUAL UPDATES: If we want to regenerate just one section,
 *    we update that column without touching the others.
 *
 * 3. CLEANER API: The frontend can request specific sections via query params.
 *
 * 4. SEARCHABILITY: We can search within specific sections
 *    (e.g., find all reports that mention "Redis" in their database design).
 *
 * === COLUMN TYPE: @Lob + columnDefinition = "MEDIUMTEXT" ===
 *
 * MySQL TEXT types and their limits:
 * - TINYTEXT:   255 bytes          (too small for any section)
 * - TEXT:       65,535 bytes (~64KB) (might be enough for small sections)
 * - MEDIUMTEXT: 16,777,215 bytes (~16MB) (safe for any AI response)
 * - LONGTEXT:   4GB                 (overkill)
 *
 * We use MEDIUMTEXT to be safe — some sections like "API Endpoints" or
 * "JPA Entities" can be very long for complex projects.
 */
@Entity
@Table(name = "architecture_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===========================================================
    // ARCHITECTURE REPORT SECTIONS
    // Each section stores one part of the AI-generated architecture.
    // All are optional (@Column nullable = true by default) because
    // the AI might not generate all sections for every project.
    // ===========================================================

    /** What the system does, who uses it, and why it exists */
    @Lob
    @Column(name = "project_overview")
    private String projectOverview;

    /** High-level goals the system must achieve */
    @Lob
    @Column(name = "business_requirements")
    private String businessRequirements;

    /** Specific features and behaviors of the system */
    @Lob
    @Column(name = "functional_requirements")
    private String functionalRequirements;

    /** Performance, scalability, security, and availability expectations */
    @Lob
    @Column(name = "non_functional_requirements")
    private String nonFunctionalRequirements;

    /** List of all recommended modules and major system components */
    @Lob
    @Column(name = "modules")
    private String modules;

    /** Complete database design: tables, columns, data types, relationships */
    @Lob
    @Column(name = "database_design")
    private String databaseDesign;

    /** Ready-to-execute SQL CREATE TABLE statements */
    @Lob
    @Column(name = "sql_statements")
    private String sqlStatements;

    /** ER diagram in Mermaid.js format for visual rendering */
    @Lob
    @Column(name = "er_diagram_mermaid")
    private String erDiagramMermaid;

    /** Complete list of REST API endpoints with methods, paths, request/response */
    @Lob
    @Column(name = "api_endpoints")
    private String apiEndpoints;

    /** Spring Boot folder and package structure */
    @Lob
    @Column(name = "spring_boot_structure")
    private String springBootStructure;

    /** JPA entity classes with fields and annotations */
    @Lob
    @Column(name = "jpa_entities")
    private String jpaEntities;

    /** DTO classes for data transfer */
    @Lob
    @Column(name = "dto_classes")
    private String dtoClasses;

    /** Field-level validation rules for all inputs */
    @Lob
    @Column(name = "validation_rules")
    private String validationRules;

    /** Security recommendations specific to the project */
    @Lob
    @Column(name = "security_recommendations")
    private String securityRecommendations;

    /** Role-based access control design */
    @Lob
    @Column(name = "rbac_design")
    private String rbacDesign;

    /** Password encryption strategy */
    @Lob
    @Column(name = "password_strategy")
    private String passwordStrategy;

    /** Whether to use microservices and how to divide them */
    @Lob
    @Column(name = "microservices_recommendation")
    private String microservicesRecommendation;

    /** Docker and AWS deployment strategy */
    @Lob
    @Column(name = "deployment_strategy")
    private String deploymentStrategy;

    /** Week-by-week development roadmap */
    @Lob
    @Column(name = "development_roadmap")
    private String developmentRoadmap;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ---- Relationship ----

    /**
     * One ArchitectureReport → One Project (inverse side).
     *
     * @OneToOne: Each report belongs to exactly one project.
     * @JoinColumn: Creates a "project_id" FK column in this table.
     *
     * This is the OWNING side of the relationship (it has the FK).
     * The Project's @OneToOne(mappedBy="project") is the inverse side.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    @ToString.Exclude
    private Project project;
}
