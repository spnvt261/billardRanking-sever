# Sử dụng image Java chính thức
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Copy file pom.xml để tải dependencies trước
COPY pom.xml ./

# Nếu bạn có wrapper Maven (mvnw), copy luôn
COPY mvnw ./
COPY .mvn ./.mvn

# Cài đặt dependencies (skip test để nhanh hơn)
RUN ./mvnw dependency:resolve

# Copy toàn bộ source code
COPY src ./src

# Build project thành .jar
RUN ./mvnw package -DskipTests

# Lệnh chạy app khi container start
CMD ["java", "-jar", "target/your-app.jar"]
