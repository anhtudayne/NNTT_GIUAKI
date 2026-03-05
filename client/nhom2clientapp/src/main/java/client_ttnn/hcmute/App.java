package client_ttnn.hcmute;

import client_ttnn.hcmute.view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

public class App {
    public static void main(String[] args) {
        try {
            // Cài đặt FlatLaf
            FlatLightLaf.setup();
            
            // Tinh chỉnh font chữ mặc định cho toàn bộ ứng dụng
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đảm bảo tạo UI trong Thread an toàn của Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
