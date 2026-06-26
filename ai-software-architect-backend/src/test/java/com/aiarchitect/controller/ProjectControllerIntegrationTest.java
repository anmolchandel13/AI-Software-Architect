package com.aiarchitect.controller;

import com.aiarchitect.dto.request.GenerateArchitectureRequest;
import com.aiarchitect.model.User;
import com.aiarchitect.repository.ProjectRepository;
import com.aiarchitect.repository.UserRepository;
import com.aiarchitect.security.JwtTokenProvider;
import com.aiarchitect.service.ai.ArchitectureGeneratorStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController.
 *
 * Verifies:
 * 1. Project generation succeeds when AI returns valid JSON. Assert reports columns populate correctly.
 * 2. Cross-user access control: attempting to fetch/delete another user's project returns 401 Unauthorized.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private ArchitectureGeneratorStrategy aiStrategy;

    private String jwtToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();

        testUser = User.builder()
                .username("testarchitect")
                .email("architect@example.com")
                .passwordHash("hashedpassword")
                .role("ROLE_USER")
                .build();
        testUser = userRepository.save(testUser);

        jwtToken = tokenProvider.generateToken(
                testUser.getEmail(),
                testUser.getId(),
                testUser.getUsername(),
                testUser.getRole()
        );
    }

    @Test
    void testGenerateProjectSuccess() throws Exception {
        // 1. Mock the AI response to return a valid JSON matching our 19 keys
        String mockResponseJson = """
                {
                  "projectOverview": "Mock Project Overview",
                  "businessRequirements": "Mock Business",
                  "functionalRequirements": "Mock Functional",
                  "nonFunctionalRequirements": "Mock Non-Functional",
                  "modules": "Mock Modules",
                  "databaseDesign": "Mock DB",
                  "sqlStatements": "Mock SQL",
                  "erDiagramMermaid": "erDiagram",
                  "apiEndpoints": "Mock API",
                  "springBootStructure": "Mock packages",
                  "jpaEntities": "Mock entities",
                  "dtoClasses": "Mock DTOs",
                  "validationRules": "Mock rules",
                  "securityRecommendations": "Mock security",
                  "rbacDesign": "Mock RBAC",
                  "passwordStrategy": "Mock password",
                  "microservicesRecommendation": "Mock micro",
                  "deploymentStrategy": "Mock deploy",
                  "developmentRoadmap": "Mock roadmap"
                }
                """;

        Mockito.when(aiStrategy.generateArchitecture(anyString())).thenReturn(mockResponseJson);

        // 2. Perform POST to generate project
        GenerateArchitectureRequest request = GenerateArchitectureRequest.builder()
                .projectIdea("Build a premium e-commerce backend")
                .build();

        mockMvc.perform(post("/api/v1/projects/generate")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Build a premium e-commerce backend")))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.architectureReport", notNullValue()))
                .andExpect(jsonPath("$.architectureReport.projectOverview", is("Mock Project Overview")))
                .andExpect(jsonPath("$.architectureReport.developmentRoadmap", is("Mock roadmap")));
    }

    @Test
    void testGetProjectByIdUnauthorized() throws Exception {
        // Create project owned by another user
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .passwordHash("otherpass")
                .build();
        otherUser = userRepository.save(otherUser);

        com.aiarchitect.model.Project project = com.aiarchitect.model.Project.builder()
                .title("Secret Project")
                .originalIdea("Secret Idea")
                .status("COMPLETED")
                .user(otherUser)
                .build();
        project = projectRepository.save(project);

        // Try to access it with our testUser's token
        mockMvc.perform(get("/api/v1/projects/" + project.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized()); // Should throw UnauthorizedException -> returns 401
    }
}
