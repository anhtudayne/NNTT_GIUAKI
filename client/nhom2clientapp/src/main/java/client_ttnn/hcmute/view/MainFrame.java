package client_ttnn.hcmute.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    public MainFrame() {
        setTitle("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
        
        // Tạo thanh Menu / Tab để chuyển đổi các màn hình chức năng
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Thêm các Tab chức năng vào (Sau này có màn hình nào sẽ add thêm vào đây)
        tabbedPane.addTab("Quản Lý Học Viên", new ImageIcon(), new StudentManagerPanel(), "Trang quản lý, tìm kiếm học viên");
        
        // Mẫu để team thêm tiếp:
        // tabbedPane.addTab("Quản Lý Lớp Học", new ClassManagerPanel());
        // tabbedPane.addTab("Ghi Danh Học Viên", new EnrollmentPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}
