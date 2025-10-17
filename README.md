# BillardRankings - Hệ thống quản lý giải đấu bida

## 📋 Tổng quan

BillardRankings là hệ thống quản lý giải đấu bida được xây dựng bằng **Spring Boot** với API RESTful đầy đủ. Hệ thống hỗ trợ quản lý workspace, người chơi, đội, giải đấu, trận đấu và lịch sử ELO.

## 🚀 Quick Start

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- MySQL 8.0+ hoặc H2 Database

### Chạy nhanh
```bash
# Clone repository
git clone <repository-url>
cd BillardRankings

# Cấu hình MySQL database
# 1. Tạo database: CREATE DATABASE test;
# 2. Cấu hình trong application.properties (đã sẵn sàng)

# Chạy ứng dụng
mvn spring-boot:run

# Truy cập ứng dụng
# API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/swagger-ui.html
# Health Check: http://localhost:8080/api/test/health
```

## 📚 Tài liệu

- **[API_USAGE_GUIDE.md](./API_USAGE_GUIDE.md)** - Hướng dẫn sử dụng API chi tiết với ví dụ thực tế
- **[API_GUIDE.md](./API_GUIDE.md)** - Tài liệu API đầy đủ
- **[SETUP_GUIDE.md](./SETUP_GUIDE.md)** - Hướng dẫn setup và deployment

## 🏗️ Kiến trúc hệ thống

### Cấu trúc dự án
```
src/main/java/com/billard/BillardRankings/
├── config/          # Cấu hình JPA, Security
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

### Database Schema
1. **workspaces** - Quản lý workspace
2. **players** - Thông tin người chơi
3. **teams** - Thông tin đội
4. **tournaments** - Thông tin giải đấu
5. **matches** - Thông tin trận đấu
6. **elo_history** - Lịch sử điểm ELO
7. **match_score_events** - Sự kiện ghi điểm
8. **team_players** - Quan hệ đội-người chơi
9. **tournament_players** - Quan hệ giải đấu-người chơi
10. **tournament_teams** - Quan hệ giải đấu-đội

## 🔧 Tính năng chính

### ✅ CRUD Operations
- **Players API** - Quản lý người chơi
- **Teams API** - Quản lý đội
- **Tournaments API** - Quản lý giải đấu
- **Matches API** - Quản lý trận đấu
- **Elo History API** - Lịch sử điểm ELO
- **Match Score Events API** - Sự kiện ghi điểm
- **Junction Tables API** - Quản lý quan hệ

### ✅ Advanced Features
- **Workspace-based filtering** - Lọc dữ liệu theo workspace
- **Pagination & Search** - Phân trang và tìm kiếm
- **Data Validation** - Validation dữ liệu đầu vào
- **Exception Handling** - Xử lý lỗi toàn cục
- **MapStruct Integration** - Mapping tự động
- **JPA Auditing** - Tự động ghi timestamp
- **Swagger Documentation** - API documentation
- **CORS Support** - Cross-origin requests

## 🌐 API Endpoints

### Base URL
```
http://localhost:8080/api
```

### Core Endpoints
| Resource | GET | POST | PUT | DELETE |
|----------|-----|------|-----|--------|
| **Players** | `/api/players?workspaceId=1` | `/api/players` | `/api/players/{id}` | `/api/players/{id}?workspaceId=1` |
| **Teams** | `/api/teams?workspaceId=1` | `/api/teams` | `/api/teams/{id}` | `/api/teams/{id}?workspaceId=1` |
| **Tournaments** | `/api/tournaments?workspaceId=1` | `/api/tournaments` | `/api/tournaments/{id}` | `/api/tournaments/{id}?workspaceId=1` |
| **Matches** | `/api/matches?workspaceId=1` | `/api/matches` | `/api/matches/{id}` | `/api/matches/{id}?workspaceId=1` |
| **Elo History** | `/api/elo-histories?workspaceId=1` | `/api/elo-histories` | `/api/elo-histories/{id}` | `/api/elo-histories/{id}?workspaceId=1` |
| **Score Events** | `/api/match-score-events?workspaceId=1` | `/api/match-score-events` | `/api/match-score-events/{id}` | `/api/match-score-events/{id}?workspaceId=1` |

### Junction Tables
| Resource | Endpoint |
|----------|----------|
| **Team Players** | `/api/team-players?workspaceId=1` |
| **Tournament Players** | `/api/tournament-players?workspaceId=1` |
| **Tournament Teams** | `/api/tournament-teams?workspaceId=1` |

## 🔍 Query Parameters

Tất cả GET endpoints hỗ trợ:
- `workspaceId` (bắt buộc) - ID workspace
- `page` (tùy chọn) - Số trang (mặc định: 1)
- `size` (tùy chọn) - Kích thước trang (mặc định: 20)
- `sort` (tùy chọn) - Sắp xếp (mặc định: id)
- `filter` (tùy chọn) - Lọc dữ liệu (RSQL)
- `search` (tùy chọn) - Tìm kiếm
- `all` (tùy chọn) - Lấy tất cả (không phân trang)

## 📝 Ví dụ sử dụng

### Tạo người chơi mới
```bash
curl -X POST "http://localhost:8080/api/players" \
  -H "Content-Type: application/json" \
  -d '{
    "workspaceId": 1,
    "name": "Nguyễn Văn A",
    "nickname": "PlayerA",
    "joinedDate": "2025-01-01"
  }'
