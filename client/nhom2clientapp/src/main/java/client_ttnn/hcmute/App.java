package client_ttnn.hcmute;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        try {
            // Cài đặt FlatLaf
            FlatLightLaf.setup();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đảm bảo tạo UI trong Thread an toàn của Swing
        SwingUtilities.invokeLater(() -> {
            client_ttnn.hcmute.view.LoginFrame loginFrame = new client_ttnn.hcmute.view.LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
