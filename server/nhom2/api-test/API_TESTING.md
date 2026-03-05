# API Testing Guide

A comprehensive guide for testing all REST API endpoints of the Language Center Management System.

## Base URL
```
http://localhost:8080/api
```

## 1. Students API (`/api/students`)

### 1.1 GET All Students
```
GET /api/students
```

### 1.2 GET Student by ID
```
GET /api/students/1
```

### 1.3 POST Create Student
```
POST /api/students
Content-Type: application/json

{
  "fullName": "Phạm Văn D",
  "dateOfBirth": "2002-03-15",
  "gender": "Male",
  "phone": "0934567890",
  "email": "phamvand@example.com",
  "address": "101 Điện Biên Phủ, Bình Thạnh, TP.HCM",
  "registrationDate": "2026-03-04",
  "status": "Active"
}
```

**Response (201 Created)**:
```json
{
  "studentId": 3,
  "fullName": "Phạm Văn D",
  "dateOfBirth": "2002-03-15",
  "gender": "Male",
  "phone": "0934567890",
  "email": "phamvand@example.com",
  "address": "101 Điện Biên Phủ, Bình Thạnh, TP.HCM",
  "registrationDate": "2026-03-04",
  "status": "Active"
}
```

### 1.4 PUT Update Student
```
PUT /api/students/1
Content-Type: application/json

{
  "fullName": "Nguyễn Văn A (Updated)",
  "dateOfBirth": "2000-01-15",
  "gender": "Male",
  "phone": "0901234567",
  "email": "nguyenvana@example.com",
  "address": "123 Lê Lợi, Q1, TP.HCM (New Address)",
  "registrationDate": "2026-02-01",
  "status": "Active"
}
```

**Response (200 OK)**:
```json
{
  "studentId": 1,
  "fullName": "Nguyễn Văn A (Updated)",
  "dateOfBirth": "2000-01-15",
  "gender": "Male",
  "phone": "0901234567",
  "email": "nguyenvana@example.com",
  "address": "123 Lê Lợi, Q1, TP.HCM (New Address)",
  "registrationDate": "2026-02-01",
  "status": "Active"
}
```

### 1.5 DELETE Student
```
DELETE /api/students/1
```

**Response (204 No Content)** - Empty response body

### 1.6 Search Students by Name
```
GET /api/students/search?name=Nguyen
```

**Response (200 OK)**:
```json
[
  {
    "studentId": 1,
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "2003-05-12",
    "gender": "Male",
    "phone": "0931111222",
    "email": "long.le@gmail.com",
    "address": "Quận 7, TP.HCM",
    "registrationDate": "2026-02-01",
    "status": "Active"
  }
]
```

## 2. Teachers API (`/api/teachers`)

### 2.1 GET All Teachers
```
GET /api/teachers
```

### 2.2 GET Teacher by ID
```
GET /api/teachers/1
```

### 2.3 POST Create Teacher
```
POST /api/teachers
Content-Type: application/json

{
  "fullName": "Ms. Jessica Lee",
  "phone": "0945678901",
  "email": "jessica.lee@example.com",
  "specialty": "IELTS Writing",
  "hireDate": "2023-06-01",
  "status": "Active"
}
```

### 2.4 PUT Update Teacher
```
PUT /api/teachers/1
Content-Type: application/json

{
  "fullName": "Nguyễn Văn An (Updated)",
  "phone": "0901111111",
  "email": "an.nguyen@center.com",
  "specialty": "IELTS Speaking",
  "hireDate": "2023-01-15",
  "status": "Active"
}
```

### 2.5 DELETE Teacher
```
DELETE /api/teachers/1
```

## 3. Courses API (`/api/courses`)

### 3.1 GET All Courses
```
GET /api/courses
```

### 3.2 GET Course by ID
```
GET /api/courses/1
```

### 3.3 POST Create Course
```
POST /api/courses
Content-Type: application/json

{
  "courseName": "TOEIC 850+",
  "description": "Khóa học TOEIC nâng cao, mục tiêu 850 điểm",
  "level": "Advanced",
  "duration": 100,
  "fee": 6000000.00,
  "status": "Active"
}
```

