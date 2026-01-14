#!/bin/bash
echo "LocalStack init script started."
set -e # Exit on error for most commands

DEFAULT_REGION=${AWS_REGION:-us-east-1}
APP_NAME="my-app" # Should match var.app_name if possible
ENVIRONMENT="local" # Should match var.environment

echo "Using region: $DEFAULT_REGION"
echo "App name: $APP_NAME"
echo "Environment: $ENVIRONMENT"

# 1. Network (VPC, Subnets) - Optional but good practice
echo "Creating VPC and Subnets..."
VPC_ID=$(awslocal ec2 create-vpc --cidr-block 10.0.0.0/16 --query Vpc.VpcId --output text)
echo "VPC_ID: $VPC_ID"
awslocal ec2 create-tags --resources $VPC_ID --tags Key=Name,Value=$ENVIRONMENT-vpc

SUBNET_PUBLIC_A_ID=$(awslocal ec2 create-subnet --vpc-id $VPC_ID --cidr-block 10.0.1.0/24 --availability-zone ${DEFAULT_REGION}a --query Subnet.SubnetId --output text)
echo "SUBNET_PUBLIC_A_ID: $SUBNET_PUBLIC_A_ID"
awslocal ec2 create-tags --resources $SUBNET_PUBLIC_A_ID --tags Key=Name,Value=$ENVIRONMENT-public-subnet-a

SUBNET_PUBLIC_B_ID=$(awslocal ec2 create-subnet --vpc-id $VPC_ID --cidr-block 10.0.2.0/24 --availability-zone ${DEFAULT_REGION}b --query Subnet.SubnetId --output text)
echo "SUBNET_PUBLIC_B_ID: $SUBNET_PUBLIC_B_ID"
awslocal ec2 create-tags --resources $SUBNET_PUBLIC_B_ID --tags Key=Name,Value=$ENVIRONMENT-public-subnet-b

# (Add private subnets if your local setup specifically needs to test private networking)

# 2. ECR Repository
echo "Creating ECR repository..."
ECR_REPO_NAME="${ENVIRONMENT}-${APP_NAME}"
awslocal ecr create-repository --repository-name "$ECR_REPO_NAME" --region "$DEFAULT_REGION" || echo "ECR repo $ECR_REPO_NAME likely already exists."
echo "ECR Repository: $ECR_REPO_NAME"

# 3. ECS Cluster
echo "Creating ECS cluster..."
ECS_CLUSTER_NAME="${ENVIRONMENT}-ecs-cluster"
awslocal ecs create-cluster --cluster-name "$ECS_CLUSTER_NAME" --region "$DEFAULT_REGION" || echo "ECS cluster $ECS_CLUSTER_NAME likely already exists."
echo "ECS Cluster: $ECS_CLUSTER_NAME"

# 4. Cognito User Pool, Client, and Domain
echo "Creating Cognito User Pool, Client, and Domain..."
COGNITO_USER_POOL_NAME="${ENVIRONMENT}-user-pool"
USER_POOL_ID=$(awslocal cognito-idp create-user-pool --pool-name "$COGNITO_USER_POOL_NAME" --query UserPool.Id --output text --region "$DEFAULT_REGION")
echo "Cognito User Pool ID: $USER_POOL_ID"

if [ -z "$USER_POOL_ID" ]; then
  echo "Failed to create Cognito User Pool. Exiting."
  exit 1
fi

COGNITO_CLIENT_NAME="${ENVIRONMENT}-app-client"
USER_POOL_CLIENT_ID=$(awslocal cognito-idp create-user-pool-client \
  --user-pool-id "$USER_POOL_ID" \
  --client-name "$COGNITO_CLIENT_NAME" \
  --no-generate-secret \
  --explicit-auth-flows ADMIN_NO_SRP_AUTH USER_PASSWORD_AUTH \
  --callback-urls "http://localhost:80/oauth2/idpresponse" "http://localhost/callback" \ # Placeholder callback
  --logout-urls "http://localhost/logout" \ # Placeholder logout
  --allowed-oauth-flows code id_token \
  --allowed-oauth-scopes email openid profile \
  --supported-identity-providers COGNITO \
  --query UserPoolClient.ClientId --output text --region "$DEFAULT_REGION")
echo "Cognito User Pool Client ID: $USER_POOL_CLIENT_ID"

if [ -z "$USER_POOL_CLIENT_ID" ]; then
  echo "Failed to create Cognito User Pool Client. Exiting."
  exit 1
fi

COGNITO_DOMAIN_PREFIX="${ENVIRONMENT}-${APP_NAME}-auth"
awslocal cognito-idp create-user-pool-domain --user-pool-id "$USER_POOL_ID" --domain "$COGNITO_DOMAIN_PREFIX" --region "$DEFAULT_REGION"
echo "Cognito Domain: ${COGNITO_DOMAIN_PREFIX}.auth.${DEFAULT_REGION}.amazoncognito.com (Note: LocalStack might use a different URL structure)"

# Output Cognito details for potential use by the application or testing
echo "LOCAL_COGNITO_USER_POOL_ID=$USER_POOL_ID"
echo "LOCAL_COGNITO_CLIENT_ID=$USER_POOL_CLIENT_ID"
echo "LOCAL_COGNITO_DOMAIN_PREFIX=$COGNITO_DOMAIN_PREFIX"

# 5. Application Load Balancer (ALB) and Target Group
# This is the most complex part and might have limitations in LocalStack.
echo "Creating ALB, Target Group, and Listener..."

ALB_NAME="${ENVIRONMENT}-app-alb"
TG_NAME="${ENVIRONMENT}-app-tg"

