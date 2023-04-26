# syntax = docker/dockerfile:1

FROM eclipse-temurin:20-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw && ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:20-jdk-alpine

# For ortools and Apache POI
RUN apk update && apk add --no-cache \
    gcompat \
    ncurses-libs \
    libgcc \
    libstdc++ \
    && apk add \
    --update ttf-dejavu \
    && rm -rf /var/cache/apk/*

ENV JAVA_OPTS=""

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT java ${JAVA_OPTS} -cp app:app/lib/* openerp.openerpresourceserver.OpenerpResourceServerApplication