### 3.4 PUT Update Course
```
PUT /api/courses/1
Content-Type: application/json

{
  "courseName": "IELTS Mastery 6.5+ (Updated)",
  "description": "Khóa học luyện thi IELTS - Updated",
  "level": "Advanced",
  "duration": 130,
  "fee": 9000000.00,
  "status": "Active"
}
```

### 3.5 DELETE Course
```
DELETE /api/courses/1
```

## 4. Rooms API (`/api/rooms`)

### 4.1 GET All Rooms
```
GET /api/rooms
```

### 4.2 GET Room by ID
```
GET /api/rooms/1
```

### 4.3 POST Create Room
```
POST /api/rooms
Content-Type: application/json

{
  "roomName": "P.301",
  "capacity": 35,
  "location": "Tầng 3 - Cơ sở 1",
  "status": "Available"
}
```

### 4.4 PUT Update Room
```
PUT /api/rooms/1
Content-Type: application/json

{
  "roomName": "P.101 (Updated)",
  "capacity": 30,
  "location": "Tầng 1 - Cơ sở 1",
  "status": "Maintenance"
}
```

### 4.5 DELETE Room
```
DELETE /api/rooms/1
```

## 6. Staff API (`/api/staff`)

### 6.1 GET All Staff
```
GET /api/staff
```

### 6.2 GET Staff by ID
```
GET /api/staff/1
```

### 6.3 POST Create Staff
```
POST /api/staff
Content-Type: application/json

{
  "fullName": "Trần Văn C",
  "role": "Consultant",
  "phone": "0987654321",
  "email": "tranvanc@center.com"
}
```

### 6.4 PUT Update Staff
```
PUT /api/staff/1
Content-Type: application/json

{
  "fullName": "Hoàng Tuấn (Updated)",
  "role": "Admin",
  "phone": "0988888888",
  "email": "admin@center.com"
}
```

### 6.5 DELETE Staff
```
DELETE /api/staff/1
```

## 7. Promotions API (`/api/promotions`)

### 7.1 GET All Promotions
```
GET /api/promotions
```

### 7.2 GET Promotion by ID
```
GET /api/promotions/1
```

### 7.3 POST Create Promotion
```
POST /api/promotions
Content-Type: application/json

{
  "promoCode": "SPRING2026",
  "discountPercent": 15.00,
  "startDate": "2026-03-01",
  "endDate": "2026-05-31",
  "description": "Khuyến mãi mùa xuân - Giảm 15%"
}
```

### 7.4 PUT Update Promotion
```
PUT /api/promotions/1
Content-Type: application/json

{
  "promoCode": "SUMMER2026",
  "discountPercent": 10.00,
  "startDate": "2026-05-01",
  "endDate": "2026-08-31",
  "description": "Khuyến mãi mùa hè - Giảm 10%"
}
```

### 7.5 DELETE Promotion
```
DELETE /api/promotions/1
```

## 8. Classes API (`/api/classes`)

### 8.1 GET All Classes
```
GET /api/classes
```

### 8.2 GET Class by ID
```
GET /api/classes/1
```

### 8.3 POST Create Class
```
POST /api/classes
Content-Type: application/json

{
  "className": "TOEIC-A02",
  "course": {"courseId": 1},
  "teacher": {"teacherId": 1},
  "room": {"roomId": 2},
  "startDate": "2026-04-01",
  "endDate": "2026-06-30",
  "maxStudent": 25,
  "status": "Pending"
}
```

### 8.4 PUT Update Class
```
PUT /api/classes/1
Content-Type: application/json

{
  "className": "IELTS-A01 (Updated)",
  "course": {"courseId": 1},
  "teacher": {"teacherId": 1},
  "room": {"roomId": 1},
  "startDate": "2026-03-01",
  "endDate": "2026-06-01",
  "maxStudent": 22,
  "status": "Ongoing"
}
```

