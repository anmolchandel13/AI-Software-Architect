package com.aiarchitect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for the full AI-generated architecture report.
 *
 * Each field corresponds to one section of the generated architecture.
 * All fields are Strings containing the AI's output (which may be
 * formatted as Markdown, JSON, or Mermaid depending on the section).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureReportResponse {

    private Long id;

    // ---- Architecture Report Sections ----
    private String projectOverview;
    private String businessRequirements;
    private String functionalRequirements;
    private String nonFunctionalRequirements;
    private String modules;
    private String databaseDesign;
    private String sqlStatements;
    private String erDiagramMermaid;
    private String apiEndpoints;
    private String springBootStructure;
    private String jpaEntities;
    private String dtoClasses;
    private String validationRules;
    private String securityRecommendations;
    private String rbacDesign;
    private String passwordStrategy;
    private String microservicesRecommendation;
    private String deploymentStrategy;
    private String developmentRoadmap;

    private LocalDateTime createdAt;
}
