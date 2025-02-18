# syntax = docker/dockerfile:1

# JDK is required to provide compiler
FROM eclipse-temurin:8-jdk AS build
WORKDIR /workspace/app

# Split source code into four layers
# The later layers contain the build configuration and the source code for the application,
# and the earlier layers contain the build system itself (the Maven wrapper)
COPY mvnw .
RUN chmod +x mvnw

COPY .mvn .mvn
COPY pom.xml .
COPY src src

COPY --from=leanhtuan/judge0-client:latest /root/.m2/repository/vn /root/.m2/repository/vn
RUN rm -f /root/.m2/repository/vn/edu/hust/soict/judge0-client/0.0.1/_remote.repositories

RUN ./mvnw install -DskipTests

# Unpack Spring Boot fat JAR
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:8-jre

ENV JAVA_OPTS=""

ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT java ${JAVA_OPTS} -cp app:app/lib/* com.hust.baseweb.CodeRunnerServiceApplication
