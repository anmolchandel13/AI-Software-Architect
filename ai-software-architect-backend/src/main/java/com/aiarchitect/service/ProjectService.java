package com.aiarchitect.service;

import com.aiarchitect.dto.response.ArchitectureReportResponse;
import com.aiarchitect.dto.response.ProjectResponse;
import com.aiarchitect.exception.AiServiceException;
import com.aiarchitect.exception.ResourceNotFoundException;
import com.aiarchitect.exception.UnauthorizedException;
import com.aiarchitect.model.ArchitectureReport;
import com.aiarchitect.model.Project;
import com.aiarchitect.model.User;
import com.aiarchitect.repository.ProjectRepository;
import com.aiarchitect.repository.UserRepository;
import com.aiarchitect.service.ai.ArchitectureGeneratorStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class orchestrating project creation, AI generation, and database updates.
 *
 * Implements core transactional boundaries:
 * 1. Creates a Project shell in PENDING state.
 * 2. Invokes the AI generator (Gemini) to obtain JSON report text.
 * 3. Deserializes the JSON and maps it to the 19-column {@link ArchitectureReport} entity.
 * 4. Persists the report and marks the project as COMPLETED.
 * 5. Handles failures by transitioning the status to FAILED.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ArchitectureGeneratorStrategy aiStrategy;
    private final ObjectMapper objectMapper;

    /**
     * Generates a new software architecture report using the active AI Strategy.
     *
     * @param originalIdea the user's software system idea.
     * @param email the authenticated user's email.
     * @return DTO representation of the created Project with report details.
     */
    @Transactional
    public ProjectResponse generateProject(String originalIdea, String email) {
        log.info("Starting project generation flow for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // 1. Determine Title and create Project Shell in PENDING state
        String title = determineTitle(originalIdea);
        Project project = Project.builder()
                .title(title)
                .originalIdea(originalIdea)
                .status("PENDING")
                .user(user)
                .build();

        project = projectRepository.save(project);

        try {
            // 2. Update status to PROCESSING
            project.setStatus("PROCESSING");
            project = projectRepository.saveAndFlush(project);

            // 3. Request architecture structure from AI
            String generatedJson = aiStrategy.generateArchitecture(originalIdea);

            // 4. Deserialize and map raw JSON output to the ArchitectureReport entity
            ArchitectureReport report = objectMapper.readValue(generatedJson, ArchitectureReport.class);
            report.setProject(project);

            // 5. Connect report to project and update status to COMPLETED
            project.setArchitectureReport(report);
            project.setStatus("COMPLETED");

            project = projectRepository.save(project);
            log.info("Project '{}' successfully generated and saved with ID: {}", title, project.getId());

            return convertToProjectResponse(project, true);

        } catch (Exception e) {
            log.error("AI Generation workflow failed for project ID: {}", project.getId(), e);
            project.setStatus("FAILED");
            projectRepository.save(project);

            throw new AiServiceException("Failed to generate software architecture: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a single project with its full architecture report.
     * Enforces ownership checking (users can only access their own projects).
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id, String email) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        validateOwnership(project, email);

        return convertToProjectResponse(project, true);
    }

    /**
     * Lists all projects belonging to the authenticated user.
     * Does not fetch the large architecture report fields for lightweight listings.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        List<Project> projects = projectRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return projects.stream()
                .map(p -> convertToProjectResponse(p, false))
                .collect(Collectors.toList());
    }

    /**
     * Searches the user's projects by title or content matching.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> searchProjects(String keyword, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        List<Project> projects = projectRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(user.getId(), keyword);

        return projects.stream()
                .map(p -> convertToProjectResponse(p, false))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a project. Enforces ownership checking.
     */
    @Transactional
    public void deleteProject(Long id, String email) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        validateOwnership(project, email);

        projectRepository.delete(project);
        log.info("Deleted project with ID: {} owned by user: {}", id, email);
    }

    // ---- Helper Methods ----

    private void validateOwnership(Project project, String email) {
        if (!project.getUser().getEmail().equals(email)) {
            log.warn("Unauthorized access attempt: user {} tried to access project ID {}", email, project.getId());
            throw new UnauthorizedException("You are not authorized to access this project");
        }
    }

    private String determineTitle(String originalIdea) {
        if (originalIdea == null || originalIdea.isBlank()) {
            return "Untitled Project";
        }
        String firstLine = originalIdea.split("\n")[0].trim();
        if (firstLine.length() > 60) {
            return firstLine.substring(0, 57) + "...";
        }
        return firstLine;
    }

    private ProjectResponse convertToProjectResponse(Project project, boolean includeReport) {
        ProjectResponse.ProjectResponseBuilder builder = ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .originalIdea(project.getOriginalIdea())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt());

        if (includeReport && project.getArchitectureReport() != null) {
            ArchitectureReport report = project.getArchitectureReport();
            builder.architectureReport(ArchitectureReportResponse.builder()
                    .id(report.getId())
                    .projectOverview(report.getProjectOverview())
                    .businessRequirements(report.getBusinessRequirements())
                    .functionalRequirements(report.getFunctionalRequirements())
                    .nonFunctionalRequirements(report.getNonFunctionalRequirements())
                    .modules(report.getModules())
                    .databaseDesign(report.getDatabaseDesign())
                    .sqlStatements(report.getSqlStatements())
                    .erDiagramMermaid(report.getErDiagramMermaid())
                    .apiEndpoints(report.getApiEndpoints())
                    .springBootStructure(report.getSpringBootStructure())
                    .jpaEntities(report.getJpaEntities())
                    .dtoClasses(report.getDtoClasses())
                    .validationRules(report.getValidationRules())
                    .securityRecommendations(report.getSecurityRecommendations())
                    .rbacDesign(report.getRbacDesign())
                    .passwordStrategy(report.getPasswordStrategy())
                    .microservicesRecommendation(report.getMicroservicesRecommendation())
                    .deploymentStrategy(report.getDeploymentStrategy())
                    .developmentRoadmap(report.getDevelopmentRoadmap())
                    .createdAt(report.getCreatedAt())
                    .build());
        }

        return builder.build();
    }
}
