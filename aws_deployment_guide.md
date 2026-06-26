# AI Software Architect — AWS Deployment Guide

This guide walks you through deploying the **AI Software Architect** full-stack application to **Amazon Web Services (AWS)** using standard enterprise best practices.

---

## 1. Production Architecture Overview

In a professional AWS environment, we distribute components across public and private subnets inside a Custom VPC (Virtual Private Cloud) to optimize security and reliability:

- **Public Subnet**: Holds an **Application Load Balancer (ALB)** and the React frontend static build hosted on **Amazon S3** + **Amazon CloudFront** (CDN).
- **Private Subnet**: Holds the Spring Boot Backend API running on **Amazon ECS (Elastic Container Service)** with Fargate (serverless container execution) and the database running on **Amazon RDS MySQL**.
- **Security Group Rules**: The database only accepts inbound traffic from the backend ECS containers. The backend ECS containers only accept inbound traffic from the ALB.

---

## 2. Step-by-Step Deployment Flow

### Step 1: Set Up the Database (Amazon RDS MySQL)
1. Navigate to the RDS Console and click **Create Database**.
2. Select **MySQL** as the Engine. Choose the Free Tier template for staging/development.
3. Configure the database identifier (e.g. `ai-architect-db`) and master credentials.
4. Under **Connectivity**, place it in your custom VPC inside the **Private Subnets**. Set **Public Access** to **No**.
5. Create a Security Group (e.g. `rds-sg`) allowing inbound TCP traffic on port `3306` only from the backend container security group.

### Step 2: Build and Push Backend Container to Amazon ECR
1. Navigate to the **Amazon Elastic Container Registry (ECR)** and create a repository named `ai-software-architect-backend`.
2. Authenticate your local Docker client with ECR:
   ```bash
   aws ecr get-login-password --region <your-region> | docker login --username AWS --password-stdin <your-account-id>.dkr.ecr.<your-region>.amazonaws.com
   ```
3. Build the backend Docker image:
   ```bash
   docker build -t ai-software-architect-backend ./ai-software-architect-backend
   ```
4. Tag and push to ECR:
   ```bash
   docker tag ai-software-architect-backend:latest <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/ai-software-architect-backend:latest
   docker push <your-account-id>.dkr.ecr.<your-region>.amazonaws.com/ai-software-architect-backend:latest
   ```

### Step 3: Run Backend on Amazon ECS (Fargate)
1. Open the **ECS Console** and create a Cluster (e.g. `ai-architect-cluster`).
2. Create a **Task Definition** (Fargate type):
   - Set task memory to `1GB` and CPU to `0.5 vCPU`.
   - Add a container. Point the image URL to your ECR registry path: `<your-account-id>.dkr.ecr.<your-region>.amazonaws.com/ai-software-architect-backend:latest`.
   - Map port `8080` (TCP).
   - Define **Environment Variables**:
     - `SPRING_PROFILES_ACTIVE`: `prod`
     - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<rds-endpoint>:3306/ai_architect_db`
     - `DB_USERNAME`: `<your-rds-username>`
     - `DB_PASSWORD`: `<your-rds-password>`
     - `JWT_SECRET`: `<your-secure-jwt-secret>`
     - `GEMINI_API_KEY`: `<your-google-ai-studio-api-key>`
3. Create an **ECS Service** referencing the Task Definition to launch the container. Configure an ALB to balance requests on port `8080`.

---

## 3. Deployment Summary Checklist
- [x] Custom VPC with Private and Public Subnets configured.
- [x] RDS Instance launched in private subnet with security group restricted to port 3306.
- [x] ECS Fargate tasks running ECR image with Spring `prod` profile.
- [x] Application Load Balancer routing `/api/**` traffic to ECS and other traffic to frontend.
