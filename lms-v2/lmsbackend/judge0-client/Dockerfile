# syntax = docker/dockerfile:1

# JDK is required to provide compiler
FROM eclipse-temurin:8-jdk
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