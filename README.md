# Self-HR

Self-HR is a comprehensive HR management system built with modern technologies and domain-driven design principles. The system provides robust features for managing human resources, including attendance tracking, contract management, project allocation, and invoice processing.

## Features

- **Attendance Management**: Track and manage employee attendance and time records
- **Contract Management**: Handle employee contracts with version control and status tracking
- **Project Management**: Manage project assignments and resource allocation
- **Invoice Processing**: Generate and manage invoices based on contracts and work records
- **User Management**: Comprehensive user administration with role-based access control
- **Reporting**: Generate detailed reports for attendance, projects, and invoices

## Architecture

The system follows a Domain-Driven Design (DDD) architecture with:
- Clear separation of domains (attendance, contract, project, etc.)
- Hexagonal architecture pattern
- CQRS pattern for data operations
- Event-driven communication between domains
- Clean and maintainable codebase structure

## System Requirements

- Docker and Docker Compose
- Java 17 or higher
- Node.js and pnpm
- PostgreSQL 16.4
- Make

## Project Structure

```
self-hr/
├── backend/                 # Backend services
│   ├── api/                # REST/GraphQL API endpoints
│   ├── applications/       # Application services
│   ├── core/              # Core business logic
│   ├── domains/           # Domain modules
│   │   ├── attendance/    # Attendance management
│   │   ├── contract/      # Contract management
│   │   ├── invoice/       # Invoice management
│   │   ├── project/       # Project management
│   │   └── proprietor/    # Proprietor management
│   ├── infrastructure/    # Infrastructure layer (DB, external services)
│   └── shared/           # Shared utilities and components
├── front/                 # Frontend application
│   └── app/              # React application
└── docker/               # Docker configuration files
```

## Technology Stack

### Backend
- Kotlin
- Spring Boot
- GraphQL
- PostgreSQL
- jOOQ for database access
- OpenTelemetry for observability
- Fluent Bit for logging

### Frontend
- React
- Vite
- Storybook
- OpenAPI generated clients

### Infrastructure
- Docker & Docker Compose
- Traefik (Reverse Proxy)
- PostgreSQL
- AWS Mock (for local development)

## Setup Instructions

1. Clone the repository
2. Copy `.env.example` to `.env` and configure environment variables (see Environment Variables section below)
3. Run the setup command:
   ```bash
   make setup
   ```

This will:
- Set up the database
- Generate necessary code
- Install frontend dependencies
- Set up the development environment

## Development

### Starting the Services

1. Start the backend:
   ```bash
   make start-backend
   ```

2. Start the frontend:
   ```bash
   make start-frontend
   ```

3. Run Storybook (for UI development):
   ```bash
   make run-storybook
   ```

### Database Operations

- Migrate database (local):
  ```bash
  make db-migrate-local
  ```

- Generate database code:
  ```bash
  make db-codegen
  ```

### API Development

- Generate GraphQL code:
  ```bash
  make graphql-codegen
  ```

- Generate OpenAPI clients:
  ```bash
  make open-api-client-gen
  ```

## Testing

- Frontend tests:
  ```bash
  make test-frontend
  ```

## Building

- Build backend:
  ```bash
  make build-backend
  ```

- Build frontend:
  ```bash
  make build-frontend
  ```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## Environment Variables

The following environment variables need to be configured in your `.env` file:

### Database Configuration
- `DB_HOST`: Database host address
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `DB_NAME`: Database name

### AWS Configuration (for local development)
- `AWS_ACCESS_KEY_ID`: AWS access key
- `AWS_SECRET_ACCESS_KEY`: AWS secret key
- `AWS_REGION`: AWS region

### Application Configuration
- `APP_PORT`: Application port (default: 8080)
- `APP_ENV`: Application environment (development/staging/production)

### Logging Configuration
- `LOG_LEVEL`: Logging level (INFO/DEBUG/WARN/ERROR)

### Observability Configuration
- `OTEL_EXPORTER_OTLP_ENDPOINT`: OpenTelemetry collector endpoint
- `OTEL_SERVICE_NAME`: Service name for telemetry
- `FLUENT_HOST`: Fluent Bit host for logging
- `FLUENT_PORT`: Fluent Bit port for logging

## License

[Add your license information here]
