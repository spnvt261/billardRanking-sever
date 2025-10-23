# --- Stage 1: Build ---
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copy file cấu hình Maven trước (để cache dependencies)
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Cấp quyền thực thi cho Maven Wrapper
RUN chmod +x mvnw

# Cài dependencies (skip test)
RUN ./mvnw dependency:go-offline -DskipTests

# Copy toàn bộ source code
COPY src ./src

# Build project
RUN ./mvnw clean package -DskipTests

# --- Stage 2: Runtime ---
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port cho Render (Render sẽ set PORT)
EXPOSE 8080

# Lệnh khởi chạy
ENTRYPOINT ["java", "-jar", "app.jar"]