```

### Lấy danh sách người chơi
```bash
curl -X GET "http://localhost:8080/api/players?workspaceId=1&page=1&size=10"
```

### Tạo giải đấu
```bash
curl -X POST "http://localhost:8080/api/tournaments" \
  -H "Content-Type: application/json" \
  -d '{
    "workspaceId": 1,
    "name": "Giải đấu mùa đông 2025",
    "tournamentType": "SINGLE_ELIMINATION",
    "startDate": "2025-12-01",
    "location": "Hà Nội",
    "prize": "10,000,000 VND",
    "status": "UPCOMING"
  }'
```

## 🛠️ Cấu hình Database

### H2 Database (Demo)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

### MySQL Database (Production)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## 🔧 Development

### Build & Run
```bash
# Compile
mvn clean compile

# Run
mvn spring-boot:run

# Build JAR
mvn clean package

# Run JAR
java -jar target/BillardRankings-0.0.1-SNAPSHOT.jar
```

### Testing
```bash
# Unit tests
mvn test

# Integration tests
mvn verify
```

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8080/api/test/health
```

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Health Check
```bash
curl http://localhost:8080/api/test/health
```

### Actuator
```
http://localhost:8080/actuator/mappings
```

## 🚨 Response Codes

- `200 OK` - Thành công
- `201 Created` - Tạo mới thành công
- `204 No Content` - Xóa thành công
- `400 Bad Request` - Dữ liệu không hợp lệ
- `404 Not Found` - Không tìm thấy
- `500 Internal Server Error` - Lỗi server

## 🔒 Security Notes

- Hiện tại chưa có authentication/authorization
- Tất cả API đều public
- Cần implement security cho production
- Sử dụng HTTPS cho production

## 📈 Performance

- **Connection Pooling** - Database connection optimization
- **Pagination** - Tối ưu cho large datasets
- **Caching** - Có thể enable caching
- **Query Optimization** - JPA query optimization

## 🤝 Contributing

1. Fork repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

- **API Usage Guide**: [API_USAGE_GUIDE.md](./API_USAGE_GUIDE.md) - **Bắt đầu từ đây!**
- **Full API Documentation**: [API_GUIDE.md](./API_GUIDE.md)
- **Setup Guide**: [SETUP_GUIDE.md](./SETUP_GUIDE.md)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/api/test/health
- **Issues**: Create GitHub issue

---

**BillardRankings** - Quản lý giải đấu bida chuyên nghiệp 🎱
