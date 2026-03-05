# Language Center Management System - Complete REST API Server

A full-featured Spring Boot 3.2.0 REST API server for managing all aspects of a language center operations, including course management, student enrollment, teacher management, class scheduling, attendance tracking, payment processing, and more.

**Status**: ✅ Fully implemented with 17 REST API endpoints covering 17 entities

## Features Implemented

✅ **17 Complete REST API Endpoints** - All CRUD operations for every entity
✅ **17 JPA Entities** - Complete database object mapping
✅ **17 Service Classes** - Business logic layer
✅ **17 Repository Interfaces** - Data access layer
✅ **Cross-Origin Support** - CORS enabled for all endpoints
✅ **Proper HTTP Status Codes** - 200, 201, 204, 404
✅ **JSON Request/Response** - All data in JSON format
✅ **Error Handling** - Comprehensive error responses

## Technology Stack

- **Java 21** - Programming language (or 17+)
- **Spring Boot 3.2.0** - Framework
- **Spring Data JPA** - ORM and database access
- **MySQL 5.7+** - Database
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

## Project Structure

```
src/main/java/trungtamngoaingu/hcmute/
├── App.java                    # Spring Boot Application Entry Point
├── controller/                 # REST API Endpoints (17 controllers)
│   ├── BranchController.java
│   ├── RoomController.java
│   ├── CourseController.java
│   ├── TeacherController.java
│   ├── StudentController.java
│   ├── StaffController.java
│   ├── PromotionController.java
│   ├── ClassController.java
│   ├── PlacementTestController.java
│   ├── EnrollmentController.java
│   ├── ScheduleController.java
│   ├── InvoiceController.java
│   ├── PaymentController.java
│   ├── AttendanceController.java
│   ├── ResultController.java
│   ├── CertificateController.java
│   └── UserAccountController.java
├── entity/                     # JPA Entities (17 entities)
│   ├── Branch.java
│   ├── Room.java
│   ├── Course.java
│   ├── Teacher.java
│   ├── Student.java
│   ├── Staff.java
│   ├── Promotion.java
│   ├── Class.java
│   ├── PlacementTest.java
│   ├── Enrollment.java
│   ├── Schedule.java
│   ├── Invoice.java
│   ├── Payment.java
│   ├── Attendance.java
│   ├── Result.java
│   ├── Certificate.java
│   └── UserAccount.java
├── repository/                 # JPA Repositories (17 interfaces)
│   ├── BranchRepository.java
│   ├── RoomRepository.java
│   └── ... (15 more repositories)
├── service/                    # Service Layer (17 services)
│   ├── BranchService.java
│   ├── RoomService.java
│   └── ... (15 more services)
└── resources/
    └── application.properties  # Configuration
```

## Cài đặt và Chạy

### 1. Chuẩn bị Database
Chạy SQL script để tạo database và các bảng:
```bash
mysql -u root -p < init_database.sql
```

### 2. Cấu hình Database
Mở file `src/main/resources/application.properties` và cập nhật thông tin:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/LanguageCenterDB?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build và Run
```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Server sẽ chạy tại: `http://localhost:8080`

## REST API Endpoints

**Base URL**: `http://localhost:8080/api`

### 1. Branches Management (`/api/branches`)
- `GET /api/branches` - Lấy tất cả chi nhánh
- `GET /api/branches/{id}` - Lấy chi nhánh theo ID
- `POST /api/branches` - Tạo chi nhánh mới
- `PUT /api/branches/{id}` - Cập nhật chi nhánh
- `DELETE /api/branches/{id}` - Xóa chi nhánh

### 2. Rooms Management (`/api/rooms`)
- `GET /api/rooms` - Lấy tất cả phòng học
- `GET /api/rooms/{id}` - Lấy phòng theo ID
- `POST /api/rooms` - Tạo phòng mới
- `PUT /api/rooms/{id}` - Cập nhật phòng
- `DELETE /api/rooms/{id}` - Xóa phòng

