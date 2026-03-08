package client_ttnn.hcmute.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel gộp chung: Hóa đơn, Thanh toán, Khuyến mãi.
 * Dùng JTabbedPane để chuyển giữa 3 màn hình trong cùng một tab sidebar.
 * Logic nghiệp vụ: Khuyến mãi (cấu hình) → Hóa đơn (lập, áp dụng KM) → Thanh toán (ghi nhận).
 * UX: giảm clutter menu, mọi thao tác tài chính ở một nơi.
 */
public class FinanceManagerPanel extends JPanel {

    public FinanceManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Tài chính: Hóa đơn, Thanh toán & Khuyến mãi");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        tabbedPane.addTab("Hóa đơn", new InvoiceManagerPanel());
        tabbedPane.addTab("Thanh toán", new PaymentManagerPanel());
        tabbedPane.addTab("Khuyến mãi", new PromotionManagerPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
