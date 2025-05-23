variable "region" {
  description = "AWS region for the infrastructure"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Deployment environment (e.g., dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "app_name" {
  description = "Name of the application"
  type        = string
  default     = "my-api"
}

variable "vpc_cidr_block" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_a_cidr_block" {
  description = "CIDR block for public subnet in AZ A"
  type        = string
  default     = "10.0.1.0/24"
}

variable "public_subnet_b_cidr_block" {
  description = "CIDR block for public subnet in AZ B"
  type        = string
  default     = "10.0.2.0/24"
}

variable "private_subnet_a_cidr_block" {
  description = "CIDR block for private subnet in AZ A"
  type        = string
  default     = "10.0.3.0/24"
}

variable "private_subnet_b_cidr_block" {
  description = "CIDR block for private subnet in AZ B"
  type        = string
  default     = "10.0.4.0/24"
}

variable "container_port" {
  description = "Port the container listens on"
  type        = number
  default     = 8080
}

variable "ecs_task_cpu" {
  description = "CPU units for the ECS task"
  type        = string
  default     = "1024" # 1 vCPU
}

variable "ecs_task_memory" {
  description = "Memory for the ECS task (in MiB)"
  type        = string
  default     = "2048" # 2GB
}

variable "log_retention_days" {
  description = "Number of days to retain CloudWatch logs"
  type        = number
  default     = 7
}

variable "ecs_service_desired_count" {
  description = "Desired number of tasks for the ECS service"
  type        = number
  default     = 2
}

variable "ecs_service_health_check_grace_period_seconds" {
  description = "Grace period for ECS service health checks"
  type        = number
  default     = 60
}

variable "health_check_path" {
  description = "Path for ALB health checks"
  type        = string
  default     = "/health"
}

variable "container_environment_variables" {
  description = "Environment variables for the container (list of objects with name and value)"
  type        = list(object({ name = string, value = string }))
  default     = []
  # Example:
  # [
  #   { name = "DB_HOST", value = "mydb.example.com" },
  #   { name = "DB_PORT", value = "5432" }
  # ]
}

variable "cognito_state_bucket" {
  description = "Name of the S3 bucket where the Cognito module's Terraform state is stored."
  type        = string
}

variable "cognito_state_key" {
  description = "Path to the Cognito module's Terraform state file in the S3 bucket."
  type        = string
}

variable "alb_certificate_arn" {
  description = "ARN of the ACM certificate for the ALB HTTPS listener."
  type        = string
}