### 3. Courses Management (`/api/courses`)
- `GET /api/courses` - Lấy tất cả khóa học
- `GET /api/courses/{id}` - Lấy khóa học theo ID
- `POST /api/courses` - Tạo khóa học mới
- `PUT /api/courses/{id}` - Cập nhật khóa học
- `DELETE /api/courses/{id}` - Xóa khóa học

### 4. Teachers Management (`/api/teachers`)
- `GET /api/teachers` - Lấy tất cả giáo viên
- `GET /api/teachers/{id}` - Lấy giáo viên theo ID
- `POST /api/teachers` - Tạo giáo viên mới
- `PUT /api/teachers/{id}` - Cập nhật giáo viên
- `DELETE /api/teachers/{id}` - Xóa giáo viên

### 5. Students Management (`/api/students`)
- `GET /api/students` - Lấy tất cả học viên
- `GET /api/students/{id}` - Lấy học viên theo ID
- `POST /api/students` - Tạo học viên mới
- `PUT /api/students/{id}` - Cập nhật học viên
- `DELETE /api/students/{id}` - Xóa học viên

### 6. Staff Management (`/api/staff`)
- `GET /api/staff` - Lấy tất cả nhân viên
- `GET /api/staff/{id}` - Lấy nhân viên theo ID
- `POST /api/staff` - Tạo nhân viên mới
- `PUT /api/staff/{id}` - Cập nhật nhân viên
- `DELETE /api/staff/{id}` - Xóa nhân viên

### 7. Promotions Management (`/api/promotions`)
- `GET /api/promotions` - Lấy tất cả khuyến mãi
- `GET /api/promotions/{id}` - Lấy khuyến mãi theo ID
- `POST /api/promotions` - Tạo khuyến mãi mới
- `PUT /api/promotions/{id}` - Cập nhật khuyến mãi
- `DELETE /api/promotions/{id}` - Xóa khuyến mãi

### 8. Classes Management (`/api/classes`)
- `GET /api/classes` - Lấy tất cả lớp học
- `GET /api/classes/{id}` - Lấy lớp học theo ID
- `POST /api/classes` - Tạo lớp học mới
- `PUT /api/classes/{id}` - Cập nhật lớp học
- `DELETE /api/classes/{id}` - Xóa lớp học

### 9. Placement Tests Management (`/api/placement-tests`)
- `GET /api/placement-tests` - Lấy tất cả bài kiểm tra đầu vào
- `GET /api/placement-tests/{id}` - Lấy bài kiểm tra theo ID
- `POST /api/placement-tests` - Tạo bài kiểm tra mới
- `PUT /api/placement-tests/{id}` - Cập nhật bài kiểm tra
- `DELETE /api/placement-tests/{id}` - Xóa bài kiểm tra

### 10. Enrollments Management (`/api/enrollments`)
- `GET /api/enrollments` - Lấy tất cả đăng ký học
- `GET /api/enrollments/{id}` - Lấy đăng ký theo ID
- `POST /api/enrollments` - Tạo đăng ký mới
- `PUT /api/enrollments/{id}` - Cập nhật đăng ký
- `DELETE /api/enrollments/{id}` - Xóa đăng ký

### 11. Schedules Management (`/api/schedules`)
- `GET /api/schedules` - Lấy tất cả lịch học
- `GET /api/schedules/{id}` - Lấy lịch học theo ID
- `POST /api/schedules` - Tạo lịch học mới
- `PUT /api/schedules/{id}` - Cập nhật lịch học
- `DELETE /api/schedules/{id}` - Xóa lịch học

### 12. Invoices Management (`/api/invoices`)
- `GET /api/invoices` - Lấy tất cả hóa đơn
- `GET /api/invoices/{id}` - Lấy hóa đơn theo ID
- `POST /api/invoices` - Tạo hóa đơn mới
- `PUT /api/invoices/{id}` - Cập nhật hóa đơn
- `DELETE /api/invoices/{id}` - Xóa hóa đơn

### 13. Payments Management (`/api/payments`)
- `GET /api/payments` - Lấy tất cả thanh toán
- `GET /api/payments/{id}` - Lấy thanh toán theo ID
- `POST /api/payments` - Tạo thanh toán mới
- `PUT /api/payments/{id}` - Cập nhật thanh toán
- `DELETE /api/payments/{id}` - Xóa thanh toán

