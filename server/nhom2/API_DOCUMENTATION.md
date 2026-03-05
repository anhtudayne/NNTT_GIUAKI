# Language Center Management System - API Server

A comprehensive Spring Boot REST API server for managing a Language Center database system with complete CRUD operations for all components.

## Project Overview

This Spring Boot application provides a RESTful API for a Language Center Management System. It includes entities for managing branches, courses, teachers, students, classes, enrollments, schedules, payments, and more.

## Prerequisites

- JDK 21 or higher
- Maven 3.6+
- MySQL 5.7+
- Git

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL Connector/J**
- **Lombok**
- **Maven**

## Project Structure

```
src/
├── main/
│   ├── java/trungtamngoaingu/hcmute/
│   │   ├── controller/     # REST API endpoints
│   │   ├── entity/         # JPA entity classes
│   │   ├── repository/     # Data access layer
│   │   ├── service/        # Business logic layer
│   │   └── App.java        # Main Spring Boot application
│   └── resources/
│       └── application.properties  # Configuration
└── test/                   # Unit tests
```

## Database Setup

### 1. Create Database Using MySQL

Execute the SQL initialization script to create all tables and sample data:

```bash
# Option 1: Using MySQL CLI
mysql -u your_username -p < init_database.sql

# Option 2: Using MySQL Workbench or another MySQL client
# Open init_database.sql and execute the script
```

### 2. Update Database Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/LanguageCenterDB?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Configuration
server.port=8080
```

## Building the Project

```bash
# Clean build
mvn clean install

# Compile only
mvn compile

# Build and package
mvn package
```

## Running the Application

```bash
# Using Maven
mvn spring-boot:run

# Using Java directly
java -jar target/nhom2-1.0-SNAPSHOT.jar

# The server will start on http://localhost:8080
```

## API Endpoints

### Base URL
```
http://localhost:8080/api
```

### 1. Branches Management
- `GET /api/branches` - Get all branches
- `GET /api/branches/{id}` - Get branch by ID
- `POST /api/branches` - Create new branch
- `PUT /api/branches/{id}` - Update branch
- `DELETE /api/branches/{id}` - Delete branch

### 2. Rooms Management
- `GET /api/rooms` - Get all rooms
- `GET /api/rooms/{id}` - Get room by ID
- `POST /api/rooms` - Create new room
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room

### 3. Courses Management
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `POST /api/courses` - Create new course
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

### 4. Teachers Management
- `GET /api/teachers` - Get all teachers
- `GET /api/teachers/{id}` - Get teacher by ID
- `POST /api/teachers` - Create new teacher
- `PUT /api/teachers/{id}` - Update teacher
- `DELETE /api/teachers/{id}` - Delete teacher

### 5. Students Management
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `POST /api/students` - Create new student
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### 6. Staff Management
- `GET /api/staff` - Get all staff
- `GET /api/staff/{id}` - Get staff by ID
- `POST /api/staff` - Create new staff member
- `PUT /api/staff/{id}` - Update staff
- `DELETE /api/staff/{id}` - Delete staff

### 7. Promotions Management
- `GET /api/promotions` - Get all promotions
- `GET /api/promotions/{id}` - Get promotion by ID
- `POST /api/promotions` - Create new promotion
- `PUT /api/promotions/{id}` - Update promotion
- `DELETE /api/promotions/{id}` - Delete promotion

### 8. Classes Management
- `GET /api/classes` - Get all classes
- `GET /api/classes/{id}` - Get class by ID
- `POST /api/classes` - Create new class
- `PUT /api/classes/{id}` - Update class
- `DELETE /api/classes/{id}` - Delete class

### 9. Placement Tests Management
- `GET /api/placement-tests` - Get all placement tests
- `GET /api/placement-tests/{id}` - Get test by ID
- `POST /api/placement-tests` - Create new test
- `PUT /api/placement-tests/{id}` - Update test
- `DELETE /api/placement-tests/{id}` - Delete test

### 10. Enrollments Management
- `GET /api/enrollments` - Get all enrollments
- `GET /api/enrollments/{id}` - Get enrollment by ID
- `POST /api/enrollments` - Create new enrollment
- `PUT /api/enrollments/{id}` - Update enrollment
- `DELETE /api/enrollments/{id}` - Delete enrollment

### 11. Schedules Management
- `GET /api/schedules` - Get all schedules
- `GET /api/schedules/{id}` - Get schedule by ID
- `POST /api/schedules` - Create new schedule
- `PUT /api/schedules/{id}` - Update schedule
- `DELETE /api/schedules/{id}` - Delete schedule

### 12. Invoices Management
- `GET /api/invoices` - Get all invoices
- `GET /api/invoices/{id}` - Get invoice by ID
- `POST /api/invoices` - Create new invoice
- `PUT /api/invoices/{id}` - Update invoice
- `DELETE /api/invoices/{id}` - Delete invoice

### 13. Payments Management
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `POST /api/payments` - Create new payment
- `PUT /api/payments/{id}` - Update payment
- `DELETE /api/payments/{id}` - Delete payment

### 14. Attendance Management
- `GET /api/attendances` - Get all attendances
- `GET /api/attendances/{id}` - Get attendance by ID
- `POST /api/attendances` - Create new attendance
- `PUT /api/attendances/{id}` - Update attendance
- `DELETE /api/attendances/{id}` - Delete attendance

### 15. Results Management
- `GET /api/results` - Get all results
- `GET /api/results/{id}` - Get result by ID
- `POST /api/results` - Create new result
- `PUT /api/results/{id}` - Update result
- `DELETE /api/results/{id}` - Delete result

### 16. Certificates Management
- `GET /api/certificates` - Get all certificates
- `GET /api/certificates/{id}` - Get certificate by ID
- `POST /api/certificates` - Create new certificate
- `PUT /api/certificates/{id}` - Update certificate
- `DELETE /api/certificates/{id}` - Delete certificate

### 17. User Accounts Management
- `GET /api/user-accounts` - Get all user accounts
- `GET /api/user-accounts/{id}` - Get account by ID
- `POST /api/user-accounts` - Create new account
- `PUT /api/user-accounts/{id}` - Update account
- `DELETE /api/user-accounts/{id}` - Delete account

## Request/Response Examples

### Create a New Student
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

### Get All Courses
```bash
curl -X GET http://localhost:8080/api/courses
```

### Update a Teacher
```bash
curl -X PUT http://localhost:8080/api/teachers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Trần Châu Anh",
    "phone": "0905555555",
    "email": "chauan.tran@center.com",
    "specialty": "TOEFL",
    "hireDate": "2023-01-15",
    "status": "Active"
  }'
