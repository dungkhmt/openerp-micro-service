# syntax = docker/dockerfile:1

# Giai đoạn 1: Build ứng dụng
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

COPY mvnw .
RUN chmod +x mvnw

COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests

# Unpack Spring Boot fat JAR
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# --- SỬA LẠI 2 DÒNG LỆNH CUỐI CÙNG ---
# 1. Giải nén đúng thư mục từ bên trong file JAR
RUN cd target/dependency/BOOT-INF/lib && \
    jar -xf ortools-linux-x86-64-*.jar ortools-linux-x86-64/


# Giai đoạn 2: Tạo image để chạy ứng dụng
FROM eclipse-temurin:17-jre

ENV TZ=Asia/Ho_Chi_Minh
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apt-get clean

ENV ENV=cloud
ENV JAVA_OPTS="-Djava.library.path=/app/native_libs"

ARG DEPENDENCY=/workspace/app/target/dependency

# 2. Sao chép đúng thư mục đã được giải nén
COPY --from=build /workspace/app/target/dependency/BOOT-INF/lib/ortools-linux-x86-64 /app/native_libs

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["/bin/sh", "-c", "java ${JAVA_OPTS} -cp app:app/lib/* com.hust.openerp.taskmanagement.TaskManagementApplication"]
