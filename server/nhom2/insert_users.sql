USE LanguageCenterDB;

-- Chèn 3 tài khoản mẫu với 3 vai trò khác nhau vào bảng có sẵn (passwordHash, role, status)
INSERT INTO UserAccount (username, passwordHash, role, status) VALUES 
('admin', '123456', 'Admin', 'Active'),
('teacher1', '123456', 'Teacher', 'Active'),
('staff1', '123456', 'Staff', 'Active');
