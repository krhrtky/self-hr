# API Infrastructure (Terraform)

## Overview

This Terraform module provisions the AWS infrastructure for the backend API. It sets up a Virtual Private Cloud (VPC) with public and private subnets, an Elastic Container Registry (ECR) for storing Docker images, an Elastic Container Service (ECS) cluster with a Fargate service for running the API, and an Application Load Balancer (ALB) to distribute traffic to the API. It also includes necessary IAM roles, security groups, and CloudWatch logging.

## Prerequisites

*   **Terraform:** Ensure Terraform (version 1.x or later) is installed.
*   **AWS CLI:** Ensure the AWS CLI is installed and configured with an AWS account and necessary permissions to create the resources defined in this module.
*   **Terraform Backend:** This module is configured to use an S3 backend for state storage and a DynamoDB table for state locking. These resources (S3 bucket, DynamoDB table) must be created manually before initializing this module. You can refer to a general AWS setup guide for creating these backend resources.

## Directory Structure

*   `main.tf`: Contains the primary set of resource definitions for the API infrastructure (VPC, ECR, ECS, ALB, etc.).
*   `variables.tf`: Defines input variables used to configure the infrastructure (e.g., region, environment name, CIDR blocks, container settings).
*   `outputs.tf`: Specifies the output values from the module after provisioning (e.g., ALB DNS name, ECR repository URL).

## Variables

Below are some of the key variables defined in `variables.tf`. You can set these using a `.tfvars` file or by passing them as command-line arguments.

*   `region`: (string) AWS region for the infrastructure (default: "us-east-1").
*   `environment`: (string) Deployment environment (e.g., "dev", "staging", "prod") (default: "dev").
*   `app_name`: (string) Name of the application (default: "my-api").
*   `vpc_cidr_block`: (string) CIDR block for the VPC (default: "10.0.0.0/16").
*   `public_subnet_a_cidr_block`: (string) CIDR block for public subnet in AZ A.
*   `public_subnet_b_cidr_block`: (string) CIDR block for public subnet in AZ B.
*   `private_subnet_a_cidr_block`: (string) CIDR block for private subnet in AZ A.
*   `private_subnet_b_cidr_block`: (string) CIDR block for private subnet in AZ B.
*   `container_port`: (number) Port the container listens on (default: 8080).
*   `ecs_task_cpu`: (string) CPU units for the ECS task (default: "1024").
*   `ecs_task_memory`: (string) Memory for the ECS task (in MiB) (default: "2048").
*   `log_retention_days`: (number) Number of days to retain CloudWatch logs (default: 7).
*   `ecs_service_desired_count`: (number) Desired number of tasks for the ECS service (default: 2).
*   `health_check_path`: (string) Path for ALB health checks (default: "/health").
*   `container_environment_variables`: (list(object)) Environment variables for the container.

Create a `terraform.tfvars` file (e.g., `dev.tfvars`) in this directory to specify your variable values:
```hcl
region        = "us-east-1"
environment   = "dev"
app_name      = "my-cool-api"
# ... other variables
```

## Initialization

Navigate to the module directory and initialize Terraform. You will need to provide a backend configuration file or pass the configuration via the command line.

Example backend configuration file (`backend-config.hcl`):
```hcl
bucket         = "your-terraform-state-bucket-name"
key            = "api/dev/terraform.tfstate" # Example key, adjust for your environment
region         = "us-east-1" # Region where the S3 bucket and DynamoDB table exist
dynamodb_table = "your-terraform-state-lock-table"
encrypt        = true
```

Then run:
```bash
cd infrastructure/aws/resources/api
terraform init -backend-config=path/to/your/backend-config.hcl
```

## Planning

Generate an execution plan to preview the changes Terraform will make:
```bash
terraform plan -var-file=path/to/your-dev.tfvars # Or your specific .tfvars file
```

## Applying

Apply the changes to provision the infrastructure:
```bash
terraform apply -var-file=path/to/your-dev.tfvars
```
Confirm the action by typing `yes` when prompted.

## Destroying

To remove all resources provisioned by this module:
```bash
terraform destroy -var-file=path/to/your-dev.tfvars
```
Confirm the action by typing `yes` when prompted.

## Outputs

After successful application, important outputs such as the Application Load Balancer DNS name and ECR repository URL are available. You can view them using:
```bash
terraform output
```
Specific outputs can also be queried:
```bash
terraform output alb_dns_name
terraform output ecr_repository_url
```
These outputs are defined in `outputs.tf`.
