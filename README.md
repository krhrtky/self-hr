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

### Legacy Local Development (Manual Setup)

This section describes running services independently or with a simpler Docker Compose setup that does not involve LocalStack for AWS service emulation.

#### Running Backend Independently
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

## Local Development with LocalStack (Recommended for Cloud Features)

This project supports a local development environment that simulates AWS services using LocalStack. This allows for testing cloud interactions locally, reducing reliance on shared development AWS accounts and providing a more consistent development experience.

### Prerequisites
*   Docker and Docker Compose installed.
*   (Optional but Recommended) AWS CLI (`aws`) installed for interacting with LocalStack. The `awslocal` command (part of the `awscli-local` package) is used in the initialization script and is the preferred tool for LocalStack interaction. You can install it via `pip install awscli-local`.

### Setup & Configuration
*   **LocalStack Service:** LocalStack is configured as a service in `docker-compose.yaml`. It's set up to expose common AWS service ports and persist data in `./tmp/localstack`.
*   **Initialization Script (`docker/localstack/init-aws.sh`):**
    *   This script automatically provisions a suite of AWS resources inside LocalStack when it starts up.
    *   Key resources it attempts to create include:
        *   VPC and Subnets
        *   ECR Repository (for the application image)
        *   ECS Cluster
        *   Cognito User Pool, User Pool Client, and User Pool Domain
        *   Application Load Balancer (ALB) with an HTTP listener and an experimental HTTPS listener with Cognito authentication
        *   Target Group for the `app` service
    *   The script is mounted into LocalStack and executed when the `ready.d` hook is triggered.
*   **Environment Variables:**
    *   The `docker-compose.yaml` file sets default AWS configuration for the `app` service to point to LocalStack (e.g., `AWS_ENDPOINT_URL: http://localstack:4566`).
    *   You can define `AWS_REGION` in your `.env` file if you need to use a region other than the default (`us-east-1`) specified in `docker-compose.yaml` for LocalStack and the `app` service.

