package client_ttnn.hcmute.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Tạo Sidebar (Menu bên trái)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(280, 0)); // Tăng độ rộng lên 280
        sidebarPanel.setBackground(new Color(44, 62, 80)); // Màu nền tối (Midnight Blue)
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Tiêu đề Logo
        JLabel lblLogo = new JLabel("LANGUAGE CENTER", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        sidebarPanel.add(lblLogo);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Khoảng cách

        // 2. Tạo phần Nội dung chính (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Breathing room

        // --- Khởi tạo các Panel chức năng ---
        StudentManagerPanel studentPanel = new StudentManagerPanel();
        TeacherManagerPanel teacherPanel = new TeacherManagerPanel();
        
        // Đặt tên chuỗi cho từng Card để gọi
        contentPanel.add(studentPanel, "StudentPanel");
        contentPanel.add(teacherPanel, "TeacherPanel");
        // contentPanel.add(new ClassManagerPanel(), "ClassPanel");

        // --- Tạo các Nút Menu ---
        JButton btnStudent = createMenuButton("👩‍🎓 Quản Lý Học Viên");
        JButton btnTeacher = createMenuButton("👨‍🏫 Quản Lý Giảng Viên");
        // JButton btnClass = createMenuButton("🏫 Quản Lý Lớp Học");

        // Add action cho các nút để chuyển Card
        btnStudent.addActionListener(e -> cardLayout.show(contentPanel, "StudentPanel"));
        btnTeacher.addActionListener(e -> cardLayout.show(contentPanel, "TeacherPanel"));

        sidebarPanel.add(btnStudent);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnTeacher);
        // sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        // sidebarPanel.add(btnClass);

        // Thêm Sidebar và Content vào Frame
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Hàm tiện ích tạo Nút Menu cho Sidebar
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Chiều rộng kéo dài hết mức của Sidebar
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style tùy biến thêm (có thể bỏ qua nếu dùng FlatLaf Default nhưng Custom nhẹ cho đẹp)
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }
}
