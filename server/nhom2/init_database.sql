-- ==============================================================================
-- KHỞI TẠO DATABASE
-- ==============================================================================
DROP DATABASE IF EXISTS LanguageCenterDB;
CREATE DATABASE LanguageCenterDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE LanguageCenterDB;

-- ==============================================================================
-- I. TẠO CÁC BẢNG ĐỘC LẬP (KHÔNG CHỨA KHÓA NGOẠI)
-- ==============================================================================

CREATE TABLE Branch (
    BranchID INT AUTO_INCREMENT PRIMARY KEY,
    BranchName VARCHAR(100) NOT NULL,
    Address VARCHAR(255),
    Phone VARCHAR(20),
    Status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

CREATE TABLE Room (
    RoomID INT AUTO_INCREMENT PRIMARY KEY,
    RoomName VARCHAR(50) NOT NULL,
    Capacity INT NOT NULL,
    Location VARCHAR(100),
    Status ENUM('Available', 'Maintenance', 'Inactive') DEFAULT 'Available'
);

CREATE TABLE Course (
    CourseID INT AUTO_INCREMENT PRIMARY KEY,
    CourseName VARCHAR(150) NOT NULL,
    Description TEXT,
    Level ENUM('Beginner', 'Intermediate', 'Advanced') NOT NULL,
    Duration INT COMMENT 'Tổng số giờ học',
    Fee DECIMAL(10, 2) NOT NULL,
    Status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

CREATE TABLE Teacher (
    TeacherID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(100) UNIQUE,
    Specialty VARCHAR(100),
    HireDate DATE,
    Status ENUM('Active', 'OnLeave', 'Inactive') DEFAULT 'Active'
);

CREATE TABLE Student (
    StudentID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    DateOfBirth DATE,
    Gender ENUM('Male', 'Female', 'Other'),
    Phone VARCHAR(20),
    Email VARCHAR(100) UNIQUE,
    Address VARCHAR(255),
    RegistrationDate DATE,
    Status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

CREATE TABLE Staff (
    StaffID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Role ENUM('Admin', 'Consultant', 'Accountant') NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(100) UNIQUE
);

CREATE TABLE Promotion (
    PromotionID INT AUTO_INCREMENT PRIMARY KEY,
    PromoCode VARCHAR(50) UNIQUE NOT NULL,
    DiscountPercent DECIMAL(5, 2) NOT NULL,
    StartDate DATE,
    EndDate DATE,
    Description VARCHAR(255)
);

-- ==============================================================================
-- II. TẠO CÁC BẢNG VẬN HÀNH & ĐÀO TẠO (KHÓA NGOẠI CẤP 1)
-- ==============================================================================

CREATE TABLE Class (
    ClassID INT AUTO_INCREMENT PRIMARY KEY,
    ClassName VARCHAR(100) NOT NULL,
    CourseID INT,
    TeacherID INT,
    RoomID INT,
    StartDate DATE,
    EndDate DATE,
    MaxStudent INT DEFAULT 20,
    Status ENUM('Pending', 'Ongoing', 'Completed', 'Canceled') DEFAULT 'Pending',
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE SET NULL,
    FOREIGN KEY (TeacherID) REFERENCES Teacher(TeacherID) ON DELETE SET NULL,
    FOREIGN KEY (RoomID) REFERENCES Room(RoomID) ON DELETE SET NULL
);

CREATE TABLE PlacementTest (
    TestID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    TestDate DATE,
    Score DECIMAL(5,2),
    RecommendedLevel VARCHAR(50),
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE
);

-- ==============================================================================
-- III. TẠO CÁC BẢNG GIAO DỊCH & LỊCH TRÌNH (KHÓA NGOẠI CẤP 2)
-- ==============================================================================

CREATE TABLE Enrollment (
    EnrollmentID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    ClassID INT,
    EnrollmentDate DATE,
    Status ENUM('Registered', 'Studying', 'Dropped', 'Completed') DEFAULT 'Registered',
    Result ENUM('Pass', 'Fail', 'Pending') DEFAULT 'Pending',
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE
);

CREATE TABLE Schedule (
    ScheduleID INT AUTO_INCREMENT PRIMARY KEY,
    ClassID INT,
    RoomID INT,
    Date DATE NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE,
    FOREIGN KEY (RoomID) REFERENCES Room(RoomID) ON DELETE SET NULL
);

CREATE TABLE Invoice (
    InvoiceID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    TotalAmount DECIMAL(12, 2) NOT NULL,
    IssueDate DATE,
    Status ENUM('Unpaid', 'Partial', 'Paid') DEFAULT 'Unpaid',
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE
);

-- ==============================================================================
-- IV. TẠO CÁC BẢNG CHI TIẾT PHÁT SINH (KHÓA NGOẠI CẤP 3)
-- ==============================================================================

CREATE TABLE Payment (
    PaymentID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    EnrollmentID INT,
    Amount DECIMAL(10, 2) NOT NULL,
    PaymentDate DATE,
    PaymentMethod ENUM('Cash', 'BankTransfer', 'Momo', 'Card') NOT NULL,
    Status ENUM('Success', 'Failed', 'Refunded') DEFAULT 'Success',
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (EnrollmentID) REFERENCES Enrollment(EnrollmentID) ON DELETE CASCADE
);

CREATE TABLE Attendance (
    AttendanceID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    ClassID INT,
    Date DATE NOT NULL,
    Status ENUM('Present', 'Absent', 'Late') NOT NULL,
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE
);

CREATE TABLE Result (
    ResultID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    ClassID INT,
    Score DECIMAL(5, 2),
    Grade VARCHAR(10),
    Comment TEXT,
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE
);

CREATE TABLE Certificate (
    CertificateID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    CourseID INT,
    CertificateName VARCHAR(150),
    IssueDate DATE,
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
);

-- ==============================================================================
-- V. TẠO BẢNG HỆ THỐNG
-- ==============================================================================

CREATE TABLE UserAccount (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Role ENUM('Admin', 'Teacher', 'Student', 'Staff') NOT NULL,
    RelatedID INT COMMENT 'ID của Teacher, Student hoặc Staff',
    Status ENUM('Active', 'Locked') DEFAULT 'Active'
);

-- ==============================================================================
-- VI. CHÈN DỮ LIỆU MẪU (MOCK DATA)
-- ==============================================================================

-- 1. Insert Branches
INSERT INTO Branch (BranchName, Address, Phone) VALUES 
('Cơ sở 1 - Trung Tâm', '123 Lê Lợi, Quận 1, TP.HCM', '0281234567'),
('Cơ sở 2 - Vệ Tinh', '456 Điện Biên Phủ, Bình Thạnh, TP.HCM', '0289876543');

-- 2. Insert Rooms
INSERT INTO Room (RoomName, Capacity, Location) VALUES 
('P.101', 25, 'Tầng 1 - Cơ sở 1'),
('P.102', 30, 'Tầng 1 - Cơ sở 1'),
('P.201', 20, 'Tầng 2 - Cơ sở 2');

-- 3. Insert Courses
INSERT INTO Course (CourseName, Description, Level, Duration, Fee) VALUES 
('IELTS Mastery 6.5+', 'Khóa học luyện thi IELTS mục tiêu 6.5', 'Advanced', 120, 8500000),
('Giao tiếp Tiếng Anh Cơ bản', 'Lấy lại căn bản giao tiếp', 'Beginner', 60, 3000000);

-- 4. Insert Teachers
INSERT INTO Teacher (FullName, Phone, Email, Specialty, HireDate) VALUES 
('Nguyễn Văn An', '0901111111', 'an.nguyen@center.com', 'IELTS', '2023-01-15'),
('Trần Thị Bích', '0902222222', 'bich.tran@center.com', 'Giao tiếp', '2023-06-20');

-- 5. Insert Students
INSERT INTO Student (FullName, DateOfBirth, Gender, Phone, Email, Address, RegistrationDate) VALUES 
('Lê Hoàng Long', '2003-05-12', 'Male', '0931111222', 'long.le@gmail.com', 'Quận 7, TP.HCM', '2026-02-01'),
('Phạm Mai Anh', '2004-10-25', 'Female', '0933333444', 'maianh.pham@gmail.com', 'Thủ Đức, TP.HCM', '2026-02-15');

-- 6. Insert Staff
INSERT INTO Staff (FullName, Role, Phone, Email) VALUES 
('Hoàng Tuấn', 'Admin', '0988888888', 'admin@center.com'),
('Vũ Lê Na', 'Consultant', '0987777777', 'lena.vu@center.com');

-- 7. Insert Promotions
INSERT INTO Promotion (PromoCode, DiscountPercent, StartDate, EndDate, Description) VALUES 
('SUMMER2026', 10.00, '2026-05-01', '2026-08-31', 'Giảm 10% học phí mùa hè');

-- 8. Insert Classes
INSERT INTO Class (ClassName, CourseID, TeacherID, RoomID, StartDate, EndDate, MaxStudent, Status) VALUES 
('IELTS-A01', 1, 1, 1, '2026-03-01', '2026-06-01', 20, 'Ongoing'),
('COM-B01', 2, 2, 3, '2026-03-15', '2026-05-15', 25, 'Pending');

-- 9. Insert Placement Tests
INSERT INTO PlacementTest (StudentID, TestDate, Score, RecommendedLevel) VALUES 
(1, '2026-02-02', 5.5, 'Intermediate');

-- 10. Insert Enrollments
INSERT INTO Enrollment (StudentID, ClassID, EnrollmentDate, Status) VALUES 
(1, 1, '2026-02-10', 'Studying'),
(2, 2, '2026-02-16', 'Registered');

-- 11. Insert Schedules
INSERT INTO Schedule (ClassID, RoomID, Date, StartTime, EndTime) VALUES 
(1, 1, '2026-03-02', '18:00:00', '20:00:00'),
(1, 1, '2026-03-04', '18:00:00', '20:00:00');

-- 12. Insert Invoices
INSERT INTO Invoice (StudentID, TotalAmount, IssueDate, Status) VALUES 
(1, 8500000, '2026-02-10', 'Paid'),
(2, 3000000, '2026-02-16', 'Unpaid');

-- 13. Insert Payments
INSERT INTO Payment (StudentID, EnrollmentID, Amount, PaymentDate, PaymentMethod) VALUES 
(1, 1, 8500000, '2026-02-10', 'BankTransfer');

-- 14. Insert Attendances
INSERT INTO Attendance (StudentID, ClassID, Date, Status) VALUES 
(1, 1, '2026-03-02', 'Present');

-- 15. Insert Results
INSERT INTO Result (StudentID, ClassID, Score, Grade, Comment) VALUES 
(1, 1, 7.0, 'Good', 'Phát âm chuẩn, cần cải thiện ngữ pháp');

-- 16. Insert Certificates
INSERT INTO Certificate (StudentID, CourseID, CertificateName, IssueDate) VALUES 
(1, 2, 'Chứng nhận Giao tiếp Tiếng Anh Cơ bản', '2025-12-20');

-- 17. Insert User Accounts
INSERT INTO UserAccount (Username, PasswordHash, Role, RelatedID) VALUES 
('admin', 'admin123', 'Admin', 1), 
('teacher_an', 'pass123', 'Teacher', 1), 
('student_long', 'pass123', 'Student', 1);
