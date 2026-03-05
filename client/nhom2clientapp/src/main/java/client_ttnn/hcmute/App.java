package client_ttnn.hcmute;

import client_ttnn.hcmute.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        // Có thể áp dụng LookAndFeel ở đây (nếu dùng thư viện FlatLaf sau này)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
