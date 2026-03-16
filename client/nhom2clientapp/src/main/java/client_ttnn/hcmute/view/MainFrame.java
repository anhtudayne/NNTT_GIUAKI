package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.UserAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Scrollable;

public class MainFrame extends JFrame {
    private static final Color APP_BG = new Color(0xE0E5EC);
    private static final Color SURFACE = new Color(0xE7ECF3);
    private static final Color SURFACE_ALT = new Color(0xEEF2F8);
    private static final Color TEXT = new Color(34, 41, 57);
    private static final Color TEXT_MUTED = new Color(92, 102, 120);
    private static final Color ACCENT = new Color(120, 150, 255);

    private final List<NavButton> navButtons = new ArrayList<>();
    private ClayPanel mainContentHost;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(UserAccount user) {
        setTitle("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ - " + (user != null ? user.getRole() : "Guest"));
        setSize(1440, 840);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(18, 18));
        // "Dock" sidebar to the left edge
        root.setBorder(new EmptyBorder(18, 0, 18, 18));
        root.setBackground(APP_BG);

        root.add(buildSidebar(user), BorderLayout.WEST);
        root.add(buildMainArea(user), BorderLayout.CENTER);

        setContentPane(root);
        setBackground(APP_BG);

        if (!navButtons.isEmpty()) {
            setActive(navButtons.getFirst());
        }
    }

    private JComponent buildSidebar(UserAccount user) {
        // BorderLayout for sidebar: NORTH=brand, CENTER=scrollable nav, SOUTH=hint.
        // BorderLayout.CENTER always fills the full remaining width — no BoxLayout alignment drift.
        ClayPanel sidebar = new ClayPanel(new BorderLayout(0, 14));
        sidebar.setFill(SURFACE);
        sidebar.setArc(36);
        sidebar.setShadowSize(16);
        sidebar.setElevation(5);
        sidebar.setInsetSize(12);
        sidebar.setReserveShadowInsets(false);
        sidebar.setBorder(new EmptyBorder(20, 18, 20, 18));
        // Wider sidebar so nav labels are easier to read
        sidebar.setPreferredSize(new Dimension(420, 0));

        // --- Brand block (NORTH) ---
        ClayPanel brand = new ClayPanel(new BorderLayout(0, 6));
        brand.setFill(SURFACE_ALT).setArc(24).setShadowSize(10).setElevation(4).setInsetSize(10);
        brand.setReserveShadowInsets(false);
        brand.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Language Center");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        String roleText = "Role: " + (user != null && user.getRole() != null ? user.getRole() : "Guest");
        JLabel subtitle = new JLabel(roleText);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        brand.add(title, BorderLayout.CENTER);
        brand.add(subtitle, BorderLayout.SOUTH);
        sidebar.add(brand, BorderLayout.NORTH);

        // --- Nav groups (CENTER) — full width guaranteed by BorderLayout ---
        FullWidthPanel navStack = new FullWidthPanel();
        navStack.setBorder(new EmptyBorder(4, 0, 4, 0));

        SectionGroup overview = new SectionGroup("Tổng quan & Doanh thu", true);
        overview.addItem(navItem("Dashboard", "DashboardPanel"));
        overview.addItem(gap(10));
        overview.addItem(navItem("Hóa đơn, Thanh toán & Khuyến mãi", "FinancePanel"));

        SectionGroup resources = new SectionGroup("Quản Lý Nguồn Lực", false);
        resources.addItem(navItem("Quản Lý Học Viên", "StudentPanel"));
        resources.addItem(gap(10));
        resources.addItem(navItem("Quản Lý Giảng Viên", "TeacherPanel"));
        resources.addItem(gap(10));
        resources.addItem(navItem("Quản Lý Nhân Sự", "StaffPanel"));
        resources.addItem(gap(10));
        resources.addItem(navItem("Quản Lý Phòng Học", "RoomPanel"));

        SectionGroup academic = new SectionGroup("Quản lý học vụ", false);
        academic.addItem(navItem("Xếp Lịch Học", "SchedulePanel"));
        academic.addItem(gap(10));
        academic.addItem(navItem("Xem Thời Khoá Biểu", "TimetablePanel"));
        academic.addItem(gap(10));
        academic.addItem(navItem("Điểm Danh Lớp", "AttendancePanel"));
        academic.addItem(gap(10));
        academic.addItem(navItem("Quản Lý Ghi Danh", "EnrollmentPanel"));
        academic.addItem(gap(10));
        academic.addItem(navItem("Khóa học & Lớp học", "EducationPanel"));

        SectionGroup other = new SectionGroup("Khác", false);
        other.addItem(navItem("Quản Lý Placement Test", "PlacementTestPanel"));
        other.addItem(gap(10));
        other.addItem(navItem("Quản Lý Chứng Chỉ", "CertificatePanel"));

        applyRoleVisibility(user, overview, resources, academic, other);

        navStack.add(overview);
        navStack.add(gap(16));
        navStack.add(resources);
        navStack.add(gap(16));
        navStack.add(academic);
        navStack.add(gap(16));
        navStack.add(other);

        JScrollPane navScroll = new JScrollPane(navStack);
        navScroll.setBorder(BorderFactory.createEmptyBorder());
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        navScroll.getVerticalScrollBar().setUnitIncrement(16);
        navScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebar.add(navScroll, BorderLayout.CENTER);

        return sidebar;
    }

    private JComponent buildMainArea(UserAccount user) {
        mainContentHost = new ClayPanel(new BorderLayout(18, 18));
        mainContentHost.setFill(SURFACE);
        mainContentHost.setArc(34);
        mainContentHost.setShadowSize(24);
        mainContentHost.setElevation(8);
        mainContentHost.setInsetSize(16);
        mainContentHost.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Removed Clay header (keep content maximized)
        mainContentHost.add(buildProjectCards(user), BorderLayout.CENTER);

        return mainContentHost;
    }

    private JComponent buildHeader(UserAccount user) {
        ClayPanel header = new ClayPanel(new GridBagLayout());
        header.setFill(SURFACE_ALT).setArc(28).setShadowSize(16).setElevation(6).setInsetSize(12);
        header.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headline = new JLabel("Clay Dashboard");
        headline.setForeground(TEXT);
        headline.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        JLabel desc = new JLabel("Pastel surfaces, big radius, inset + drop shadows (Graphics2D).");
        desc.setForeground(TEXT_MUTED);
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(headline);
        left.add(Box.createRigidArea(new Dimension(0, 6)));
        left.add(desc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        header.add(left, gbc);

        ClayPill pill = new ClayPill("Status: POC");
        pill.setMaximumSize(new Dimension(160, 34));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 12, 0, 0);
        header.add(pill, gbc);

        return header;
    }

    private JComponent buildProjectCards(UserAccount user) {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Real project panels
        contentPanel.add(new DashboardPanel(), "DashboardPanel");
        contentPanel.add(new StudentManagerPanel(), "StudentPanel");
        contentPanel.add(new TeacherManagerPanel(), "TeacherPanel");
        contentPanel.add(new StaffManagerPanel(), "StaffPanel");
        contentPanel.add(new RoomManagerPanel(), "RoomPanel");
        contentPanel.add(new ScheduleManagerPanel(), "SchedulePanel");
        contentPanel.add(new TimetableViewPanel(), "TimetablePanel");
        contentPanel.add(new AttendanceManagerPanel(user), "AttendancePanel");
        contentPanel.add(new EducationManagerPanel(), "EducationPanel");
        contentPanel.add(new EnrollmentManagerPanel(), "EnrollmentPanel");
        contentPanel.add(new PlacementTestManagerPanel(), "PlacementTestPanel");
        contentPanel.add(new CertificateManagerPanel(), "CertificatePanel");
        contentPanel.add(new FinanceManagerPanel(), "FinancePanel");

        ClayPanel host = new ClayPanel(new BorderLayout());
        host.setFill(SURFACE_ALT).setArc(30).setShadowSize(18).setElevation(6).setInsetSize(14);
        host.setBorder(new EmptyBorder(10, 10, 10, 10));
        host.add(contentPanel, BorderLayout.CENTER);

        return host;
    }

    private ClayPanel metricCard(String title, String value, String subtitle, Color badgeColor) {
        ClayPanel card = new ClayPanel(new BorderLayout(0, 10));
        card.setFill(SURFACE_ALT).setArc(30).setShadowSize(18).setElevation(6).setInsetSize(14);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setPreferredSize(new Dimension(260, 150));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT_MUTED);
        t.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        JLabel v = new JLabel(value);
        v.setForeground(TEXT);
        v.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));

        JLabel sub = new JLabel(subtitle);
        sub.setForeground(TEXT_MUTED);
        sub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(t, BorderLayout.WEST);

        ClayDot dot = new ClayDot(badgeColor);
        top.add(dot, BorderLayout.EAST);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.add(v);
        mid.add(Box.createRigidArea(new Dimension(0, 6)));
        mid.add(sub);

        card.add(top, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        return card;
    }

    private ClayPanel activityCard() {
        ClayPanel card = new ClayPanel(new BorderLayout(0, 12));
        card.setFill(SURFACE_ALT).setArc(32).setShadowSize(18).setElevation(6).setInsetSize(14);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel t = new JLabel("Recent activity");
        t.setForeground(TEXT);
        t.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.add(activityRow("New student enrolled", "2m ago"));
        list.add(Box.createRigidArea(new Dimension(0, 10)));
        list.add(activityRow("Schedule updated", "15m ago"));
        list.add(Box.createRigidArea(new Dimension(0, 10)));
        list.add(activityRow("Payment received", "1h ago"));
        list.add(Box.createVerticalGlue());

        card.add(t, BorderLayout.NORTH);
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JComponent activityRow(String label, String time) {
        ClayPanel row = new ClayPanel(new BorderLayout(12, 0));
        row.setFill(new Color(0xF3F6FB)).setArc(26).setShadowSize(14).setElevation(5).setInsetSize(12);
        row.setBorder(new EmptyBorder(12, 12, 12, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel l = new JLabel(label);
        l.setForeground(TEXT);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        JLabel r = new JLabel(time);
        r.setForeground(TEXT_MUTED);
        r.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        row.add(l, BorderLayout.WEST);
        row.add(r, BorderLayout.EAST);
        return row;
    }

    private JComponent navItem(String label, String cardKey) {
        // Plain JPanel wrapper (no ClayPanel shadow here — shadow is in NavButton's paint)
        JPanel tile = new JPanel(new BorderLayout());
        tile.setOpaque(false);
        tile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        NavButton btn = new NavButton(label);
        btn.setActionCommand(cardKey);
        btn.addActionListener(e -> {
            setActive(btn);
            if (cardLayout != null && contentPanel != null) {
                cardLayout.show(contentPanel, cardKey);
            }
        });
        navButtons.add(btn);

        tile.add(btn, BorderLayout.CENTER);
        return tile;
    }

    private void setActive(NavButton active) {
        for (NavButton b : navButtons) {
            b.setActive(b == active);
        }
        if (mainContentHost != null) {
            mainContentHost.repaint();
        }
    }

    private static class NavButton extends JButton {
        private boolean active;
        private boolean hovered;

        private NavButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            // LEFT alignment + fill full width of container
            setHorizontalAlignment(SwingConstants.LEFT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(TEXT);
            setFont(new Font("Segoe UI", Font.PLAIN, 20));
            // Left pad gives text breathing room from edge
            setBorder(new EmptyBorder(16, 22, 16, 12));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        private void setActive(boolean active) {
            this.active = active;
            setForeground(active ? new Color(30, 40, 100) : TEXT);
            setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 20));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (active || hovered) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Pill shape — fills full button width so it looks like a card
                    int arc = 18;
                    int px = 4, py = 3;
                    int pw = getWidth() - px * 2;
                    int ph = getHeight() - py * 2;

                    if (active) {
                        // Stronger accent fill + top highlight
                        g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80));
                        g2.fillRoundRect(px, py, pw, ph, arc, arc);
                        // Top-left highlight strip
                        g2.setColor(new Color(255, 255, 255, 90));
                        g2.fillRoundRect(px + 2, py + 2, pw - 4, ph / 2, arc, arc);
                        // Soft left accent bar
                        g2.setColor(ACCENT);
                        g2.fillRoundRect(px, py + 6, 3, ph - 12, 3, 3);
                    } else {
                        // Hover: slightly stronger fill
                        g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 26));
                        g2.fillRoundRect(px, py, pw, ph, arc, arc);
                        g2.setColor(new Color(255, 255, 255, 60));
                        g2.drawRoundRect(px, py, pw, ph, arc, arc);
                    }
                } finally {
                    g2.dispose();
                }
            }
            super.paintComponent(g);
        }
    }

    private static class ClayDot extends JComponent {
        private final Color color;

        private ClayDot(Color color) {
            this.color = color != null ? color : new Color(120, 150, 255);
            setPreferredSize(new Dimension(18, 18));
            setMinimumSize(new Dimension(18, 18));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight());
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;

                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillOval(x + 2, y + 2, s - 3, s - 3);

                g2.setColor(color);
                g2.fillOval(x, y, s - 3, s - 3);

                g2.setColor(new Color(255, 255, 255, 160));
                g2.fillOval(x + 2, y + 2, Math.max(1, s / 3), Math.max(1, s / 3));
            } finally {
                g2.dispose();
            }
        }
    }

    private static class ClayPill extends ClayPanel {
        private ClayPill(String text) {
            super(new BorderLayout());
            setFill(new Color(0xF3F6FB));
            setArc(999);
            setShadowSize(12);
            setElevation(4);
            setInsetSize(10);
            setBorder(new EmptyBorder(8, 12, 8, 12));

            JLabel l = new JLabel(text);
            l.setForeground(TEXT_MUTED);
            l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            add(l, BorderLayout.CENTER);
        }
    }

    private static Component gap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    private void applyRoleVisibility(UserAccount user, SectionGroup overview, SectionGroup resources, SectionGroup academic, SectionGroup other) {
        String role = user != null ? user.getRole() : null;
        boolean isTeacher = "Teacher".equals(role);

        if (isTeacher) {
            overview.setVisible(false);
            resources.setVisible(false);
            other.setVisible(false);
            academic.setExpanded(true);
        } else {
            overview.setExpanded(true);
        }
    }

    private static class SectionGroup extends JPanel {
        private final SectionToggle toggle;
        private final JPanel body;

        private SectionGroup(String title, boolean expanded) {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            toggle = new SectionToggle(title);
            toggle.setAlignmentX(Component.LEFT_ALIGNMENT);
            toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

            body = new JPanel();
            body.setOpaque(false);
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBorder(new EmptyBorder(6, 8, 0, 0));
            body.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            add(toggle);
            add(body);

            setExpanded(expanded);
            toggle.addActionListener(e -> setExpanded(!body.isVisible()));
        }

        private void addItem(Component c) {
            body.add(c);
        }

        private void setExpanded(boolean expanded) {
            body.setVisible(expanded);
            toggle.setExpanded(expanded);
            revalidate();
            repaint();
        }
    }

    private static class SectionToggle extends JButton {
        private boolean expanded;

        private SectionToggle(String title) {
            super(title);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setHorizontalTextPosition(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(TEXT_MUTED);
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setBorder(new EmptyBorder(6, 10, 6, 10));
        }

        private void setExpanded(boolean expanded) {
            this.expanded = expanded;
            setText(getText().replace(" ▾", "").replace(" ▸", "") + (expanded ? " ▾" : " ▸"));
        }
    }
}

/**
 * A JPanel that always reports its preferred width as the containing JViewport's width.
 * This is the canonical fix for "items right/center-aligned inside JScrollPane with BoxLayout Y_AXIS":
 * by returning true from getScrollableTracksViewportWidth(), Swing forces the panel to fill
 * the viewport width, so BoxLayout has no extra space to use for centering.
 */
class FullWidthPanel extends JPanel implements Scrollable {
    public FullWidthPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 60; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
    /** Crucial: force panel width = viewport width so BoxLayout Y_AXIS left-aligns all items. */
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
}
