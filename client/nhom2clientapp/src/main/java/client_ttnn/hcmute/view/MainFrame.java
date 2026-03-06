package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.UserAccount;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private UserAccount currentUser;

    public MainFrame(UserAccount user) {
        this.currentUser = user;
        setTitle("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ - " + (user != null ? user.getRole() : ""));
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
        lblLogo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
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
        StaffManagerPanel staffPanel = new StaffManagerPanel(); // NHÂN SỰ
        RoomManagerPanel roomPanel = new RoomManagerPanel(); // PHÒNG HỌC MỚI THÊM
        ScheduleManagerPanel schedulePanel = new ScheduleManagerPanel(); // LỊCH HỌC
        TimetableViewPanel timetablePanel = new TimetableViewPanel(); // XEM TKB
        CourseManagerPanel coursePanel = new CourseManagerPanel();
        ClassManagerPanel classPanel = new ClassManagerPanel();
        EnrollmentManagerPanel enrollmentPanel = new EnrollmentManagerPanel();
        PlacementTestManagerPanel placementTestPanel = new PlacementTestManagerPanel();
        CertificateManagerPanel certificatePanel = new CertificateManagerPanel();
        
        // Đặt tên chuỗi cho từng Card để gọi
        contentPanel.add(studentPanel, "StudentPanel");
        contentPanel.add(teacherPanel, "TeacherPanel");
        contentPanel.add(staffPanel, "StaffPanel"); 
        contentPanel.add(roomPanel, "RoomPanel"); 
        contentPanel.add(schedulePanel, "SchedulePanel"); // LỊCH HỌC
        contentPanel.add(timetablePanel, "TimetablePanel"); // XEM TKB
        contentPanel.add(coursePanel, "CoursePanel");
        contentPanel.add(classPanel, "ClassPanel");
        contentPanel.add(enrollmentPanel, "EnrollmentPanel");
        contentPanel.add(placementTestPanel, "PlacementTestPanel");
        contentPanel.add(certificatePanel, "CertificatePanel");

        // --- Tạo các Nút Menu ---
        JButton btnStudent = createMenuButton("Quản Lý Học Viên");
        JButton btnTeacher = createMenuButton("Quản Lý Giảng Viên");
        JButton btnStaff = createMenuButton("Quản Lý Nhân Sự"); 
        JButton btnRoom = createMenuButton("Quản Lý Phòng Học"); 
        JButton btnSchedule = createMenuButton("Xếp Lịch Học"); // LỊCH HỌC
        JButton btnTimetable = createMenuButton("Xem Thời Khoá Biểu"); // XEM TKB
        JButton btnCourse = createMenuButton("Quản Lý Khóa Học");
        JButton btnClass = createMenuButton("Quản Lý Lớp Học");
        JButton btnEnrollment = createMenuButton("Quản Lý Ghi Danh");
        JButton btnPlacementTest = createMenuButton("Quản Lý Placement Test");
        JButton btnCertificate = createMenuButton("Quản Lý Chứng Chỉ");

        // Add action cho các nút để chuyển Card
        btnStudent.addActionListener(e -> cardLayout.show(contentPanel, "StudentPanel"));
        btnTeacher.addActionListener(e -> cardLayout.show(contentPanel, "TeacherPanel"));
        btnStaff.addActionListener(e -> cardLayout.show(contentPanel, "StaffPanel")); 
        btnRoom.addActionListener(e -> cardLayout.show(contentPanel, "RoomPanel")); 
        btnSchedule.addActionListener(e -> cardLayout.show(contentPanel, "SchedulePanel")); // LỊCH HỌC
        btnTimetable.addActionListener(e -> cardLayout.show(contentPanel, "TimetablePanel")); // XEM TKB
        btnCourse.addActionListener(e -> cardLayout.show(contentPanel, "CoursePanel"));
        btnClass.addActionListener(e -> cardLayout.show(contentPanel, "ClassPanel"));
        btnEnrollment.addActionListener(e -> cardLayout.show(contentPanel, "EnrollmentPanel"));
        btnPlacementTest.addActionListener(e -> cardLayout.show(contentPanel, "PlacementTestPanel"));
        btnCertificate.addActionListener(e -> cardLayout.show(contentPanel, "CertificatePanel"));

        sidebarPanel.add(btnStudent);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnTeacher);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnStaff);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnRoom);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnSchedule); // LỊCH HỌC
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnTimetable); // XEM TKB
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnCourse);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnClass);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnEnrollment);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnPlacementTest);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnCertificate);

        // --- LOGIC PHÂN QUYỀN HEADER ---
        // Nếu là GIÁO VIÊN (Teacher), ẩn các nút Không thuộc thẩm quyền
        if (currentUser != null && "Teacher".equals(currentUser.getRole())) {
            btnStudent.setVisible(false);
            btnTeacher.setVisible(false);
            btnStaff.setVisible(false);
            btnRoom.setVisible(false);
            btnCourse.setVisible(false);
            btnClass.setVisible(false);
            btnEnrollment.setVisible(false);
            btnPlacementTest.setVisible(false);
            btnCertificate.setVisible(false);
            // Teacher chỉ được xếp lịch và Xem TKB
            // Mở mặc định Tab TKB cho Teacher lúc vào
            cardLayout.show(contentPanel, "TimetablePanel"); 
        }

        // Thêm Sidebar và Content vào Frame
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Hàm tiện ích tạo Nút Menu cho Sidebar
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Chiều rộng kéo dài hết mức
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }
}
