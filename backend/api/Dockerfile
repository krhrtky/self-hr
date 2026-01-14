FROM amazoncorretto:21.0.3

ARG DB_URL
ARG DB_USER
ARG DB_PASSWORD

ENV DB_URL $DB_URL
ENV DB_USER $DB_USER
ENV DB_PASSWORD $DB_PASSWORD

WORKDIR /build

# Copy Gradle wrapper, settings, and root build file
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY settings.gradle.kts ./
COPY build.gradle.kts ./
COPY gradle.properties ./

# Copy backend project build files to define project structure
COPY backend/api/build.gradle.kts backend/api/build.gradle.kts
COPY backend/applications/build.gradle.kts backend/applications/build.gradle.kts
COPY backend/core/build.gradle.kts backend/core/build.gradle.kts
COPY backend/domains/build.gradle.kts backend/domains/build.gradle.kts
COPY backend/infrastructure/build.gradle.kts backend/infrastructure/build.gradle.kts
COPY backend/testCore/build.gradle.kts backend/testCore/build.gradle.kts

# Copy the schema for code generation
COPY schema.graphql ./schema.graphql

# It's often better to download dependencies as a separate layer
# RUN ./gradlew --no-daemon :backend:api:dependencies

# Copy source code
COPY backend/api/src backend/api/src
COPY backend/applications/src backend/applications/src
COPY backend/core/src backend/core/src
COPY backend/domains/src backend/domains/src
COPY backend/infrastructure/src backend/infrastructure/src
# testCore source is not needed for the final api.jar, only its build.gradle.kts for project structure

RUN ./gradlew backend:api:bootJar

FROM amazoncorretto:21.0.3

ENV OTEL_TRACES_EXPORTER otlp
ENV OTEL_METRICS_EXPORTER otlp
ENV OTEL_LOGS_EXPORTER console
ENV OTEL_EXPORTER_OTLP_ENDPOINT http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_TRACES_ENDPOINT http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_METRICS_ENDPOINT http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_PROTOCOL grpc
# Note: service.instance.id=petclinic seems to be a hardcoded value from a tutorial.
# Consider making this dynamic via an environment variable if this Dockerfile is to be reused for other services.
ENV OTEL_RESOURCE_ATTRIBUTES service.instance.id=petclinic,INSTANA_PLUGIN=jvm

# shadow-utils is not needed for runtime, groupadd/useradd are part of coreutils (already in the base image)
# RUN yum update -y \
#     && yum install -y shadow-utils \
#     && yum clean all

RUN mkdir -p /app/jar

RUN groupadd --system javauser && useradd --system --no-create-home --gid javauser --shell /bin/false javauser


COPY --from=0 /build/backend/api/build/libs/api.jar /app/jar/api.jar
# schema.graphql is copied from the build stage. Ensure it's actually needed by the runtime application.
# If only used during build (e.g. by DGS codegen), it might not be needed in the final image.
# For now, keeping it as per original Dockerfile's intent.
COPY --from=0 /build/schema.graphql /schema.graphql

WORKDIR /app

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .
RUN chmod -R 755 /app
USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:./opentelemetry-javaagent.jar", "-Djava.security.egd=file:/dev/./urandom", "-Djava/awt.headless=true", "-XX:+UseG1GC", "-Xms2g", "-Xmx2g", "-XX:-TieredCompilation", "-jar", "/app/jar/api.jar"]
