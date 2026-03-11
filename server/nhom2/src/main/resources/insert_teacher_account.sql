-- 1. Thêm một Giáo viên mới vào bảng Teacher
INSERT INTO teacher (full_name, phone, email, specialty, hire_date, status)
VALUES ('Nguyễn Giáo Viên Mới', '0987654321', 'giaovien1@trungtam.com', 'IELTS, TOEIC', '2023-01-01', 'Active');

-- 2. Lấy ID của giáo viên vừa được tạo (Giả sử ID tự tăng là X)
-- Ở đây dùng biến @newTeacherId để lấy ID vừa Insert xong
SET @newTeacherId = LAST_INSERT_ID();

-- 3. Tạo một Tài khoản Hệ thống cho vị giáo viên này
-- Gắn @newTeacherId vào cột related_id và set Role = 'Teacher'
INSERT INTO user_account (username, password_hash, role, related_id, status)
VALUES ('giaovien1', '123456', 'Teacher', @newTeacherId, 'Active');

-- KIỂM TRA LẠI DỮ LIỆU BẰNG CÂU LỆNH SAU:
-- SELECT u.username, u.role, t.full_name, t.specialty 
-- FROM user_account u 
-- JOIN teacher t ON u.related_id = t.teacher_id
-- WHERE u.username = 'giaovien1';
