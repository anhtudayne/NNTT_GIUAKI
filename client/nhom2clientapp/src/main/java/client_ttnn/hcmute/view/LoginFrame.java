package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.UserAccount;
import client_ttnn.hcmute.service.AuthApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final AuthApiService authService;

    public LoginFrame() {
        authService = new AuthApiService();
        
        setTitle("Đăng Nhập Hệ Thống");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel hiển thị Logo và Tên Trung tâm
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel lblTitle = new JLabel("LANGUAGE CENTER");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Hệ Thống Quản Lý Đào Tạo");
        lblSubTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        lblSubTitle.setForeground(new Color(236, 240, 241));
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblSubTitle);

        // Body Panel chứa Form Đăng Nhập
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);

        // Label và Input Tài khoản
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblUser.setForeground(new Color(52, 73, 94));
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        txtUsername.setPreferredSize(new Dimension(300, 40));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Label và Input Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblPass.setForeground(new Color(52, 73, 94));
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Nút Đăng nhập
        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(46, 204, 113)); // Xanh Emerald
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setBorder(BorderFactory.createEmptyBorder());

        // Gắn sự kiện Click Login
        btnLogin.addActionListener(e -> processLogin());

        // Bố trí GridBag
        gbc.gridx = 0; gbc.gridy = 0;
        bodyPanel.add(lblUser, gbc);
        
        gbc.gridy = 1;
        bodyPanel.add(txtUsername, gbc);
        
        gbc.gridy = 2; gbc.insets = new Insets(20, 0, 5, 0);
        bodyPanel.add(lblPass, gbc);
        
        gbc.gridy = 3; gbc.insets = new Insets(10, 0, 5, 0);
        bodyPanel.add(txtPassword, gbc);
        
        gbc.gridy = 4; gbc.insets = new Insets(30, 0, 0, 0);
        bodyPanel.add(btnLogin, gbc);

        // Add to Frame
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
        
        // Bắt phím Enter
        getRootPane().setDefaultButton(btnLogin);
    }

    private void processLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tài khoản và Mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Đổi text nút thành Đang đăng nhập...
            btnLogin.setText("ĐANG XỬ LÝ...");
            btnLogin.setEnabled(false);
            
            // Gọi gọi API thực tế qua AuthService
            UserAccount account = authService.login(username, password);
            
            if (account != null) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công với quyền " + account.getRole(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                // Đóng Form Login
                this.dispose(); 
                
                // Mở Form MainFrame và truyền Role sang để Phân quyền
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame(account);
                    mainFrame.setVisible(true);
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnLogin.setText("ĐĂNG NHẬP");
            btnLogin.setEnabled(true);
        }
    }
}