```

### Delete a Branch
```bash
curl -X DELETE http://localhost:8080/api/branches/1
```

## Data Model Relationships

![Entity Relationships](https://via.placeholder.com/800x600?text=Entity+Diagram)

### Key Relationships:
- **Branch** → Independent entity
- **Course** → Independent entity
- **Teacher** → Independent entity
- **Student** → Independent entity
- **Room** → Independent entity
- **Staff** → Independent entity
- **Promotion** → Independent entity
- **Class** → depends on Course, Teacher, Room
- **PlacementTest** → depends on Student
- **Enrollment** → depends on Student, Class
- **Schedule** → depends on Class, Room
- **Invoice** → depends on Student
- **Payment** → depends on Student, Enrollment
- **Attendance** → depends on Student, Class
- **Result** → depends on Student, Class
- **Certificate** → depends on Student, Course
- **UserAccount** → System entity

## Features

✅ **CRUD Operations** - Complete Create, Read, Update, Delete for all entities
✅ **RESTful API** - Standard HTTP methods (GET, POST, PUT, DELETE)
✅ **JSON Support** - All responses in JSON format
✅ **Error Handling** - Proper HTTP status codes and error messages
✅ **CORS Support** - Cross-Origin Resource Sharing enabled
✅ **JPA/Hibernate ORM** - Database abstraction layer
✅ **Validation** - Input validation using Spring Boot Validation
✅ **Naming Conventions** - Consistent naming throughout the codebase

## Testing the API

### Using REST Client (VS Code Extension)
Create a file `api-test.http`:

```http
### Get all students
GET http://localhost:8080/api/students

### Create new student
POST http://localhost:8080/api/students
Content-Type: application/json

{
  "fullName": "Lý Văn B",
  "dateOfBirth": "2004-08-15",
  "gender": "Male",
  "phone": "0933334444",
  "email": "vanlam@gmail.com",
  "address": "Quận 2, TP.HCM",
  "registrationDate": "2026-03-04",
  "status": "Active"
}

### Get student by ID
GET http://localhost:8080/api/students/1

### Update student
PUT http://localhost:8080/api/students/1
Content-Type: application/json

{
  "fullName": "Lý Văn B Updated",
  "dateOfBirth": "2004-08-15",
  "gender": "Male",
  "phone": "0933334444",
  "email": "vanlam.updated@gmail.com",
  "address": "Quận 2, TP.HCM",
  "registrationDate": "2026-03-04",
  "status": "Active"
}

### Delete student
DELETE http://localhost:8080/api/students/1
```

### Using cURL
```bash
# Test the API is running
curl -X GET http://localhost:8080/api/students

# See formatted output
curl -X GET http://localhost:8080/api/students | jq '.'
```

## Common Issues & Solutions

### Connection Refused
- Make sure MySQL is running
- Verify database credentials in `application.properties`
- Check MySQL port (default 3306)

### Table Not Found
- Run the `init_database.sql` script to create tables
- Verify database name matches in `application.properties`

### Port Already in Use
- Change server port in `application.properties`: `server.port=8081`

## Development Notes

- The application uses **Lombok** to reduce boilerplate code with annotations like `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Lazy loading is used for relationships to optimize database queries
- All controllers use `@CrossOrigin(origins = "*")` for CORS support
- Proper HTTP status codes are returned:
  - `200 OK` - Successful GET/PUT
  - `201 CREATED` - Successful POST
  - `204 NO CONTENT` - Successful DELETE
  - `404 NOT FOUND` - Resource not found

## Future Enhancements

- [ ] Add Spring Security for authentication/authorization
- [ ] Implement custom query methods in repositories
- [ ] Add pagination and sorting support
- [ ] Implement business logic for payment processing
- [ ] Add audit logging
- [ ] Create data export functionality (PDF, Excel)
- [ ] Add automated testing with JUnit and MockMvc
- [ ] Implement API documentation with Swagger/OpenAPI
- [ ] Add caching mechanisms with Redis

## Configuration Files

### pom.xml
Main Maven configuration with all dependencies

### application.properties
- Database connection settings
- JPA/Hibernate configuration
- Server port configuration
- Logging levels

### init_database.sql
SQL script to initialize the database with all tables and sample data

## License

This project is part of an educational assignment for the Language Center Management System course.

## Support

For issues or questions, please contact the development team or check the project documentation.

## Authors

- Group 2 - HCMUTE
- Course: Language Center Management System Development

---

**Last Updated**: March 4, 2026  
**Server Version**: 1.0.0  
**Java Version**: 21  
**Spring Boot Version**: 3.2.0
