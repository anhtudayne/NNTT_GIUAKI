package client_ttnn.hcmute;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            // Cài đặt FlatLaf
            FlatLightLaf.setup();
            // Global UI polish (rounded corners, consistent feel)
            UIManager.put("Component.arc", 14);
            UIManager.put("Button.arc", 14);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("Button.margin", new java.awt.Insets(8, 14, 8, 14));
            FlatLaf.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tải trước dữ liệu tĩnh ngầm (Cache Pre-fetching)
        client_ttnn.hcmute.util.CacheManager.getInstance().prefetchDataAsync();

        // Đảm bảo tạo UI trong Thread an toàn của Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            client_ttnn.hcmute.view.ModernLoginForm loginForm = new client_ttnn.hcmute.view.ModernLoginForm();
            loginForm.setVisible(true);
        });
    }
}