### 14. Attendance Management (`/api/attendances`)
- `GET /api/attendances` - Lấy tất cả điểm danh
- `GET /api/attendances/{id}` - Lấy điểm danh theo ID
- `POST /api/attendances` - Tạo điểm danh mới
- `PUT /api/attendances/{id}` - Cập nhật điểm danh
- `DELETE /api/attendances/{id}` - Xóa điểm danh

### 15. Results Management (`/api/results`)
- `GET /api/results` - Lấy tất cả kết quả học tập
- `GET /api/results/{id}` - Lấy kết quả theo ID
- `POST /api/results` - Tạo kết quả mới
- `PUT /api/results/{id}` - Cập nhật kết quả
- `DELETE /api/results/{id}` - Xóa kết quả

### 16. Certificates Management (`/api/certificates`)
- `GET /api/certificates` - Lấy tất cả chứng chỉ
- `GET /api/certificates/{id}` - Lấy chứng chỉ theo ID
- `POST /api/certificates` - Tạo chứng chỉ mới
- `PUT /api/certificates/{id}` - Cập nhật chứng chỉ
- `DELETE /api/certificates/{id}` - Xóa chứng chỉ

### 17. User Accounts Management (`/api/user-accounts`)
- `GET /api/user-accounts` - Lấy tất cả tài khoản người dùng
- `GET /api/user-accounts/{id}` - Lấy tài khoản theo ID
- `POST /api/user-accounts` - Tạo tài khoản mới
- `PUT /api/user-accounts/{id}` - Cập nhật tài khoản
- `DELETE /api/user-accounts/{id}` - Xóa tài khoản

## Ví dụ Request/Response

### Tạo học viên mới
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "2003-05-12",
    "gender": "Male",
    "phone": "0931111222",
    "email": "a.nguyen@gmail.com",
    "address": "Quận 1, TP.HCM",
    "registrationDate": "2026-03-04",
    "status": "Active"
  }'
```

**Response (201 Created)**:
```json
{
  "studentId": 3,
  "fullName": "Nguyễn Văn A",
  "dateOfBirth": "2003-05-12",
  "gender": "Male",
  "phone": "0931111222",
  "email": "a.nguyen@gmail.com",
  "address": "Quận 1, TP.HCM",
  "registrationDate": "2026-03-04",
  "status": "Active"
}
```

### Lấy danh sách khóa học
```bash
curl -X GET http://localhost:8080/api/courses
```

**Response (200 OK)**:
```json
[
  {
    "courseId": 1,
    "courseName": "IELTS Mastery 6.5+",
    "description": "Khóa học luyện thi IELTS mục tiêu 6.5",
    "level": "Advanced",
    "duration": 120,
    "fee": 8500000.00,
    "status": "Active"
  },
  {
    "courseId": 2,
    "courseName": "Giao tiếp Tiếng Anh Cơ bản",
    "description": "Lấy lại căn bản giao tiếp",
    "level": "Beginner",
    "duration": 60,
    "fee": 3000000.00,
    "status": "Active"
  }
]
```

## Documentation

Xem file [API_DOCUMENTATION.md](API_DOCUMENTATION.md) để có tài liệu API chi tiết.

## Contact & Support

Nếu có câu hỏi hay vấn đề, vui lòng liên hệ với nhóm phát triển.

**Version**: 1.0.0  
**Last Updated**: March 4, 2026
- `DELETE /api/enrollments/{id}` - Xóa đăng ký
- `GET /api/enrollments/student/{studentId}` - Lấy đăng ký theo học viên
- `GET /api/enrollments/class/{classId}` - Lấy đăng ký theo lớp học

### Payments API
- `GET /api/payments` - Lấy danh sách tất cả thanh toán
- `GET /api/payments/{id}` - Lấy thông tin thanh toán theo ID
- `POST /api/payments` - Tạo thanh toán mới
- `PUT /api/payments/{id}` - Cập nhật thanh toán
- `DELETE /api/payments/{id}` - Xóa thanh toán
- `GET /api/payments/student/{studentId}` - Lấy thanh toán theo học viên
- `GET /api/payments/status/{status}` - Lấy thanh toán theo trạng thái

### Attendances API
- `GET /api/attendances` - Lấy danh sách tất cả điểm danh
- `GET /api/attendances/{id}` - Lấy thông tin điểm danh theo ID
- `POST /api/attendances` - Tạo điểm danh mới
- `PUT /api/attendances/{id}` - Cập nhật điểm danh
- `DELETE /api/attendances/{id}` - Xóa điểm danh
- `GET /api/attendances/student/{studentId}` - Lấy điểm danh theo học viên
- `GET /api/attendances/class/{classId}` - Lấy điểm danh theo lớp học

## Ví dụ Request/Response

### Tạo Student mới
**Request:**
```json
POST /api/students
Content-Type: application/json

