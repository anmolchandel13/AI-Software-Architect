package com.aiarchitect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for project listing and detail views.
 *
 * This is what the frontend receives when it asks for a project.
 * Notice: it does NOT contain the user's password hash, database IDs
 * of internal entities, or any other sensitive data — that's exactly
 * why we use DTOs instead of returning JPA entities directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String originalIdea;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** The full architecture report — included when viewing a single project */
    private ArchitectureReportResponse architectureReport;
}
