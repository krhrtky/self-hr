# Project Overview

This project is an attendance management system.

## Technologies Used

* **Backend:** Kotlin, Gradle
* **Frontend:** React, TypeScript, Vite
* **Containerization:** Docker
* **Cloud Services:** AWS (Cognito)

## Architecture

The project follows a client-server architecture:

*   **Frontend (Client):** A single-page application (SPA) built with React and TypeScript. It interacts with the backend via REST APIs.
*   **Backend (Server):** A Kotlin application (likely using Spring Boot) that exposes REST APIs for the frontend. It handles business logic and data persistence.
*   **Database:** A relational database is used for data storage (schema managed by `sqldef`).
*   **Containerization:** Both frontend and backend applications are containerized using Docker for consistent deployment and scaling.
*   **Authentication:** AWS Cognito is used for user authentication.
*   **API Gateway/Load Balancer:** Traefik is used, likely as an API gateway or load balancer.

```mermaid
graph TD
    A[Frontend (React+TypeScript)] --> B{REST API};
    B --> C[Backend (Kotlin API Layer)];
    C --> D[Application Layer];
    D --> E[Domain Layer];
    D --> F[Infrastructure Layer];
    F --> G[Database (PostgreSQL)];
    F --> H[AWS Cognito];

    subgraph Backend
        C
        D
        E
        F
    end
```
