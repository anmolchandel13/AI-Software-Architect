# AI Software Architect 🛠️

**AI Software Architect** is a full-stack, enterprise-grade web application that operates as an AI-powered Senior Software Architect. A user describes a software project concept in plain English, and the application designs a comprehensive, multi-layered system blueprint—database schemas, SQL statements, Entity-Relationship diagrams (Mermaid.js), Spring Boot packaging structures, JPA entities, validation rules, Docker files, and deployment plans.

---

## 🚀 Key Features

- **Secured Authentication**: Stateless JWT-based security filter chain utilizing HMAC-SHA256 signature verification.
- **AI Blueprint Engine**: Leverages Google Gemini 2.0 Flash with response configuration mapping inputs into a robust 19-key JSON schema.
- **JPA & Database Design**: Hibernate ORM mapping model entities (`User`, `Project`, `ArchitectureReport`, `ExportHistory`) with Cascade controls and lazy-fetch settings.
- **Interactive Reports Dashboard**: Full project history logs, keyword-based search queries, and secure delete flows.
- **Multiformat Document Exports**: Stream reports dynamically as styled print-ready PDFs, structured JSON blueprints, or raw Markdown.
- **Visual ER Diagrams**: Compiled and rendered on-the-fly using Mermaid.js integrations.
- **Centralized Error Control**: Standardized HTTP exceptions mapped through global controller advice to output uniform error schemas.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.3, Spring Security, Spring Data JPA, WebClient, OpenPDF, JJWT (0.12.6)
- **Frontend**: React 18, Vite 5, Axios, React Router v6, Lucide React, Mermaid.js
- **Database**: H2 (Development & Testing), MySQL 8 (Production)
- **DevOps**: Docker, Docker Compose, AWS (RDS, ECS Fargate, ECR, ALB)

---

## 📁 Repository Structure

```
ai-software-architect/
├── ai-software-architect-backend/   # Spring Boot REST API
│   ├── src/main/java/com/aiarchitect/
│   │   ├── config/                  # SecurityConfig, GeminiConfig, CorsConfig
│   │   ├── controller/              # AuthController, ProjectController, ExportController
│   │   ├── service/                 # AuthService, ProjectService, ExportService
│   │   ├── repository/              # UserRepository, ProjectRepository, etc.
│   │   ├── model/                   # JPA Entity classes (User, Project, Report)
│   │   └── security/                # JwtAuthenticationFilter, JwtTokenProvider
│   └── Dockerfile                   # Multi-stage maven build wrapper
│
├── ai-software-architect-frontend/  # React SPA (Vite + Nginx)
│   ├── src/
│   │   ├── components/              # Common (Navbar, Loader), Report (MermaidRenderer)
│   │   ├── pages/                   # LoginPage, RegisterPage, DashboardPage, etc.
│   │   ├── context/                 # Session AuthContext state provider
│   │   └── services/                # Axios api client configurations
│   ├── nginx.conf                   # Nginx reverse proxy routing definition
│   └── Dockerfile                   # Nginx multi-stage node builder
│
└── docker-compose.yml               # Multi-container orchestration (MySQL + Backend + Frontend)
```

---

## ⚙️ Local Setup Guide

### Prerequisites
- **Java 17 JDK**
- **Maven** (or use the packaged `mvnw` wrapper)
- **Google AI Studio API Key** (Set as environment variable `GEMINI_API_KEY`)

### Running the Backend (Dev Profile - H2 DB)
1. Navigate to the backend directory:
   ```bash
   cd ai-software-architect-backend
   ```
2. Build the application:
   ```bash
   ./mvnw compile
   ```
3. Run the Spring Boot server:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   *The server starts on port `8080`.*
   *Health checks: `http://localhost:8080/api/v1/health`*
   *Swagger API Documentation: `http://localhost:8080/swagger-ui.html`*
   *H2 console database viewer: `http://localhost:8080/h2-console`*

---

## 🐳 Docker Compose Orchestration (Production Build)

Launch the entire stack (MySQL + Backend API + Frontend served via Nginx on port 80) in one command:

1. Ensure the `GEMINI_API_KEY` is exported on your host shell.
2. From the workspace root directory, run:
   ```bash
   docker-compose up --build
   ```
3. Open `http://localhost` in your browser to interact with the full application.