# Create Security Group for ALB (allow all for local simplicity)
SG_ALB_ID=$(awslocal ec2 create-security-group --group-name "${ENVIRONMENT}-alb-sg" --description "ALB SG for local" --vpc-id "$VPC_ID" --query GroupId --output text)
echo "ALB Security Group ID: $SG_ALB_ID"
# Allow inbound HTTP/HTTPS (for local, often all ports are open by default from host)
awslocal ec2 authorize-security-group-ingress --group-id "$SG_ALB_ID" --protocol tcp --port 80 --cidr 0.0.0.0/0
awslocal ec2 authorize-security-group-ingress --group-id "$SG_ALB_ID" --protocol tcp --port 443 --cidr 0.0.0.0/0


ALB_ARN=$(awslocal elbv2 create-load-balancer \
  --name "$ALB_NAME" \
  --subnets "$SUBNET_PUBLIC_A_ID" "$SUBNET_PUBLIC_B_ID" \
  --security-groups "$SG_ALB_ID" \
  --scheme internet-facing \
  --type application \
  --query 'LoadBalancers[0].LoadBalancerArn' --output text --region "$DEFAULT_REGION" 2>/dev/null || echo "Failed to create ALB. This might be a LocalStack limitation or misconfiguration.")

if [ -z "$ALB_ARN" ]; then
  echo "ALB creation failed. Skipping ALB listener and target group setup."
else
  echo "ALB ARN: $ALB_ARN"
  ALB_DNS_NAME=$(awslocal elbv2 describe-load-balancers --load-balancer-arns "$ALB_ARN" --query 'LoadBalancers[0].DNSName' --output text --region "$DEFAULT_REGION")
  echo "ALB DNS Name (LocalStack): $ALB_DNS_NAME" # This will be a LocalStack internal DNS

  TARGET_GROUP_ARN=$(awslocal elbv2 create-target-group \
    --name "$TG_NAME" \
    --protocol HTTP \
    --port 8080 \ # Port your 'app' service runs on
    --vpc-id "$VPC_ID" \
    --target-type ip \
    --health-check-protocol HTTP \
    --health-check-port traffic-port \
    --health-check-path /actuator/health \ # Assuming Spring Boot actuator
    --query 'TargetGroups[0].TargetGroupArn' --output text --region "$DEFAULT_REGION")
  echo "Target Group ARN: $TARGET_GROUP_ARN"

  # Attempt to create HTTPS listener with Cognito authentication
  # This requires a certificate. For LocalStack, we often skip cert validation or use a dummy one.
  # LocalStack might not fully support `authenticate-cognito` action type.
  # We will create a simple HTTP listener first, then attempt HTTPS if possible.

  echo "Creating HTTP listener (port 80) forwarding to Target Group $TG_NAME..."
  awslocal elbv2 create-listener \
    --load-balancer-arn "$ALB_ARN" \
    --protocol HTTP \
    --port 80 \
    --default-actions Type=forward,TargetGroupArn="$TARGET_GROUP_ARN" \
    --region "$DEFAULT_REGION" || echo "Failed to create HTTP listener."

  echo "Attempting to create HTTPS listener (port 443) with Cognito Auth..."
  # For HTTPS, ACM certificate is needed. In LocalStack, this is often tricky.
  # We might need to create a dummy cert or LocalStack might have a default one.
  # The `authenticate-cognito` action might not be fully supported.
  # This part is experimental for LocalStack.
  USER_POOL_ARN="arn:aws:cognito-idp:$DEFAULT_REGION:000000000000:userpool/$USER_POOL_ID" # Construct User Pool ARN for LocalStack

  set +e # Do not exit on error for this experimental part
  awslocal elbv2 create-listener \
    --load-balancer-arn "$ALB_ARN" \
    --protocol HTTPS \
    --port 443 \
    --certificates CertificateArn=arn:aws:acm:$DEFAULT_REGION:000000000000:certificate/placeholder-cert-arn \ # Placeholder/dummy cert ARN
    --ssl-policy ELBSecurityPolicy-2016-08 \
    --default-actions \
      "[{\"Type\":\"authenticate-cognito\",\"Order\":1,\"AuthenticateCognitoConfig\":{\"UserPoolArn\":\"$USER_POOL_ARN\",\"UserPoolClientId\":\"$USER_POOL_CLIENT_ID\",\"UserPoolDomain\":\"$COGNITO_DOMAIN_PREFIX\",\"OnUnauthenticatedRequest\":\"authenticate\",\"Scope\":\"openid\",\"SessionCookieName\":\"AWSELBAuthSessionCookie-0\"}},{\"Type\":\"forward\",\"Order\":2,\"TargetGroupArn\":\"$TARGET_GROUP_ARN\"}]" \
    --region "$DEFAULT_REGION"
  
  if [ $? -eq 0 ]; then
    echo "HTTPS listener with Cognito auth action *attempted*. Check LocalStack logs for success/failure."
  else
    echo "Failed to create HTTPS listener with Cognito auth. This feature might be limited in LocalStack. Falling back to HTTP only for ALB."
  fi
  set -e # Re-enable exit on error
fi

# (Optional) Create a basic ECS Task Definition and Service if needed for more complete simulation
# This would involve defining container (pointing to a locally built image) and service config.
# For now, focus is on ALB + Cognito.

echo "LocalStack init script finished."
echo "Important LocalStack endpoints/values:"
echo "  ALB DNS (if created): http://$ALB_DNS_NAME (or use localhost:4566 with ALB name in path/Host header)"
echo "  Cognito User Pool ID: $USER_POOL_ID"
echo "  Cognito Client ID: $USER_POOL_CLIENT_ID"
echo "  Cognito Domain Prefix: $COGNITO_DOMAIN_PREFIX"
echo "  ECR Repository: $ECR_REPO_NAME (Access via http://localhost:4566)"
echo "  ECS Cluster: $ECS_CLUSTER_NAME"
