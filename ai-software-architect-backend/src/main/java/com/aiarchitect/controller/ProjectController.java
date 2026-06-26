package com.aiarchitect.controller;

import com.aiarchitect.dto.request.GenerateArchitectureRequest;
import com.aiarchitect.dto.response.ProjectResponse;
import com.aiarchitect.service.ProjectService;
import com.aiarchitect.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controller exposing REST endpoints for managing projects and triggering architecture generation.
 *
 * All endpoints require a valid JWT token. Access ownership is enforced at the service layer
 * by resolving the authenticated user's email via {@link Principal#getName()}.
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for project management and AI architecture generation")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Triggers AI content generation for a plain-English software concept.
     */
    @PostMapping("/generate")
    @Operation(summary = "Submit a project idea to generate architecture report", 
               description = "Triggers the AI prompt engineering pipeline, invokes Gemini, deserializes, and saves the detailed report.")
    public ResponseEntity<ProjectResponse> generateProject(
            @Valid @RequestBody GenerateArchitectureRequest request,
            Principal principal) {
        ProjectResponse response = projectService.generateProject(request.getProjectIdea(), principal.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves metadata and full architecture report detail for a project.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get project details by ID", 
               description = "Retrieves project metadata and the complete 19-section architecture report. Ownership is enforced.")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id, Principal principal) {
        ProjectResponse response = projectService.getProjectById(id, principal.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Lists lightweight project summaries for the logged-in user.
     */
    @GetMapping
    @Operation(summary = "List all projects for the logged-in user", 
               description = "Returns a lightweight listing of all projects belonging to the authenticated user (excludes large report details).")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(Principal principal) {
        List<ProjectResponse> response = projectService.getAllProjects(principal.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Searches for projects matching a title keyword.
     */
    @GetMapping("/search")
    @Operation(summary = "Search projects by title", 
               description = "Returns user projects containing the keyword in their title.")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam String keyword,
            Principal principal) {
        List<ProjectResponse> response = projectService.searchProjects(keyword, principal.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a project and its reports.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project", 
               description = "Deletes a project and its associated architecture report. Ownership is enforced.")
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable Long id, Principal principal) {
        projectService.deleteProject(id, principal.getName());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Project deleted successfully",
                "projectId", id
        ));
    }
}
