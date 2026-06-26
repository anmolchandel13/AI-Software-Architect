package com.aiarchitect.service.ai;

import org.springframework.stereotype.Component;

/**
 * Utility class for constructing structured prompt templates for the Gemini AI model.
 *
 * Prompt Engineering Principles Applied:
 * 1. Role prompting ("You are a world-class Senior Software Architect").
 * 2. Strict output constraint ("output your response strictly as a single, valid JSON object").
 * 3. Exact schema mapping (dictating the 19 specific keys representing report sections).
 * 4. Detail enforcement (requesting complete markdown snippets, SQL statements, and Java code).
 */
@Component
public class PromptBuilder {

    /**
     * Builds the structured system instruction prompt for a given user idea.
     *
     * @param projectIdea the user's software concept.
     * @return the fully formatted prompt string.
     */
    public String buildPrompt(String projectIdea) {
        return """
                You are a world-class Senior Software Architect. Your task is to design a complete, production-ready, professional-grade software architecture report based on the following user project idea:
                
                Project Idea: "%s"
                
                You must output your response strictly as a single, valid JSON object.
                Do not include any wrapping markdown blocks like ```json or ```. Output ONLY the raw JSON string.
                
                The JSON object must contain exactly the following 19 keys. The value of each key must be in Markdown format, providing detailed technical contents:
                
                1. "projectOverview": A detailed summary of what the system does, who uses it, why it exists, and the core value proposition.
                2. "businessRequirements": High-level business goals, core objectives, and domain concepts.
                3. "functionalRequirements": Bulleted list of specific features, capabilities, user roles, and behaviors of the system.
                4. "nonFunctionalRequirements": Technical expectations for performance, scalability, security, availability, and reliability.
                5. "modules": Outline of the recommended modular layout (major system components or microservices boundaries).
                6. "databaseDesign": The detailed database design including proposed tables, columns, types (MySQL dialect), primary/foreign keys, and relationships.
                7. "sqlStatements": Ready-to-run DDL SQL statements (CREATE TABLE, ALTER TABLE, indexes) for all tables.
                8. "erDiagramMermaid": An Entity-Relationship (ER) diagram rendered in Mermaid.js syntax. Do not wrap this in a code block inside the JSON string; just output the raw Mermaid diagram text.
                9. "apiEndpoints": Complete list of REST API endpoints (method, path, request body structure, response status/body structure, headers).
                10. "springBootStructure": The recommended package layout and package structure (under com.example.project) showing controllers, services, repositories, models, etc.
                11. "jpaEntities": Ready-to-use Java JPA entity code snippets with Hibernate annotations for core tables.
                12. "dtoClasses": Ready-to-use Java DTO class snippets for incoming requests and outgoing API responses, including jakarta.validation annotations.
                13. "validationRules": Comprehensive validation constraints for DTOs and database columns (size, range, format).
                14. "securityRecommendations": Specific threat modeling, OWASP protections (CORS, CSRF, XSS), and encryption recommendations.
                15. "rbacDesign": Role-Based Access Control (RBAC) mapping table, showing which roles (e.g., ADMIN, USER) have access to which REST endpoints.
                16. "passwordStrategy": Recommended password hashing algorithms, salting approach, and security policies (minimum strength, history).
                17. "microservicesRecommendation": Architectural justification of whether to use monolith or microservices, detailing boundaries, communication (REST, gRPC, MQ), and orchestration if applicable.
                18. "deploymentStrategy": Complete Dockerfile setups, docker-compose.yml configuration, and AWS hosting instructions (RDS, ECS, VPC).
                19. "developmentRoadmap": A week-by-week implementation plan (e.g., Week 1: Auth, Week 2: Core modules...) to guide developer execution.
                
                Remember:
                - Output ONLY valid JSON.
                - Escape double quotes and backslashes inside your markdown text correctly so the JSON is fully parseable.
                - Be thorough, technical, and detailed in every section. Do not use generic placeholders.
                """.formatted(projectIdea);
    }
}
