package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.UserAccount;
import client_ttnn.hcmute.service.AuthApiService;
import client_ttnn.hcmute.util.ButtonStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class ModernLoginForm extends JFrame {
    private final AuthApiService authService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMessage;

    public ModernLoginForm() {
        this.authService = new AuthApiService();
        setTitle("Hệ thống Quản lý Trung tâm Ngoại ngữ - Đăng nhập");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        // Main container split into two halves
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(Color.WHITE);

        // --- LEFT PANEL (Illustration) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(240, 244, 255)); // Soft purple/blue tint

        // Try to load the image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL imgUrl = getClass().getResource("/images/login_illustration.png");
            if (imgUrl != null) {
                // Resize image to fit the left half roughly (450x550) so it fills the height
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image scaledImg = originalIcon.getImage().getScaledInstance(450, 550, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imageLabel.setText("[Illustration Missing]");
            }
        } catch (Exception e) {
            imageLabel.setText("[Illustration Error]");
        }
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        // --- RIGHT PANEL (Login Form) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Branding
        JLabel brandLabel = new JLabel("Lumina Language Center");
        brandLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        brandLabel.setForeground(new Color(41, 128, 185)); // Brand Color
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subBrandLabel = new JLabel("Khơi nguồn tri thức");
        subBrandLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subBrandLabel.setForeground(new Color(127, 140, 141));
        subBrandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subBrandLabel.setBorder(new EmptyBorder(5, 0, 40, 0));

        // Username field
        JLabel userLabel = new JLabel("Tên đăng nhập");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        userLabel.setForeground(new Color(52, 73, 94));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setPreferredSize(new Dimension(300, 40));
        txtUsername.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(bdColor()), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Password field
        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        passLabel.setForeground(new Color(52, 73, 94));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(bdColor()), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Message Label
        lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        lblMessage.setForeground(Color.RED);
        lblMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMessage.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Login Button
        btnLogin = ButtonStyles.createPrimaryButton("Đăng Nhập");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Custom color for this specific brand (e.g., a nice blue)
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);

        btnLogin.addActionListener(e -> processLogin());

        // Allow pressing Enter to login
        KeyAdapter enterLogin = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterLogin);
        txtPassword.addKeyListener(enterLogin);

        rightPanel.add(brandLabel);
        rightPanel.add(subBrandLabel);
        rightPanel.add(userLabel);
        rightPanel.add(txtUsername);
        rightPanel.add(passLabel);
        rightPanel.add(txtPassword);
        rightPanel.add(lblMessage);
        rightPanel.add(btnLogin);
        rightPanel.add(Box.createVerticalGlue());

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
    }

    private int bdColor() {
        return 0xBDC3C7;
    }

    private void processLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
            return;
        }

        lblMessage.setText(" "); // clear
        btnLogin.setText("Đang đăng nhập...");
        btnLogin.setEnabled(false);

        // Run network call in background
        SwingWorker<UserAccount, Void> worker = new SwingWorker<>() {
            @Override
            protected UserAccount doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    UserAccount account = get();
                    if (account != null) {
                        MainFrame mainFrame = new MainFrame(account);
                        mainFrame.setVisible(true);
                        dispose(); // Close login
                    } else {
                        lblMessage.setText("Sai tài khoản hoặc mật khẩu.");
                    }
                } catch (Exception ex) {
                    lblMessage.setText("Lỗi kết nối Server.");
                } finally {
                    btnLogin.setText("Đăng Nhập");
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
