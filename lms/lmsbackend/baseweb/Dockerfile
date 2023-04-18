# syntax = docker/dockerfile:1

# JDK is required to provide compiler
FROM eclipse-temurin:8-jdk-alpine as build
WORKDIR /workspace/app

# Split source code into four layers
# The later layers contain the build configuration and the source code for the application,
# and the earlier layers contain the build system itself (the Maven wrapper)
COPY mvnw .
RUN chmod +x mvnw

COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests

# Unpack Spring Boot fat JAR
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:8-jre-alpine

# Install ortools and Apache POI dependencies
RUN apk update && apk add --no-cache \
    gcompat \
    ncurses-libs \
    libgcc \
    libstdc++ \
    && apk add \
    --update ttf-dejavu \
    && rm -rf /var/cache/apk/*

# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

ENV JAVA_OPTS=""

# /tmp (inside the container) will be a volume
# When running the container, a random numbers and characters named directory will be created in /var/lib/docker/volumes
# VOLUME /tmp

ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT java ${JAVA_OPTS} -cp app:app/lib/* com.hust.baseweb.BasewebApplication
