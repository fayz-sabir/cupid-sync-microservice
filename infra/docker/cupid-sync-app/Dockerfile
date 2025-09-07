FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY core-domain/pom.xml core-domain/pom.xml
COPY application/pom.xml application/pom.xml
COPY adapter-persistence/pom.xml adapter-persistence-jpa/pom.xml
COPY adapter-integration-cupid/pom.xml adapter-integration-cupid/pom.xml
COPY adapter-cache-redis/pom.xml adapter-cache-redis/pom.xml
COPY adapter-messaging/pom.xml adapter-messaging/pom.xml
COPY web-app/pom.xml web-app/pom.xml
COPY ingestion-app/pom.xml ingestion-app/pom.xml
COPY cupid-sync-app/pom.xml cupid-sync-app/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests -pl cupid-sync-app -am dependency:go-offline

COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -DskipTests -pl cupid-sync-app -am clean package


FROM eclipse-temurin:17-jre

ARG APP_UID=10001
ARG APP_GID=10001

RUN groupadd --system --gid ${APP_GID} app && \
    useradd  --system --no-create-home --uid ${APP_UID} --gid ${APP_GID} app

WORKDIR /app
RUN mkdir -p /app/config /app/logs /data /tmp && \
    chown -R ${APP_UID}:${APP_GID} /app /data /tmp

COPY --from=build --chown=${APP_UID}:${APP_GID} /workspace/cupid-sync-app/target/*.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENV TMPDIR=/tmp

EXPOSE 8089

USER ${APP_UID}:${APP_GID}

ENTRYPOINT ["java","-jar","/app/app.jar"]
