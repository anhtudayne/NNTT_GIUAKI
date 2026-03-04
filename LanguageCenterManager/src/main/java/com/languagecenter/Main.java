package com.languagecenter;

import com.languagecenter.util.JPAUtil;
import com.languagecenter.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Language Center Manager...");

        // Chuẩn của Swing: đảm bảo Component khởi tạo trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });

        // Đăng ký một sự kiện khi ứng dụng tắt thì giải phóng kết nối DB
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down... Closing EntityManagerFactory.");
            JPAUtil.shutdown();
        }));
    }
}