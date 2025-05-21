# Project Title (Replace with Actual Project Title)

This is the main README for the project. It provides an overview of the project structure, setup, and deployment.

## Table of Contents

- [Project Structure](#project-structure)
- [Local Development](#local-development)
- [AWS Infrastructure & Deployment](#aws-infrastructure--deployment)
- [Backend](#backend)
- [Frontend](#frontend)
- [Contributing](#contributing)
- [License](#license)

## Project Structure

Briefly describe the main directories and their purpose:
*   `/backend`: Contains the Spring Boot Kotlin backend API.
*   `/front`: Contains the frontend application.
*   `/infrastructure`: Contains Terraform code for AWS infrastructure.
*   `/docker`: Contains Docker configurations for local development (e.g., localstack, database).
*   `/otel`: OpenTelemetry collector configuration.
*   `/fluent-bit`: Fluent Bit configuration for log aggregation.
*   `/traefik`: Traefik proxy configuration for local development.

## Local Development

### Prerequisites
*   Docker and Docker Compose
*   JDK (version specified in `backend/api/Dockerfile`, e.g., Corretto 21)
*   Node.js and pnpm (for frontend development, version in `front/app/package.json`)
*   `make` (optional, for using Makefile shortcuts)

### Running Locally with Docker Compose
The easiest way to run the entire application stack (backend, frontend, database, etc.) locally is using Docker Compose:
```bash
# Create a .env file from the example if you haven't already
# cp .env.example .env
# Update .env with your local configurations if needed

docker-compose up --build
```
This will:
*   Build the backend API Docker image.
*   Start all services defined in `docker-compose.yaml`, including the database, API, frontend (if configured), Traefik, OpenTelemetry collector, and Fluent Bit.
*   The API will typically be accessible at `http://localhost:8080` or via Traefik at `http://localhost`.
*   The frontend (if served by Docker Compose) will be accessible at its configured port (e.g., `http://localhost:3000`).

### Running Backend Independently
To run the backend API directly (e.g., from your IDE or using Gradle):
1.  Ensure you have a PostgreSQL database running and accessible. You can use the one from `docker-compose up db` or a separate instance.
2.  Set the required environment variables for database connection (`DB_URL`, `DB_USER`, `DB_PASSWORD`) and any other necessary configurations (see `.env.example` or `backend/api/Dockerfile`).
3.  Navigate to the backend directory:
    ```bash
    cd backend/api
    ./gradlew bootRun
    ```

### Running Frontend Independently
Refer to the frontend's README at `front/app/README.md` for specific instructions on local development. Typically:
```bash
cd front/app
pnpm install
pnpm dev
```

## AWS Infrastructure & Deployment

### Overview
The API is designed to be deployed to Amazon Web Services (AWS). The infrastructure leverages services like Elastic Container Service (ECS) for running the containerized application, Application Load Balancer (ALB) for traffic distribution, Elastic Container Registry (ECR) for Docker image storage, and Virtual Private Cloud (VPC) for network isolation.

### Infrastructure Provisioning
The AWS infrastructure is managed using Terraform.

*   **API Infrastructure**: Detailed information on setting up the core API infrastructure (VPC, ECR, ECS, ALB, etc.) can be found in the dedicated README:
    *   [API Infrastructure Setup Guide](./infrastructure/aws/resources/api/README.md)
*   **Cognito**: User authentication and management are handled by AWS Cognito. The Terraform configuration for Cognito is managed separately.
    *   See the Cognito infrastructure directory: [./infrastructure/aws/resources/cognito/](./infrastructure/aws/resources/cognito/)

### Deployment Process
Deployments to AWS are automated using GitHub Actions. The workflow is defined in `.github/workflows/docker-image-push.yml`.

*   **Trigger**: Pushing changes to the `main` branch automatically triggers the deployment pipeline.
*   **Process**:
    1.  The backend API's Docker image is built.
    2.  The image is pushed to Amazon ECR.
    3.  The Amazon ECS service is updated with the new Docker image, which triggers a new deployment of the application.
*   **Required Secrets for GitHub Actions**: The workflow requires the following secrets to be configured in the GitHub repository settings:
    *   `AWS_ACCESS_KEY_ID`
    *   `AWS_SECRET_ACCESS_KEY`
    *   `AWS_REGION`
    *   `ECR_REPOSITORY_NAME` (The name of the ECR repository)
    *   `ECS_CLUSTER_NAME` (The name of the ECS cluster)
    *   `ECS_SERVICE_NAME` (The name of the ECS service)
    *   `ECS_TASK_DEFINITION_FAMILY` (The family name of the ECS task definition)
    *   `ECS_CONTAINER_NAME` (The name of the container defined in the task definition)

## Backend
The backend is a Spring Boot application written in Kotlin.
*   **Source Code**: `backend/`
*   **API Definition**: GraphQL schema at `schema.graphql`.
*   **Key Technologies**: Spring Boot, Kotlin, Gradle, GraphQL (DGS), PostgreSQL.

## Frontend
The frontend is a [Specify Frontend Technology, e.g., React/Vue/Angular] application.
*   **Source Code**: `front/app/`
*   **Key Technologies**: [Specify, e.g., TypeScript, React, Vite, Tailwind CSS]
*   Refer to `front/app/README.md` for more details.


## Contributing
[Information about contributing to the project, coding standards, pull request process, etc.]

## License
[Specify the project license, e.g., MIT, Apache 2.0]
