package client_ttnn.hcmute.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EducationManagerPanel extends JPanel {

    public EducationManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Quản lý Đào tạo: Khóa học & Lớp học");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        tabbedPane.addTab("Khóa học", new CourseManagerPanel());
        tabbedPane.addTab("Lớp học", new ClassManagerPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