### Running the Local Environment with LocalStack
1.  **Ensure your `.env` file is present.** If not, you might want to copy `.env.example` to `.env` and adjust as needed, though for LocalStack, most AWS credentials in `.env` are overridden by the Docker Compose settings for the `app` service.
2.  **Start the environment:**
    ```bash
    docker-compose up -d
    ```
    To view logs from all services (including LocalStack's initialization):
    ```bash
    docker-compose up
    ```
3.  **Rebuilding services (e.g., if you change `backend/api/Dockerfile`):**
    ```bash
    docker-compose build app # Or specific service
    docker-compose up -d
    ```
    Or to rebuild all:
    ```bash
    docker-compose build && docker-compose up -d
    ```
    On the first startup, or after deleting `./tmp/localstack`, LocalStack will run the `init-aws.sh` script, which can take a few minutes.

### Accessing Services
*   **Application (`app` service):**
    *   The `init-aws.sh` script attempts to set up an Application Load Balancer (ALB) within LocalStack. The script outputs the local ALB DNS name (e.g., `http://local-app-alb.localhost.localstack.cloud:4566` or similar). However, directly accessing this DNS name from your host machine might require additional host file configuration or specific LocalStack networking setups.
    *   **Primary access for the API will likely be via `http://localhost:4566` for the ALB, using a Host header matching the ALB name, or through specific service endpoints if LocalStack's ALB routing doesn't work as expected out-of-the-box.**
    *   See the "Connecting LocalStack ALB to the `app` Service" section below for important caveats.
*   **LocalStack General Endpoint / Web UI:**
    *   LocalStack services are accessible via the main endpoint: `http://localhost:4566`.
    *   LocalStack used to have a basic Web UI. For LocalStack 3.0+, a more advanced UI is part of LocalStack Pro or LocalStack Desktop. You can check `http://localhost:4566/` for any available default dashboard.
*   **Cognito (Local):**
    *   The `init-aws.sh` script outputs `LOCAL_COGNITO_USER_POOL_ID`, `LOCAL_COGNITO_CLIENT_ID`, and `LOCAL_COGNITO_DOMAIN_PREFIX`. These can be used with `awslocal` to interact with the local Cognito service.
    *   A typical Cognito login form URL pattern (might vary with LocalStack versions and setup) is:
        `http://localhost:4566/cognito-idp/login?response_type=code&client_id=<YOUR_LOCAL_CLIENT_ID>&redirect_uri=<YOUR_APP_CALLBACK_URL_CONFIGURED_IN_COGNITO_CLIENT>&scope=openid+profile+email`
        (Note: The hostname for the Cognito UI might be `localhost.localstack.cloud` or require specific configuration. Refer to LocalStack's documentation for the exact structure of the hosted UI URL for Cognito.)

### Interacting with LocalStack AWS Services
You can use `awslocal` (or `aws` CLI configured with `--endpoint-url=http://localhost:4566`) to inspect and manage resources within LocalStack.
*   **Examples:**
    ```bash
    awslocal s3 ls --region ${AWS_REGION:-us-east-1}
    awslocal ecr describe-repositories --region ${AWS_REGION:-us-east-1}
    awslocal cognito-idp list-user-pools --max-results 10 --region ${AWS_REGION:-us-east-1}
    awslocal elbv2 describe-load-balancers --region ${AWS_REGION:-us-east-1}
    awslocal elbv2 describe-target-groups --region ${AWS_REGION:-us-east-1}
    ```

### Connecting LocalStack ALB to the `app` Service (Important Caveat)
A known challenge with the current setup is the automatic registration of the `app` service (running as a Docker container) with the Target Group created by the LocalStack ALB.
*   The `init-aws.sh` script creates an ALB and a Target Group (typically named `local-app-tg`).
*   However, the `app` container's IP address (on the Docker network) is not automatically registered with this Target Group by the current script.
*   **Manual Registration Steps (after `docker-compose up`):**
    1.  Identify the `app` container's ID: `docker ps`
    2.  Find the `app` container's IP address on the relevant Docker network (usually the network named `<project_name>_default`):
        ```bash
        docker inspect <app_container_id_or_name> | jq -r '.[0].NetworkSettings.Networks | to_entries[0].value.IPAddress'
        # Replace <project_name> if your Docker network has a different name.
        ```
    3.  Get the Target Group ARN (output by `init-aws.sh` during startup, or use `awslocal elbv2 describe-target-groups`).
    4.  Register the `app` container with the Target Group:
        ```bash
        awslocal elbv2 register-targets \
          --target-group-arn <target_group_arn_from_init_script_or_awslocal> \
          --targets Id=<app_container_ip_address>,Port=8080 \
          --region ${AWS_REGION:-us-east-1}
        ```
*   **Alternative for Simpler Testing:** If you don't need to test the full ALB/Cognito flow, you can expose the `app` service's port directly in `docker-compose.yaml` (e.g., `ports: - "8081:8080"`) and access it via `http://localhost:8081`, bypassing the LocalStack ALB.

### Limitations & Known Issues
*   **ALB with Cognito Authentication:** The `authenticate-cognito` action for ALB listeners is an advanced feature. Its simulation in LocalStack (especially in the free tier) might be incomplete or behave differently than real AWS. The `init-aws.sh` script attempts this setup, but it requires thorough testing.
*   **HTTPS for ALB:** The `init-aws.sh` script attempts to set up an HTTPS listener using a placeholder certificate ARN (`arn:aws:acm:us-east-1:000000000000:certificate/placeholder-cert-arn`). For true HTTPS locally with valid certificates, significant additional configuration would be needed, which is beyond the scope of the current LocalStack setup. Expect certificate errors if accessing the HTTPS endpoint directly.
*   **Service Discovery:** DNS resolution for services within LocalStack (like ALB DNS names) might not work automatically from your host machine without additional configuration (e.g., editing `/etc/hosts` or using LocalStack's DNS server if available and configured).
*   **Persistence:** LocalStack is configured with `PERSISTENCE=1` and mounts `./tmp/localstack` to `/var/lib/localstack`. This means AWS resource state *should* be saved across `docker-compose down` and `up`. To reset LocalStack's state completely, delete the `./tmp/localstack` directory.

### Troubleshooting
*   **Check LocalStack Logs:** `docker-compose logs localstack`. Look for output from `init-aws.sh` and any service errors.
*   **Check `app` Service Logs:** `docker-compose logs app`.
*   **Inspect Docker Networks:** `docker network ls`, `docker network inspect <network_name>`.
*   **Ensure `init-aws.sh` is Executable:** It should have been made executable in previous steps (`chmod +x docker/localstack/init-aws.sh`).

## AWS Infrastructure & Deployment

### Overview
The API is designed to be deployed to Amazon Web Services (AWS). The infrastructure leverages services like Elastic Container Service (ECS) for running the containerized application, Application Load Balancer (ALB) for traffic distribution, Elastic Container Registry (ECR) for Docker image storage, and Virtual Private Cloud (VPC) for network isolation. **The API is protected by Amazon Cognito authentication at the Application Load Balancer level, meaning access to API endpoints requires users to authenticate via the configured Cognito User Pool.** All traffic is enforced to use HTTPS.

### Infrastructure Provisioning
The AWS infrastructure is managed using Terraform.

*   **API Infrastructure**: Detailed information on setting up the core API infrastructure (VPC, ECR, ECS, ALB, etc.) can be found in the dedicated README. **This setup includes the integration of the Application Load Balancer with Amazon Cognito for authentication.**
    *   [API Infrastructure Setup Guide](./infrastructure/aws/resources/api/README.md)
*   **Cognito**: User authentication and management are handled by AWS Cognito. The Terraform configuration for Cognito is managed separately and must be deployed *before* the API infrastructure.
    *   See the Cognito infrastructure directory: [./infrastructure/aws/resorces/cognito/](./infrastructure/aws/resorces/cognito/) (Note: "resorces" is the actual directory name with a typo).

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
