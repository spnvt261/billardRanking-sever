# HƯỚNG DẪN SỬ DỤNG API (Tiếng Việt)

Tài liệu này tóm tắt cách dùng API của dự án BillardRankings (nguồn: mã Java trong `src/main/java`).

Môi trường
- Base URL: http://<HOST>:<PORT>/ (ví dụ: http://localhost:8080)
- Tất cả endpoint đặt dưới `/api/...`.

1. Xác thực (Authentication)
- Endpoint:
  - POST /api/auth/login
    - Body (JSON):
      {
        "workspaceKey": "<shareKey của workspace>",
        "password": "<mật khẩu>"
      }
    - Trả về (200): `LoginResponse` chứa:
      - `accessToken` (JWT Bearer dùng cho các request POST/PUT/DELETE)
      - `refreshToken` (chuỗi để lấy token mới)
      - `tokenType` (ví dụ: "Bearer")
      - `workspace` (object `WorkspaceDTO`: id, name, shareKey)

  - POST /api/auth/refresh
    - Body (JSON): { "refreshToken": "<refresh token>" }
    - Trả về (200): tương tự `LoginResponse` với accessToken mới.

2. Cách gửi token
- Với các request POST/PUT/DELETE (non-GET), cần header:
  Authorization: Bearer <accessToken>

3. Quyền truy cập theo workspace (Important)
- Mỗi resource yêu cầu `workspaceId` (thường là query parameter) để xác định workspace đang thao tác.
- Authentication token (JWT) chứa workspaceKey (subject). Ứng dụng đã thêm kiểm tra authorization:
  - Khi token hợp lệ, server sẽ resolve `workspaceKey` → `workspaceId` và lưu `authWorkspaceId` trong Authentication.details.
  - Với mọi request non-GET, `WorkspaceAuthorizationFilter` sẽ kiểm tra `requested workspaceId` (từ query param `workspaceId` hoặc body JSON `workspaceId`) phải khớp `authWorkspaceId`. Nếu khác nhau, server trả:
    {
      "success": false,
      "message": "Forbidden: token does not belong to requested workspace"
    }

4. Các resource chính & ví dụ
Lưu ý: các GET đều không cần token; POST/PUT/DELETE cần token và workspaceId phải khớp với token.

- Players
  - GET /api/players?page=&size=&sort=&filter=&search=&all=&workspaceId=
    - Response: `ListResponse<PlayerResponse>`
  - GET /api/players/{id}?workspaceId=
    - Response: `PlayerResponse`
  - POST /api/players
    - Body: `PlayerRequest` (ví dụ):
      {
        "workspaceId": 1,
        "name": "Nguyen Van A",
        "nickname": "A",
        "avatarUrl": "http://...",
        "description": "...",
        "joinedDate": "2025-01-01"
      }
    - Yêu cầu header Authorization

- Teams
  - Tương tự players: `/api/teams` (GET, GET/{id}, POST, PUT/{id}, DELETE)

- Matches
  - `/api/matches` (GET, GET/{id}, POST, PUT/{id}, DELETE)
  - `MatchRequest` chứa `workspaceId`, `team1Id`, `team2Id`, `matchDate`, v.v.

- Match details
  - GET /api/match-details?page=&size=&...&workspaceId=
  - GET /api/match-details/{matchId}?workspaceId=

- Match score events
  - `/api/match-score-events` (CRUD pattern)

- Elo histories
  - `/api/elo-histories` (CRUD pattern)

- Tournaments
  - `/api/tournaments` (CRUD pattern)

5. Trả về lỗi phổ biến
- 401 Unauthorized
  - Khi token thiếu hoặc không hợp lệ cho request non-GET.
  - Server trả JSON: {"success":false,"message":"JWT token is missing or invalid"}

- 403 Forbidden
  - Khi token hợp lệ nhưng token không thuộc workspace được yêu cầu (authorization theo workspace):
    {"success":false,"message":"Forbidden: token does not belong to requested workspace"}

- 500 Internal Server Error
  - Nếu có lỗi server (ví dụ đọc body nhiều lần) — đã cố gắng khắc phục bằng `CachedBodyHttpServletRequest` để tránh lỗi `getReader() has already been called`.

6. DTO chính (tóm tắt)
- LoginRequest
  - workspaceKey: string (bắt buộc)
  - password: string (bắt buộc)

- LoginResponse
  - accessToken, refreshToken, tokenType, workspace (WorkspaceDTO)

- WorkspaceDTO
  - id (Long), name (String), shareKey (String)

- PlayerRequest
  - workspaceId (Long), name, nickname, avatarUrl, description, joinedDate

- ListResponse<T>
  - content: list, page, size, totalElements, totalPages, last

7. Rate limiting
- Hiện tại không có rate-limiting được triển khai trong code. `API_GUIDE.md` có phần mô tả khuyến nghị về rate-limiting nhưng không được áp dụng tự động.
- Nếu cần, nên triển khai ở gateway (Nginx/Traefik) hoặc thêm Bucket4j trên server (filter) để áp giới hạn theo workspace/IP.

8. Gợi ý bảo mật & vận hành
- Không cho client truyền `workspaceId` (nếu có thể) — lấy workspace id trực tiếp từ token để tránh giả mạo. Nếu không, luôn kiểm tra `authWorkspaceId` vs `requestedWorkspaceId` (điều đã implement).
- Thêm caching ngắn hạn cho mapping `workspaceKey -> workspaceId` để tránh truy vấn DB mỗi request.
- Xem xét giới hạn rate-limits tại gateway hoặc thêm Bucket4j.

9. Thử nghiệm nhanh (ví dụ curl)
- Login để lấy token:
```
POST /api/auth/login
Content-Type: application/json

{ "workspaceKey":"your_share_key", "password":"your_password" }
```

- Tạo player (POST) — cần token và workspaceId trùng token:
```
POST /api/players?workspaceId=1
Authorization: Bearer <accessToken>
Content-Type: application/json

{ "workspaceId":1, "name":"Nguyen Van A", "joinedDate":"2025-01-01" }
```

Kết luận
- Tài liệu này tóm tắt các endpoint chính, cách xác thực và lưu ý quan trọng về phân quyền theo workspace. Nếu bạn muốn, tôi có thể:
  - sinh file OpenAPI/Swagger tự động từ code,
  - hoặc cập nhật API để loại bỏ `workspaceId` do client truyền (lấy hoàn toàn từ token),
  - hoặc thêm rate-limiter (Bucket4j) cùng tests.
