# Hướng dẫn chạy dự án Local

## Cấu trúc dự án:
```
Osahaneat/
├── food_delivery/     # Backend (Spring Boot) - Port 82
├── theme-sidebar/     # Frontend User (HTML/CSS/JS)
└── admin/            # Frontend Admin (HTML/CSS/JS)
```

## Cách 1: Chạy Tất Cả Từ Backend (Khuyên Dùng) ✅

Spring Boot đã được cấu hình để serve cả frontend, bạn chỉ cần chạy backend!

### Bước 1: Tạo Database
```sql
CREATE DATABASE osahaneat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Bước 2: Cập nhật Database Config (nếu cần)
Sửa `food_delivery/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=1234  # Đổi theo password MySQL của bạn
```

### Bước 3: Chạy Backend
```bash
cd food_delivery
mvn spring-boot:run
```

Hoặc trong IDE:
- Mở project `food_delivery`
- Chạy class `FoodDeliveryApplication.java`

### Bước 4: Truy cập Frontend

**User Frontend (theme-sidebar):**
- URL: `http://localhost:82/theme/index.html`
- Hoặc: `http://localhost:82/` (sẽ redirect đến theme-sidebar)

**Admin Panel:**
- URL: `http://localhost:82/admin/login.html`
- Hoặc: `http://localhost:82/admin/` (sẽ redirect đến admin)

### Bước 5: Test API
Backend API: `http://localhost:82`

## Cách 2: Chạy Frontend Riêng Biệt (Nếu muốn)

Nếu bạn muốn chạy frontend riêng, có thể dùng các cách sau:

### Option A: VS Code Live Server
1. Cài extension "Live Server" trong VS Code
2. Click phải vào file `theme-sidebar/index.html` → "Open with Live Server"
3. Hoặc `admin/login.html` → "Open with Live Server"

### Option B: Python HTTP Server
```bash
# Terminal 1: Chạy theme-sidebar
cd theme-sidebar
python -m http.server 3000

# Terminal 2: Chạy admin
cd admin
python -m http.server 3001

# Terminal 3: Chạy backend
cd food_delivery
mvn spring-boot:run
```

Sau đó truy cập:
- User Frontend: `http://localhost:3000`
- Admin Panel: `http://localhost:3001`
- Backend API: `http://localhost:82`

### Option C: Node.js http-server
```bash
# Cài http-server globally
npm install -g http-server

# Chạy theme-sidebar
cd theme-sidebar
http-server -p 3000

# Chạy admin
cd admin
http-server -p 3001
```

## Cấu hình Frontend API

Frontend đã được cấu hình để gọi API tại `http://localhost:82`:
- `theme-sidebar/js/api.js` - API_BASE_URL = 'http://localhost:82'
- `admin/js/api.js` - API_BASE_URL = 'http://localhost:82'

Nếu bạn chạy frontend ở port khác, API calls vẫn sẽ gọi về `localhost:82`.

## Tài khoản Test

### Admin Account:
- URL: `http://localhost:82/admin/login.html`
- Email: `admin@gmail.com`
- Password: `123456`

### User Account:
Có thể tạo tài khoản mới tại:
- URL: `http://localhost:82/theme/signup.html`
- Hoặc đăng nhập tại:
- URL: `http://localhost:82/theme/signin.html`

## Troubleshooting

### Frontend không load được
- Đảm bảo backend đang chạy ở port 82
- Kiểm tra console browser (F12) xem có lỗi gì không
- Kiểm tra CORS nếu chạy frontend ở port khác

### API calls thất bại
- Kiểm tra backend đang chạy: `http://localhost:82`
- Kiểm tra network tab trong browser console
- Đảm bảo MySQL đang chạy và database đã được tạo

### Lỗi CORS
- CORS đã được cấu hình cho phép `localhost:*`
- Nếu vẫn lỗi, kiểm tra `CorsConfiguration.java`

## Lưu ý

1. **Khuyên dùng Cách 1**: Chạy tất cả từ backend - đơn giản nhất!
2. Backend tự động serve frontend từ folders `../theme-sidebar` và `../admin`
3. Không cần cài thêm web server riêng cho frontend
4. Tất cả đều chạy trên port 82