### 8.5 DELETE Class
```
DELETE /api/classes/1
```

## 9. Placement Tests API (`/api/placement-tests`)

### 9.1 GET All Placement Tests
```
GET /api/placement-tests
```

### 9.2 GET Placement Test by ID
```
GET /api/placement-tests/1
```

### 9.3 POST Create Placement Test
```
POST /api/placement-tests
Content-Type: application/json

{
  "student": {"studentId": 1},
  "testDate": "2026-03-05",
  "score": 6.0,
  "recommendedLevel": "Intermediate"
}
```

### 9.4 PUT Update Placement Test
```
PUT /api/placement-tests/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "testDate": "2026-02-02",
  "score": 6.5,
  "recommendedLevel": "Upper-Intermediate"
}
```

### 9.5 DELETE Placement Test
```
DELETE /api/placement-tests/1
```

## 10. Enrollments API (`/api/enrollments`)

### 10.1 GET All Enrollments
```
GET /api/enrollments
```

### 10.2 GET Enrollment by ID
```
GET /api/enrollments/1
```

### 10.3 POST Create Enrollment
```
POST /api/enrollments
Content-Type: application/json

{
  "student": {"studentId": 2},
  "classEntity": {"classId": 2},
  "enrollmentDate": "2026-03-04",
  "status": "Registered",
  "result": "Pending"
}
```

### 10.4 PUT Update Enrollment
```
PUT /api/enrollments/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "classEntity": {"classId": 1},
  "enrollmentDate": "2026-02-10",
  "status": "Studying",
  "result": "Pending"
}
```

### 10.5 DELETE Enrollment
```
DELETE /api/enrollments/1
```

## 11. Schedules API (`/api/schedules`)

### 11.1 GET All Schedules
```
GET /api/schedules
```

### 11.2 GET Schedule by ID
```
GET /api/schedules/1
```

### 11.3 POST Create Schedule
```
POST /api/schedules
Content-Type: application/json

{
  "classEntity": {"classId": 1},
  "room": {"roomId": 1},
  "date": "2026-03-06",
  "startTime": "18:00:00",
  "endTime": "20:00:00"
}
```

### 11.4 PUT Update Schedule
```
PUT /api/schedules/1
Content-Type: application/json

{
  "classEntity": {"classId": 1},
  "room": {"roomId": 1},
  "date": "2026-03-02",
  "startTime": "18:00:00",
  "endTime": "20:00:00"
}
```

### 11.5 DELETE Schedule
```
DELETE /api/schedules/1
```

## 12. Invoices API (`/api/invoices`)

### 12.1 GET All Invoices
```
GET /api/invoices
```

### 12.2 GET Invoice by ID
```
GET /api/invoices/1
```

### 12.3 POST Create Invoice
```
POST /api/invoices
Content-Type: application/json

{
  "student": {"studentId": 2},
  "totalAmount": 3000000.00,
  "issueDate": "2026-03-04",
  "status": "Unpaid"
}
```

### 12.4 PUT Update Invoice
```
PUT /api/invoices/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "totalAmount": 8500000.00,
  "issueDate": "2026-02-10",
  "status": "Paid"
}
```

### 12.5 DELETE Invoice
```
DELETE /api/invoices/1
```

## 13. Payments API (`/api/payments`)

### 13.1 GET All Payments
```
GET /api/payments
```

### 13.2 GET Payment by ID
```
GET /api/payments/1
```

### 13.3 POST Create Payment
```
POST /api/payments
Content-Type: application/json

{
  "student": {"studentId": 1},
  "enrollment": {"enrollmentId": 1},
  "amount": 4000000.00,
  "paymentDate": "2026-03-04",
  "paymentMethod": "Card",
  "status": "Success"
}
```

### 13.4 PUT Update Payment
```
PUT /api/payments/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "enrollment": {"enrollmentId": 1},
  "amount": 8500000.00,
  "paymentDate": "2026-02-10",
  "paymentMethod": "BankTransfer",
  "status": "Success"
}
```

