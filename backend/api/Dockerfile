FROM amazoncorretto:21.0.3

ARG DB_URL
ARG DB_USER
ARG DB_PASSWORD

ENV DB_URL $DB_URL
ENV DB_USER $DB_USER
ENV DB_PASSWORD $DB_PASSWORD

WORKDIR /build
COPY . .
RUN ./gradlew backend:api:bootJar

FROM amazoncorretto:21.0.3

ENV OTEL_TRACES_EXPORTER otlp
ENV OTEL_METRICS_EXPORTER otlp
ENV OTEL_LOGS_EXPORTER console
ENV OTEL_EXPORTER_OTLP_ENDPOINT http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_TRACES_ENDPOINT http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_METRICS_ENDPOINT http://otel-collector:4317
ENV OTEL_EXPORTER_OTLP_PROTOCOL grpc
ENV OTEL_RESOURCE_ATTRIBUTES service.instance.id=petclinic,INSTANA_PLUGIN=jvm

RUN yum update -y \
    && yum install -y shadow-utils \
    && yum clean all

RUN mkdir -p /app/jar

RUN groupadd --system javauser && useradd -r -s /bin/false -g javauser javauser


COPY --from=0 /build/backend/api/build/libs/api.jar /app/jar/api.jar
COPY --from=0 /build/schema.graphql /schema.graphql

WORKDIR /app

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .
RUN chmod -R 755 /app
USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:./opentelemetry-javaagent.jar", "-Djava.security.egd=file:/dev/./urandom", "-Djava/awt.headless=true", "-XX:+UseG1GC", "-Xms2g", "-Xmx2g", "-XX:-TieredCompilation", "-jar", "/app/jar/api.jar"]
