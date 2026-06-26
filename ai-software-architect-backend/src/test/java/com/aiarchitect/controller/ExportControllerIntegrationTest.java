package com.aiarchitect.controller;

import com.aiarchitect.model.ArchitectureReport;
import com.aiarchitect.model.Project;
import com.aiarchitect.model.User;
import com.aiarchitect.repository.ProjectRepository;
import com.aiarchitect.repository.UserRepository;
import com.aiarchitect.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ExportController.
 *
 * Verifies:
 * 1. Markdown exporter compiles headers and content correctly.
 * 2. JSON exporter formats fields without recursion loops.
 * 3. PDF exporter builds binary output stream.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ExportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String jwtToken;
    private Project testProject;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();

        User testUser = User.builder()
                .username("exportuser")
                .email("exportuser@example.com")
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

        ArchitectureReport report = ArchitectureReport.builder()
                .projectOverview("Test Project Overview")
                .businessRequirements("Test Business")
                .functionalRequirements("Test Functional")
                .build();

        testProject = Project.builder()
                .title("Export Project")
                .originalIdea("Original Export Idea")
                .status("COMPLETED")
                .user(testUser)
                .architectureReport(report)
                .build();
        
        report.setProject(testProject);
        testProject = projectRepository.save(testProject);
    }

    @Test
    void testExportToMarkdown() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + testProject.getId() + "/export/markdown")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + testProject.getId() + ".md\""))
                .andExpect(content().contentType("text/markdown"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# Export Project")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("## Project Overview")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Project Overview")));
    }

    @Test
    void testExportToJson() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + testProject.getId() + "/export/json")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + testProject.getId() + ".json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.projectOverview", org.hamcrest.Matchers.is("Test Project Overview")))
                .andExpect(jsonPath("$.businessRequirements", org.hamcrest.Matchers.is("Test Business")));
    }

    @Test
    void testExportToPdf() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + testProject.getId() + "/export/pdf")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + testProject.getId() + ".pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