### 13.5 DELETE Payment
```
DELETE /api/payments/1
```

## 14. Attendances API (`/api/attendances`)

### 14.1 GET All Attendances
```
GET /api/attendances
```

### 14.2 GET Attendance by ID
```
GET /api/attendances/1
```

### 14.3 POST Create Attendance
```
POST /api/attendances
Content-Type: application/json

{
  "student": {"studentId": 1},
  "classEntity": {"classId": 1},
  "date": "2026-03-05",
  "status": "Present"
}
```

### 14.4 PUT Update Attendance
```
PUT /api/attendances/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "classEntity": {"classId": 1},
  "date": "2026-03-02",
  "status": "Present"
}
```

### 14.5 DELETE Attendance
```
DELETE /api/attendances/1
```

## 15. Results API (`/api/results`)

### 15.1 GET All Results
```
GET /api/results
```

### 15.2 GET Result by ID
```
GET /api/results/1
```

### 15.3 POST Create Result
```
POST /api/results
Content-Type: application/json

{
  "student": {"studentId": 2},
  "classEntity": {"classId": 2},
  "score": 7.5,
  "grade": "Good",
  "comment": "Cần cải thiện kỹ năng nghe"
}
```

### 15.4 PUT Update Result
```
PUT /api/results/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "classEntity": {"classId": 1},
  "score": 7.0,
  "grade": "Good",
  "comment": "Phát âm chuẩn, cần cải thiện ngữ pháp"
}
```

### 15.5 DELETE Result
```
DELETE /api/results/1
```

## 16. Certificates API (`/api/certificates`)

### 16.1 GET All Certificates
```
GET /api/certificates
```

### 16.2 GET Certificate by ID
```
GET /api/certificates/1
```

### 16.3 POST Create Certificate
```
POST /api/certificates
Content-Type: application/json

{
  "student": {"studentId": 2},
  "course": {"courseId": 2},
  "certificateName": "Chứng nhận Giao tiếp Tiếng Anh Cơ bản",
  "issueDate": "2026-03-04"
}
```

### 16.4 PUT Update Certificate
```
PUT /api/certificates/1
Content-Type: application/json

{
  "student": {"studentId": 1},
  "course": {"courseId": 2},
  "certificateName": "Chứng nhận Giao tiếp Tiếng Anh Cơ bản",
  "issueDate": "2025-12-20"
}
```

### 16.5 DELETE Certificate
```
DELETE /api/certificates/1
```

## 17. User Accounts API (`/api/user-accounts`)

### 17.1 GET All User Accounts
```
GET /api/user-accounts
```

### 17.2 GET User Account by ID
```
GET /api/user-accounts/1
```

### 17.3 POST Create User Account
```
POST /api/user-accounts
Content-Type: application/json

{
  "username": "teacher_leo",
  "passwordHash": "pass123",
  "role": "Teacher",
  "relatedId": 3,
  "status": "Active"
}
```

### 17.4 PUT Update User Account
```
PUT /api/user-accounts/1
Content-Type: application/json

{
  "username": "admin",
  "passwordHash": "admin123_updated",
  "role": "Admin",
  "relatedId": 1,
  "status": "Active"
}
```

### 17.5 DELETE User Account
```
DELETE /api/user-accounts/1
```

## HTTP Response Codes

- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **204 No Content** - Delete successful
- **400 Bad Request** - Invalid request data
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

## Testing Tips

1. Import sample data first: `mysql -u root -p mis_language_center < sample_data.sql`
2. Start the Spring Boot application: `mvn spring-boot:run`
3. Use Postman or cURL to test the endpoints
4. Check database for data changes after each operation
5. Monitor logs in console for SQL queries and errors

## Error Handling Examples

### Invalid ID (404)
```
GET http://localhost:8080/api/students/999
Response: 500 Internal Server Error with error message
```

### Invalid Data (400)
```
POST http://localhost:8080/api/students
Content-Type: application/json

{
  "fullName": "" // Empty name should be validated
}
```
