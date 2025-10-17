# Sử dụng image Java chính thức
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Copy file pom.xml trước để tải dependencies
COPY pom.xml ./

# Nếu có Maven Wrapper, copy luôn
COPY mvnw ./
COPY .mvn ./.mvn

# Cấp quyền thực thi cho mvnw
RUN chmod +x mvnw

# Cài đặt dependencies (skip test để nhanh hơn)
RUN ./mvnw dependency:resolve -DskipTests

# Copy toàn bộ source code
COPY src ./src

# Build project thành jar
RUN ./mvnw package -DskipTests

# Lệnh chạy app khi container start
# Lưu ý: thay 'your-app.jar' bằng tên file jar thực tế trong target
CMD ["java", "-jar", "target/billardRanking-sever-0.0.1-SNAPSHOT.jar"]
