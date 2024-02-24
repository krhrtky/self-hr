terraform {
  required_version = "1.7.4"

  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "5.31.0"
    }
  }
}

provider "aws" {
  region     = "us-east-1"
  access_key = "dummy"
  secret_key = "dummy"
}

variable "cognito_user_email" {
  default = "sample@example.com"
}

variable "cognito_user_password" {
  default = "password"
}

resource "null_resource" "delete_mock_cognito_properties" {
  triggers = {
    always_run = "${timestamp()}"
  }

  provisioner "local-exec" {
    command = "rm -f /output/mock-cognito.properties"
  }
}

resource "aws_cognito_user_pool" "my_user_pool" {
  name = "MyUserPool"

  alias_attributes = ["email"]
  username_attributes = ["email"]

  schema {
    attribute_data_type = "String"
    name               = "email"
    required           = true
  }
}

resource "aws_cognito_user_pool_client" "my_user_pool_client" {
  name         = "MyUserPoolClient"
  user_pool_id = aws_cognito_user_pool.my_user_pool.id
  generate_secret = true
  explicit_auth_flows = ["ADMIN_USER_PASSWORD_AUTH"]
}

resource "aws_cognito_user" "admin_user" {
  user_pool_id          = aws_cognito_user_pool.my_user_pool.id
  username              = var.cognito_user_email
  message_action        = "SUPPRESS"
  desired_delivery_mediums = ["EMAIL"]

  user_attributes = [
    {
      name  = "email"
      value = var.cognito_user_email
    },
    {
      name  = "email_verified"
      value = "true"
    },
  ]
}

resource "null_resource" "admin_set_user_password" {
  triggers = {
    always_run = aws_cognito_user.admin_user.id
  }

  provisioner "local-exec" {
    command = <<EOT
      aws cognito-idp admin-set-user-password \
        --user-pool-id ${aws_cognito_user_pool.my_user_pool.id} \
        --username ${var.cognito_user_email} \
        --password ${var.cognito_user_password} \
        --permanent
    EOT
  }
}

output "user_pool_id" {
  value = aws_cognito_user_pool.my_user_pool.id
}

output "client_id" {
  value = aws_cognito_user_pool_client.my_user_pool_client.client_id
}
