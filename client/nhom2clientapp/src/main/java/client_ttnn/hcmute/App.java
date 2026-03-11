package client_ttnn.hcmute;

import com.formdev.flatlaf.FlatLightLaf;

public class App {
    public static void main(String[] args) {
        try {
            // Cài đặt FlatLaf
            FlatLightLaf.setup();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tải trước dữ liệu tĩnh ngầm (Cache Pre-fetching)
        client_ttnn.hcmute.util.CacheManager.getInstance().prefetchDataAsync();

        // Đảm bảo tạo UI trong Thread an toàn của Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Tạm thời TẮT Đăng nhập, mở thẳng MainFrame với quyền Admin để test nhanh
            client_ttnn.hcmute.model.UserAccount mockAdmin = new client_ttnn.hcmute.model.UserAccount();
            mockAdmin.setRole("Admin");
            mockAdmin.setRelatedId(1);
            
            client_ttnn.hcmute.view.MainFrame mainFrame = new client_ttnn.hcmute.view.MainFrame(mockAdmin);
            mainFrame.setVisible(true);
            
            // client_ttnn.hcmute.view.LoginFrame loginFrame = new client_ttnn.hcmute.view.LoginFrame();
            // loginFrame.setVisible(true);
        });
    }
}
