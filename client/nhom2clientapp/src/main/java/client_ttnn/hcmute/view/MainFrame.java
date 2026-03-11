package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.UserAccount;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {
    private static final Color SIDEBAR_BG = new Color(31, 45, 61);
    private static final Color SIDEBAR_BG_GRADIENT = new Color(24, 35, 50);
    private static final Color DEFAULT_MENU_BG = new Color(52, 73, 94);
    private static final Color RESOURCE_MENU_BG = new Color(26, 188, 156);
    private static final Color ACTIVE_MENU_BG = new Color(241, 196, 15);
    private static final Color MENU_TEXT_COLOR = new Color(236, 240, 241);
    private static final Color CHILD_MENU_TEXT_COLOR = new Color(214, 225, 236);
    private static final Color HEADER_CARD_BG = new Color(255, 255, 255, 26);

    // Màu tab con theo nhóm để giao diện sinh động hơn
    private static final Color DASHBOARD_BTN_COLOR = new Color(52, 152, 219);
    private static final Color FINANCE_BTN_COLOR = new Color(22, 160, 133);
    private static final Color SCHEDULE_BTN_COLOR = new Color(41, 128, 185);
    private static final Color TIMETABLE_BTN_COLOR = new Color(52, 73, 94);
    private static final Color ATTENDANCE_BTN_COLOR = new Color(230, 126, 34);
    private static final Color ENROLLMENT_BTN_COLOR = new Color(39, 174, 96);

    private static final Color COURSE_BTN_COLOR = new Color(155, 89, 182);
    private static final Color CLASS_BTN_COLOR = new Color(142, 68, 173);
    private static final Color EDUCATION_BTN_COLOR = new Color(142, 68, 173);
    private static final Color PLACEMENT_TEST_BTN_COLOR = new Color(127, 140, 141);
    private static final Color CERTIFICATE_BTN_COLOR = new Color(243, 156, 18);
    
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
        JPanel sidebarPanel = new GradientPanel(SIDEBAR_BG, SIDEBAR_BG_GRADIENT);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(280, 0)); // Tăng độ rộng lên 280
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JPanel brandCard = new JPanel(new BorderLayout(0, 4));
        brandCard.setOpaque(true);
        brandCard.setBackground(HEADER_CARD_BG);
        brandCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 45)),
            new EmptyBorder(12, 14, 12, 14)
        ));
        brandCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        JLabel lblLogo = new JLabel("LANGUAGE CENTER", SwingConstants.LEFT);
        lblLogo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        lblLogo.setForeground(Color.WHITE);
        JLabel lblRole = new JLabel(user != null ? "Role: " + user.getRole() : "Role: Guest", SwingConstants.LEFT);
        lblRole.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblRole.setForeground(new Color(220, 230, 241));
        brandCard.add(lblLogo, BorderLayout.CENTER);
        brandCard.add(lblRole, BorderLayout.SOUTH);
        sidebarPanel.add(brandCard);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 24))); // Khoảng cách

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
        JButton btnToggleOverviewRevenue = createMenuButton("Tổng quan & Doanh thu ▸");
        JButton btnToggleManagement = createMenuButton("Quản Lý Nguồn Lực ▸");
        JButton btnToggleAcademic = createMenuButton("Quản lý học vụ ▸");
        JButton btnSchedule = createMenuButton("Xếp Lịch Học"); // LỊCH HỌC
        JButton btnTimetable = createMenuButton("Xem Thời Khoá Biểu"); // XEM TKB
        JButton btnAttendance = createMenuButton("Điểm Danh Lớp"); // ĐIỂM DANH LỚP
        JButton btnEducation = createMenuButton("Khóa học & Lớp học"); // ĐÀO TẠO
        JButton btnEnrollment = createMenuButton("Quản Lý Ghi Danh");
        JButton btnToggleOther = createMenuButton("Khác ▸");
        JButton btnPlacementTest = createMenuButton("Quản Lý Placement Test");
        JButton btnCertificate = createMenuButton("Quản Lý Chứng Chỉ");
        JButton btnFinance = createMenuButton("Hóa đơn, Thanh toán & Khuyến mãi");

        styleSectionToggleButton(btnToggleOverviewRevenue, new Color(52, 152, 219));
        styleSectionToggleButton(btnToggleManagement, new Color(39, 174, 96));
        styleSectionToggleButton(btnToggleAcademic, new Color(155, 89, 182));
        styleSectionToggleButton(btnToggleOther, new Color(52, 73, 94));

        btnDashboard.setBackground(DASHBOARD_BTN_COLOR);
        btnFinance.setBackground(FINANCE_BTN_COLOR);
        btnSchedule.setBackground(SCHEDULE_BTN_COLOR);
        btnTimetable.setBackground(TIMETABLE_BTN_COLOR);
        btnAttendance.setBackground(ATTENDANCE_BTN_COLOR);
        btnEnrollment.setBackground(ENROLLMENT_BTN_COLOR);
        btnEducation.setBackground(EDUCATION_BTN_COLOR);
        btnPlacementTest.setBackground(PLACEMENT_TEST_BTN_COLOR);
        btnCertificate.setBackground(CERTIFICATE_BTN_COLOR);
        
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

        JPanel overviewRevenuePanel = new JPanel();
        overviewRevenuePanel.setLayout(new BoxLayout(overviewRevenuePanel, BoxLayout.Y_AXIS));
        overviewRevenuePanel.setOpaque(false);
        overviewRevenuePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overviewRevenuePanel.setBorder(new EmptyBorder(4, 14, 4, 0));
        overviewRevenuePanel.setVisible(false);

        overviewRevenuePanel.add(btnDashboard);
        overviewRevenuePanel.add(Box.createRigidArea(new Dimension(0, 12)));
        overviewRevenuePanel.add(btnFinance);

        btnToggleOverviewRevenue.addActionListener(e -> {
            boolean isExpanded = overviewRevenuePanel.isVisible();
            overviewRevenuePanel.setVisible(!isExpanded);
            btnToggleOverviewRevenue.setText(!isExpanded ? "Tổng quan & Doanh thu ▾" : "Tổng quan & Doanh thu ▸");
            sidebarPanel.revalidate();
            sidebarPanel.repaint();
        });

        JPanel managementButtonsPanel = new JPanel();
        managementButtonsPanel.setLayout(new BoxLayout(managementButtonsPanel, BoxLayout.Y_AXIS));
        managementButtonsPanel.setOpaque(false);
        managementButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        managementButtonsPanel.setBorder(new EmptyBorder(4, 14, 4, 0));
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

        JPanel academicButtonsPanel = new JPanel();
        academicButtonsPanel.setLayout(new BoxLayout(academicButtonsPanel, BoxLayout.Y_AXIS));
        academicButtonsPanel.setOpaque(false);
        academicButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        academicButtonsPanel.setBorder(new EmptyBorder(4, 14, 4, 0));
        academicButtonsPanel.setVisible(false);

        academicButtonsPanel.add(btnSchedule);
        academicButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        academicButtonsPanel.add(btnTimetable);
        academicButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        academicButtonsPanel.add(btnAttendance);
        academicButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        academicButtonsPanel.add(btnEnrollment);
        academicButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        academicButtonsPanel.add(btnEducation);

        btnToggleAcademic.addActionListener(e -> {
            boolean isExpanded = academicButtonsPanel.isVisible();
            academicButtonsPanel.setVisible(!isExpanded);
            btnToggleAcademic.setText(!isExpanded ? "Quản lý học vụ ▾" : "Quản lý học vụ ▸");
            sidebarPanel.revalidate();
            sidebarPanel.repaint();
        });

        JPanel otherButtonsPanel = new JPanel();
        otherButtonsPanel.setLayout(new BoxLayout(otherButtonsPanel, BoxLayout.Y_AXIS));
        otherButtonsPanel.setOpaque(false);
        otherButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherButtonsPanel.setBorder(new EmptyBorder(4, 14, 4, 0));
        otherButtonsPanel.setVisible(false);

        otherButtonsPanel.add(btnPlacementTest);
        otherButtonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        otherButtonsPanel.add(btnCertificate);

        btnToggleOther.addActionListener(e -> {
            boolean isExpanded = otherButtonsPanel.isVisible();
            otherButtonsPanel.setVisible(!isExpanded);
            btnToggleOther.setText(!isExpanded ? "Khác ▾" : "Khác ▸");
            sidebarPanel.revalidate();
            sidebarPanel.repaint();
        });

        sidebarPanel.add(btnToggleOverviewRevenue);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebarPanel.add(overviewRevenuePanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(btnToggleManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebarPanel.add(managementButtonsPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(btnToggleAcademic);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebarPanel.add(academicButtonsPanel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(btnToggleOther);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebarPanel.add(otherButtonsPanel);

        // --- LOGIC PHÂN QUYỀN HEADER ---
        // Nếu là GIÁO VIÊN (Teacher), ẩn các nút Không thuộc thẩm quyền
        if (currentUser != null && "Teacher".equals(currentUser.getRole())) {
            btnToggleOverviewRevenue.setVisible(false);
            overviewRevenuePanel.setVisible(false);
            btnDashboard.setVisible(false);
            btnToggleManagement.setVisible(false);
            managementButtonsPanel.setVisible(false);
            btnToggleAcademic.setVisible(true);
            academicButtonsPanel.setVisible(true);
            btnToggleAcademic.setText("Quản lý học vụ ▾");
            btnToggleOther.setVisible(false);
            otherButtonsPanel.setVisible(false);
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
            overviewRevenuePanel.setVisible(true);
            btnToggleOverviewRevenue.setText("Tổng quan & Doanh thu ▾");
            academicButtonsPanel.setVisible(false);
            btnToggleAcademic.setText("Quản lý học vụ ▸");
            otherButtonsPanel.setVisible(false);
            btnToggleOther.setText("Khác ▸");
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
        JButton button = new SidebarButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Chiều rộng kéo dài hết mức
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setForeground(CHILD_MENU_TEXT_COLOR);
        button.setBackground(DEFAULT_MENU_BG);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(255, 255, 255, 55)),
            BorderFactory.createEmptyBorder(9, 18, 9, 12)
        ));
        button.setMargin(new Insets(0, 0, 0, 0));
        
        return button;
    }

    /**
     * Tạo nút quản lý nguồn lực với styling đặc biệt
     */
    private JButton createResourceButton(String text) {
        JButton button = new SidebarButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setForeground(CHILD_MENU_TEXT_COLOR);
        button.setBackground(DEFAULT_MENU_BG);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(255, 255, 255, 70)),
            BorderFactory.createEmptyBorder(9, 20, 9, 12)
        ));
        button.setMargin(new Insets(0, 0, 0, 0));
        
        return button;
    }

    private void styleSectionToggleButton(JButton button, Color background) {
        button.setBackground(background);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(0, 0, 0, 0)),
            BorderFactory.createEmptyBorder(13, 16, 13, 12)
        ));
        button.setMargin(new Insets(0, 0, 0, 0));
    }

    private static class GradientPanel extends JPanel {
        private final Color topColor;
        private final Color bottomColor;

        private GradientPanel(Color topColor, Color bottomColor) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class SidebarButton extends JButton {
        private boolean hovered;

        private SidebarButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = getBackground();
            ButtonModel model = getModel();
            if (model.isPressed()) {
                bg = adjustBrightness(bg, -24);
            } else if (hovered && isEnabled()) {
                bg = adjustBrightness(bg, 18);
            }

            g2.setColor(bg);
            Shape shape = new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
            g2.fill(shape);

            g2.setColor(new Color(255, 255, 255, 36));
            g2.draw(shape);

            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillRoundRect(6, 5, Math.max(0, getWidth() - 12), Math.max(0, (getHeight() / 2) - 4), 10, 10);
            g2.dispose();

            super.paintComponent(g);
        }

        private static Color adjustBrightness(Color c, int delta) {
            int r = Math.max(0, Math.min(255, c.getRed() + delta));
            int g = Math.max(0, Math.min(255, c.getGreen() + delta));
            int b = Math.max(0, Math.min(255, c.getBlue() + delta));
            return new Color(r, g, b);
        }
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

    private Color getNavigationButtonColor(JButton button, JButton[] resourceTabButtons) {
        if (containsButton(resourceTabButtons, button)) {
            return getResourceButtonColor(button);
        }

        String text = button.getText();
        if (text.contains("Dashboard")) return DASHBOARD_BTN_COLOR;
        if (text.contains("Hóa đơn")) return FINANCE_BTN_COLOR;
        if (text.contains("Xếp Lịch")) return SCHEDULE_BTN_COLOR;
        if (text.contains("Thời Khoá Biểu")) return TIMETABLE_BTN_COLOR;
        if (text.contains("Điểm Danh")) return ATTENDANCE_BTN_COLOR;
        if (text.contains("Ghi Danh")) return ENROLLMENT_BTN_COLOR;
        if (text.contains("Khóa Học")) return COURSE_BTN_COLOR;
        if (text.contains("Lớp Học")) return CLASS_BTN_COLOR;
        if (text.contains("Khóa học & Lớp học")) return EDUCATION_BTN_COLOR;
        if (text.contains("Placement Test")) return PLACEMENT_TEST_BTN_COLOR;
        if (text.contains("Chứng Chỉ")) return CERTIFICATE_BTN_COLOR;
        return DEFAULT_MENU_BG;
    }

    private void setActiveMenuButton(JButton activeButton, JButton[] navigationButtons, JButton[] resourceTabButtons) {
        for (JButton button : navigationButtons) {
            button.setBackground(getNavigationButtonColor(button, resourceTabButtons));
            button.setForeground(Color.WHITE);
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