{
  "fullName": "Nguyễn Văn A",
  "dateOfBirth": "2000-01-01",
  "gender": "Male",
  "phone": "0987654321",
  "email": "nguyenvana@example.com",
  "address": "123 Đường ABC, TP.HCM",
  "status": "Active"
}
```

**Response:**
```json
{
  "studentId": 1,
  "fullName": "Nguyễn Văn A",
  "dateOfBirth": "2000-01-01",
  "gender": "Male",
  "phone": "0987654321",
  "email": "nguyenvana@example.com",
  "address": "123 Đường ABC, TP.HCM",
  "registrationDate": "2026-03-04",
  "status": "Active",
  "createdAt": "2026-03-04T10:30:00",
  "updatedAt": "2026-03-04T10:30:00"
}
```

### Lấy danh sách Students
**Request:**
```
GET /api/students
```

**Response:**
```json
[
  {
    "studentId": 1,
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "2000-01-01",
    "gender": "Male",
    "phone": "0987654321",
    "email": "nguyenvana@example.com",
    "address": "123 Đường ABC, TP.HCM",
    "registrationDate": "2026-03-04",
    "status": "Active",
    "createdAt": "2026-03-04T10:30:00",
    "updatedAt": "2026-03-04T10:30:00"
  }
]
```

## Test API với Postman hoặc cURL

### Ví dụ với cURL:
```bash
# Lấy danh sách students
curl http://localhost:8080/api/students

# Tạo student mới
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Nguyễn Văn A","email":"test@example.com","phone":"0987654321","status":"Active"}'

# Cập nhật student
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Nguyễn Văn B","email":"test@example.com","phone":"0987654321","status":"Active"}'

# Xóa student
curl -X DELETE http://localhost:8080/api/students/1
```

## Database Schema
Hệ thống bao gồm 18 bảng chính:
1. **students** - Quản lý học viên
2. **teachers** - Quản lý giáo viên
3. **courses** - Quản lý khóa học
4. **classes** - Quản lý lớp học
5. **enrollments** - Quản lý đăng ký học
6. **payments** - Quản lý thanh toán
7. **invoices** - Quản lý hóa đơn
8. **attendances** - Quản lý điểm danh
9. **schedules** - Quản lý lịch học
10. **rooms** - Quản lý phòng học
11. **branches** - Quản lý chi nhánh
12. **staffs** - Quản lý nhân viên
13. **user_accounts** - Quản lý tài khoản người dùng
14. **results** - Quản lý kết quả học tập
15. **promotions** - Quản lý khuyến mãi
16. **placement_tests** - Quản lý kiểm tra đầu vào
17. **certificates** - Quản lý chứng chỉ
18. **notifications** - Quản lý thông báo

## Lưu ý
- Đảm bảo MySQL đang chạy trước khi start application
- Cấu hình đúng username/password database trong application.properties
- Dữ liệu enum phải match với giá trị trong database (VD: 'Active', 'Inactive')
- API sử dụng HTTP Status codes chuẩn (200 OK, 201 Created, 204 No Content, 404 Not Found, etc.)

## Tác giả
Nhóm 2 - HCMUTE
