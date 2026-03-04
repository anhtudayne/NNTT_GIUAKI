package com.languagecenter.view;

import com.languagecenter.model.Student;
import com.languagecenter.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private StudentService studentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public MainFrame() {
        setTitle("Quản lý Học Sinh - Language Center");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Giữa màn hình

        // Layout chính
        setLayout(new BorderLayout());

        // Tạo Toolbar trên cùng
        JPanel topPanel = new JPanel();
        JButton btnLoadAll = new JButton("Danh sách toàn bộ");
        JButton btnLoadActive = new JButton("Chỉ Hiện Học Viên Active");
        topPanel.add(btnLoadAll);
        topPanel.add(btnLoadActive);

        add(topPanel, BorderLayout.NORTH);

        // Tạo Bảng (Table)
        String[] columns = {"ID", "Họ Tên (Học Sinh)", "Email", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            // Khởi tạo Service và nạp list sinh viên cơ bản vào DB
            studentService = new StudentService();
            studentService.initData();
            // Nạp data lúc mới mở
            loadDataToTable(studentService.getAllStudents());
        } catch (Throwable e) {
            System.err.println("Lỗi kết nối MySQL: Màn hình sẽ hiển thị dữ liệu ảo.");
            // Nạp dữ liệu ảo hiển thị chữ "học sinh" nếu DB chưa được bật
            tableModel.addRow(new Object[]{1, "Học sinh Test (Chưa bật MySQL)", "test@mail", "Active"});
        }

        // Gắn sự kiện Button
        btnLoadAll.addActionListener(e -> {
            loadDataToTable(studentService.getAllStudents());
        });

        btnLoadActive.addActionListener(e -> {
            // Test thử Stream filter (chỉ load ACTIVE)
            loadDataToTable(studentService.getActiveStudents());
        });
    }

    private void loadDataToTable(List<Student> students) {
        // Xóa data cũ
        tableModel.setRowCount(0);
        
        // Nạp data mới
        for (Student s : students) {
            Object[] row = {
                    s.getStudentId(),
                    s.getFullName(),
                    s.getEmail(),
                    s.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}
