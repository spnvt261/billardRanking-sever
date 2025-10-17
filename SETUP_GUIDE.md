# Hướng dẫn Setup và Chạy BillardRankings

## Tổng quan

BillardRankings là hệ thống quản lý giải đấu bida được xây dựng bằng Spring Boot với các tính năng CRUD đầy đủ cho tất cả entities.

## Yêu cầu hệ thống

### Phần mềm cần thiết
- **Java 17+** - [Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://openjdk.org/)
- **Maven 3.6+** - [Download Maven](https://maven.apache.org/download.cgi)
- **Git** - [Download Git](https://git-scm.com/downloads)

### Database (chọn một trong hai)
1. **H2 Database** (đã cấu hình sẵn - khuyến nghị cho demo)
2. **MySQL 8.0+** - [Download MySQL](https://dev.mysql.com/downloads/mysql/)

## Cài đặt và Setup

### Bước 1: Clone Repository
```bash
git clone <repository-url>
cd BillardRankings
```

### Bước 2: Kiểm tra Java và Maven
```bash
# Kiểm tra Java version
java -version

# Kiểm tra Maven version
mvn -version
```

### Bước 3: Cấu hình Database

#### Option 1: Sử dụng H2 Database (Khuyến nghị cho demo)
H2 database đã được cấu hình sẵn trong `application.properties`:
```properties
# Database Config - H2 for testing
spring.datasource.driver-class-name = org.h2.Driver
spring.datasource.url = jdbc:h2:mem:testdb
spring.datasource.username = sa
spring.datasource.password = 

# H2 Console (for debugging)
spring.h2.console.enabled = true
spring.h2.console.path = /h2-console
```

#### Option 2: Sử dụng MySQL
1. Cài đặt MySQL Server
2. Tạo database:
```sql
CREATE DATABASE test;
```
3. Cập nhật `application.properties`:
```properties
# Database Config
spring.datasource.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/test
spring.datasource.username = root
spring.datasource.password = your_password

# JPA Config
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect
```

### Bước 4: Build và Chạy ứng dụng

#### Compile project
```bash
mvn clean compile
```

#### Chạy ứng dụng
```bash
mvn spring-boot:run
```

#### Hoặc build JAR và chạy
```bash
# Build JAR
mvn clean package

# Chạy JAR
java -jar target/BillardRankings-0.0.1-SNAPSHOT.jar
```

### Bước 5: Kiểm tra ứng dụng
Ứng dụng sẽ chạy trên: `http://localhost:8080`

#### Test Health Check
```bash
curl http://localhost:8080/api/test/health
```

Expected response:
```json
{
  "status": "OK",
  "timestamp": "2025-10-11T22:37:23.011949",
  "message": "BillardRankings API is running"
}
```

## Truy cập các giao diện

### 1. Swagger UI
```
http://localhost:8080/swagger-ui.html
```
- Xem tất cả API endpoints
- Test API trực tiếp trên browser
- Xem schema của Request/Response

### 2. H2 Console (nếu dùng H2)
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (để trống)
- Xem và quản lý dữ liệu trong database

### 3. Actuator Endpoints
```
http://localhost:8080/actuator/mappings
```
- Xem tất cả mapping endpoints

## Cấu trúc Database

### Bảng chính
1. **workspaces** - Quản lý workspace
2. **players** - Thông tin người chơi
3. **teams** - Thông tin đội
4. **tournaments** - Thông tin giải đấu
5. **matches** - Thông tin trận đấu
6. **elo_history** - Lịch sử điểm ELO
7. **match_score_events** - Sự kiện ghi điểm

### Bảng quan hệ (Junction Tables)
1. **team_players** - Quan hệ đội-người chơi
2. **tournament_players** - Quan hệ giải đấu-người chơi
3. **tournament_teams** - Quan hệ giải đấu-đội

## Tạo dữ liệu mẫu

### Sử dụng H2 Console
1. Truy cập `http://localhost:8080/h2-console`
2. Kết nối với database
3. Chạy các SQL sau:

```sql
-- Tạo workspace
INSERT INTO workspaces (id, name, password_hash, share_key, created_at, updated_at) 
VALUES (1, 'Demo Workspace', 'hashed_password', 12345678, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Tạo players
INSERT INTO players (id, workspace_id, name, nickname, joined_date, created_at, updated_at) 
VALUES (1, 1, 'Nguyễn Văn A', 'PlayerA', '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO players (id, workspace_id, name, nickname, joined_date, created_at, updated_at) 
VALUES (2, 1, 'Trần Thị B', 'PlayerB', '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Tạo teams
INSERT INTO teams (id, workspace_id, team_name, created_at, updated_at) 
VALUES (1, 1, 'Đội A', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO teams (id, workspace_id, team_name, created_at, updated_at) 
VALUES (2, 1, 'Đội B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### Sử dụng API
```bash
# Tạo player
curl -X POST "http://localhost:8080/api/players" \
  -H "Content-Type: application/json" \
  -d '{
    "workspaceId": 1,
    "name": "Nguyễn Văn C",
    "nickname": "PlayerC",
    "joinedDate": "2025-01-01"
  }'

# Tạo team
curl -X POST "http://localhost:8080/api/teams" \
  -H "Content-Type: application/json" \
  -d '{
    "workspaceId": 1,
    "teamName": "Đội C"
  }'
```

## Troubleshooting

### Lỗi thường gặp

#### 1. Port 8080 đã được sử dụng
```bash
# Tìm process sử dụng port 8080
netstat -ano | findstr :8080

# Kill process
taskkill /F /PID <process_id>
```

#### 2. Java version không đúng
```bash
# Kiểm tra Java version
java -version

# Nếu không phải Java 17+, cài đặt Java 17+
```

#### 3. Maven build fail
```bash
# Clean và rebuild
mvn clean
mvn compile

# Nếu vẫn lỗi, xóa .m2/repository và download lại
rm -rf ~/.m2/repository
mvn clean compile
```

#### 4. Database connection error
- Kiểm tra MySQL service có chạy không
- Kiểm tra username/password trong application.properties
- Kiểm tra database 'test' có tồn tại không

#### 5. Application không start
```bash
# Chạy với debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Hoặc xem log chi tiết
mvn spring-boot:run -Dlogging.level.org.springframework=DEBUG
```

### Log files
- Console logs: Xem trực tiếp trong terminal
- Application logs: Có thể cấu hình trong `application.properties`

## Development

### Cấu trúc project
```
src/main/java/com/billard/BillardRankings/
├── config/          # Cấu hình JPA, Security, etc.
├── constant/         # Các hằng số
├── controller/       # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities
├── exception/       # Exception handling
├── mapper/          # MapStruct mappers
├── repository/      # JPA Repositories
├── service/         # Business logic services
└── utils/           # Utility classes
```

### Hot reload
```bash
# Sử dụng Spring Boot DevTools
mvn spring-boot:run

# Hoặc sử dụng IDE với auto-reload
```

### Testing
```bash
# Chạy unit tests
mvn test

# Chạy integration tests
mvn verify
```

## Production Deployment

### Build JAR
```bash
mvn clean package -DskipTests
```

### Chạy JAR
```bash
java -jar target/BillardRankings-0.0.1-SNAPSHOT.jar
```

### Environment Variables
```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/test
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password

# Server
export SERVER_PORT=8080

# Chạy với environment variables
java -jar target/BillardRankings-0.0.1-SNAPSHOT.jar
```

## API Testing Tools

### 1. Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- Tích hợp sẵn trong ứng dụng

### 2. Postman
- Import collection từ Swagger
- Test các API endpoints

### 3. curl
```bash
# Test health
curl http://localhost:8080/api/test/health

# Test players API
curl -X GET "http://localhost:8080/api/players?workspaceId=1"
```

### 4. REST Client (VS Code Extension)
```http
### Health Check
GET http://localhost:8080/api/test/health

### Get Players
GET http://localhost:8080/api/players?workspaceId=1

### Create Player
POST http://localhost:8080/api/players
Content-Type: application/json

{
  "workspaceId": 1,
  "name": "Test Player",
  "nickname": "Test",
  "joinedDate": "2025-01-01"
}
```

## Monitoring và Logging

### Actuator Endpoints
```
http://localhost:8080/actuator/mappings
```

### Logging Configuration
Trong `application.properties`:
```properties
# Logging
logging.level.org.hibernate.SQL = DEBUG
logging.level.org.hibernate.type = TRACE
logging.level.web = TRACE
logging.level.org.springframework.web = TRACE
```

## Security Notes

- Hiện tại chưa có authentication/authorization
- Tất cả API đều public
- Cần implement security cho production
- Sử dụng HTTPS cho production

## Performance Tuning

### Database
- Sử dụng connection pooling
- Optimize queries
- Add database indexes

### JVM
```bash
# Tăng heap size
java -Xms512m -Xmx1024m -jar target/BillardRankings-0.0.1-SNAPSHOT.jar
```

### Application
- Enable caching
- Optimize queries
- Use pagination
