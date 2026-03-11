package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.UserAccount;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Color DEFAULT_MENU_BG = new Color(52, 73, 94);
    private static final Color RESOURCE_MENU_BG = new Color(26, 188, 156);
    private static final Color ACTIVE_MENU_BG = new Color(241, 196, 15);
    
    // Màu riêng cho từng nút quản lý nguồn lực
    private static final Color STUDENT_BTN_COLOR = new Color(52, 152, 219);  // Xanh dương
    private static final Color TEACHER_BTN_COLOR = new Color(155, 89, 182);  // Tím
    private static final Color STAFF_BTN_COLOR = new Color(230, 126, 34);    // Cam
    private static final Color ROOM_BTN_COLOR = new Color(22, 160, 133);     // Xanh lá
    
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
        DashboardPanel dashboardPanel = new DashboardPanel();
        StudentManagerPanel studentPanel = new StudentManagerPanel();
        TeacherManagerPanel teacherPanel = new TeacherManagerPanel();
        StaffManagerPanel staffPanel = new StaffManagerPanel(); // NHÂN SỰ
        RoomManagerPanel roomPanel = new RoomManagerPanel(); // PHÒNG HỌC
        ScheduleManagerPanel schedulePanel = new ScheduleManagerPanel(); // LỊCH HỌC
        TimetableViewPanel timetablePanel = new TimetableViewPanel(); // XEM TKB
        AttendanceManagerPanel attendancePanel = new AttendanceManagerPanel(this.currentUser); // ĐIỂM DANH LỚP
        EducationManagerPanel educationPanel = new EducationManagerPanel(); // Gộp: Lớp học, Khóa học
        EnrollmentManagerPanel enrollmentPanel = new EnrollmentManagerPanel();
        PlacementTestManagerPanel placementTestPanel = new PlacementTestManagerPanel();
        CertificateManagerPanel certificatePanel = new CertificateManagerPanel();
        FinanceManagerPanel financePanel = new FinanceManagerPanel(); // Gộp: Hóa đơn, Thanh toán, Khuyến mãi

        // Đặt tên chuỗi cho từng Card để gọi
        contentPanel.add(dashboardPanel, "DashboardPanel");
        contentPanel.add(studentPanel, "StudentPanel");
        contentPanel.add(teacherPanel, "TeacherPanel");
        contentPanel.add(staffPanel, "StaffPanel");
        contentPanel.add(roomPanel, "RoomPanel");
        contentPanel.add(schedulePanel, "SchedulePanel");
        contentPanel.add(timetablePanel, "TimetablePanel");
        contentPanel.add(attendancePanel, "AttendancePanel");
        contentPanel.add(educationPanel, "EducationPanel");
        contentPanel.add(enrollmentPanel, "EnrollmentPanel");
        contentPanel.add(placementTestPanel, "PlacementTestPanel");
        contentPanel.add(certificatePanel, "CertificatePanel");
        contentPanel.add(financePanel, "FinancePanel");

        // --- Tạo các Nút Menu ---
        JButton btnDashboard = createMenuButton("Dashboard");
        JButton btnStudent = createResourceButton("👤 Quản Lý Học Viên");
        JButton btnTeacher = createResourceButton("👨‍🎓 Quản Lý Giảng Viên");
        JButton btnStaff = createResourceButton("👥 Quản Lý Nhân Sự"); 
        JButton btnRoom = createResourceButton("🏫 Quản Lý Phòng Học"); 
        JButton btnToggleManagement = createMenuButton("Quản Lý Nguồn Lực ▸");
        JButton btnSchedule = createMenuButton("Xếp Lịch Học"); // LỊCH HỌC
        JButton btnTimetable = createMenuButton("Xem Thời Khoá Biểu"); // XEM TKB
        JButton btnAttendance = createMenuButton("Điểm Danh Lớp"); // ĐIỂM DANH LỚP
        JButton btnEducation = createMenuButton("Khóa học & Lớp học"); // ĐÀO TẠO
        JButton btnEnrollment = createMenuButton("Quản Lý Ghi Danh");
        JButton btnPlacementTest = createMenuButton("Quản Lý Placement Test");
        JButton btnCertificate = createMenuButton("Quản Lý Chứng Chỉ");
        JButton btnFinance = createMenuButton("Hóa đơn, Thanh toán & Khuyến mãi");

        // Áp dụng màu riêng cho nút toggle
        btnToggleManagement.setBackground(new Color(39, 174, 96));
        btnToggleManagement.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnToggleManagement.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        btnToggleManagement.setForeground(Color.WHITE);
        
        // Áp dụng màu riêng cho từng nút nguồn lực
        btnStudent.setBackground(STUDENT_BTN_COLOR);
        btnStudent.setForeground(Color.WHITE);
        btnTeacher.setBackground(TEACHER_BTN_COLOR);
        btnTeacher.setForeground(Color.WHITE);
        btnStaff.setBackground(STAFF_BTN_COLOR);
        btnStaff.setForeground(Color.WHITE);
        btnRoom.setBackground(ROOM_BTN_COLOR);
        btnRoom.setForeground(Color.WHITE);
        
        JButton[] resourceTabButtons = {btnStudent, btnTeacher, btnStaff, btnRoom};
        JButton[] navigationButtons = {
            btnDashboard, btnStudent, btnTeacher, btnStaff, btnRoom,
            btnSchedule, btnTimetable, btnAttendance, btnEducation,
            btnEnrollment, btnPlacementTest, btnCertificate, btnFinance
        };

        // Add action cho các nút để chuyển Card
        btnDashboard.addActionListener(e -> {
            cardLayout.show(contentPanel, "DashboardPanel");
            setActiveMenuButton(btnDashboard, navigationButtons, resourceTabButtons);
        });
        btnStudent.addActionListener(e -> {
            cardLayout.show(contentPanel, "StudentPanel");
            setActiveMenuButton(btnStudent, navigationButtons, resourceTabButtons);
        });
        btnTeacher.addActionListener(e -> {
            cardLayout.show(contentPanel, "TeacherPanel");
            setActiveMenuButton(btnTeacher, navigationButtons, resourceTabButtons);
        });
        btnStaff.addActionListener(e -> {
            cardLayout.show(contentPanel, "StaffPanel");
            setActiveMenuButton(btnStaff, navigationButtons, resourceTabButtons);
        });
        btnRoom.addActionListener(e -> {
            cardLayout.show(contentPanel, "RoomPanel");
            setActiveMenuButton(btnRoom, navigationButtons, resourceTabButtons);
        });
        btnSchedule.addActionListener(e -> {
            cardLayout.show(contentPanel, "SchedulePanel");
            setActiveMenuButton(btnSchedule, navigationButtons, resourceTabButtons);
        });
        btnTimetable.addActionListener(e -> {
            cardLayout.show(contentPanel, "TimetablePanel");
            setActiveMenuButton(btnTimetable, navigationButtons, resourceTabButtons);
        });
        btnAttendance.addActionListener(e -> {
            cardLayout.show(contentPanel, "AttendancePanel");
            setActiveMenuButton(btnAttendance, navigationButtons, resourceTabButtons);
        });
        btnEducation.addActionListener(e -> {
            cardLayout.show(contentPanel, "EducationPanel");
            setActiveMenuButton(btnEducation, navigationButtons, resourceTabButtons);
        });
        btnEnrollment.addActionListener(e -> {
            cardLayout.show(contentPanel, "EnrollmentPanel");
            setActiveMenuButton(btnEnrollment, navigationButtons, resourceTabButtons);
        });
        btnPlacementTest.addActionListener(e -> {
            cardLayout.show(contentPanel, "PlacementTestPanel");
            setActiveMenuButton(btnPlacementTest, navigationButtons, resourceTabButtons);
        });
        btnCertificate.addActionListener(e -> {
            cardLayout.show(contentPanel, "CertificatePanel");
            setActiveMenuButton(btnCertificate, navigationButtons, resourceTabButtons);
        });
        btnFinance.addActionListener(e -> {
            cardLayout.show(contentPanel, "FinancePanel");
            setActiveMenuButton(btnFinance, navigationButtons, resourceTabButtons);
        });

        JPanel managementButtonsPanel = new JPanel();
        managementButtonsPanel.setLayout(new BoxLayout(managementButtonsPanel, BoxLayout.Y_AXIS));
        managementButtonsPanel.setBackground(new Color(44, 62, 80));
        managementButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        managementButtonsPanel.setVisible(false);

        managementButtonsPanel.add(btnStudent);
        managementButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        managementButtonsPanel.add(btnTeacher);
        managementButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        managementButtonsPanel.add(btnStaff);
        managementButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        managementButtonsPanel.add(btnRoom);

        btnToggleManagement.addActionListener(e -> {
            boolean isExpanded = managementButtonsPanel.isVisible();
            managementButtonsPanel.setVisible(!isExpanded);
            btnToggleManagement.setText(!isExpanded ? "Quản Lý Nguồn Lực ▾" : "Quản Lý Nguồn Lực ▸");
            sidebarPanel.revalidate();
            sidebarPanel.repaint();
        });

        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(btnToggleManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebarPanel.add(managementButtonsPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(btnSchedule); // LỊCH HỌC
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnTimetable); // XEM TKB
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnAttendance); // ĐIỂM DANH LỚP
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnEducation);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnEnrollment);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnPlacementTest);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnCertificate);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnFinance);

        // --- LOGIC PHÂN QUYỀN HEADER ---
        // Nếu là GIÁO VIÊN (Teacher), ẩn các nút Không thuộc thẩm quyền
        if (currentUser != null && "Teacher".equals(currentUser.getRole())) {
            btnDashboard.setVisible(false);
            btnToggleManagement.setVisible(false);
            managementButtonsPanel.setVisible(false);
            btnStudent.setVisible(false);
            btnTeacher.setVisible(false);
            btnStaff.setVisible(false);
            btnRoom.setVisible(false);
            btnEducation.setVisible(false);
            btnEnrollment.setVisible(false);
            btnPlacementTest.setVisible(false);
            btnCertificate.setVisible(false);
            btnFinance.setVisible(false);
            // Teacher chỉ được xếp lịch và Xem TKB
            // Teacher chỉ được xếp lịch, Xem TKB và THỰC HIỆN ĐIỂM DANH
            // Nút btnAttendance KHÔNG BỊ setVisible(false) -> Giáo viên được quyền truy cập!
            
            // Mở mặc định Tab TKB cho Teacher lúc vào
            cardLayout.show(contentPanel, "TimetablePanel"); 
            setActiveMenuButton(btnTimetable, navigationButtons, resourceTabButtons);
        } else {
            // Mặc định cho Admin/Staff: mở Dashboard khi vào
            cardLayout.show(contentPanel, "DashboardPanel");
            setActiveMenuButton(btnDashboard, navigationButtons, resourceTabButtons);
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
        button.setBackground(DEFAULT_MENU_BG);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }

    /**
     * Tạo nút quản lý nguồn lực với styling đặc biệt
     */
    private JButton createResourceButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52)); // Cao hơn nút thường
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); // Font lớn hơn
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setForeground(Color.WHITE);
        button.setBackground(DEFAULT_MENU_BG);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 2, 0, new Color(255, 255, 255)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        return button;
    }

    /**
     * Tô nổi bật nhóm nút quản lý nguồn lực để dễ nhận diện.
     */
    private void applyResourceButtonStyle(JButton button) {
        button.setBackground(RESOURCE_MENU_BG);
        button.setForeground(Color.WHITE);
    }
    
    /**
     * Lấy màu gốc của nút nguồn lực dựa trên text
     */
    private Color getResourceButtonColor(JButton button) {
        String text = button.getText();
        if (text.contains("Học Viên")) return STUDENT_BTN_COLOR;
        if (text.contains("Giảng Viên")) return TEACHER_BTN_COLOR;
        if (text.contains("Nhân Sự")) return STAFF_BTN_COLOR;
        if (text.contains("Phòng Học")) return ROOM_BTN_COLOR;
        return RESOURCE_MENU_BG;
    }

    private void setActiveMenuButton(JButton activeButton, JButton[] navigationButtons, JButton[] resourceTabButtons) {
        for (JButton button : navigationButtons) {
            if (containsButton(resourceTabButtons, button)) {
                button.setBackground(getResourceButtonColor(button));
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(DEFAULT_MENU_BG);
                button.setForeground(Color.WHITE);
            }
        }

        activeButton.setBackground(ACTIVE_MENU_BG);
        activeButton.setForeground(new Color(44, 62, 80));
    }

    private boolean containsButton(JButton[] buttons, JButton target) {
        for (JButton button : buttons) {
            if (button == target) {
                return true;
            }
        }
        return false;
    }
}
