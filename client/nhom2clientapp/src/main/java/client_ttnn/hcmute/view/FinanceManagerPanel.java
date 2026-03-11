package client_ttnn.hcmute.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel gộp chung: Hóa đơn & Thanh toán (gắn với hóa đơn), Khuyến mãi.
 * Tab "Hóa đơn & Thanh toán": Danh sách hóa đơn (có cột Chi tiết → xem thanh toán, thêm thanh toán) + Tất cả thanh toán.
 * Logic: Hóa đơn là gốc; thanh toán gắn InvoiceID; trạng thái hóa đơn tự cập nhật theo tổng đã thu.
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

        JTabbedPane invoicePaymentTabs = new JTabbedPane(JTabbedPane.TOP);
        invoicePaymentTabs.addTab("Danh sách hóa đơn", new InvoiceManagerPanel());
        invoicePaymentTabs.addTab("Tất cả thanh toán", new PaymentManagerPanel());
        tabbedPane.addTab("Hóa đơn & Thanh toán", invoicePaymentTabs);
        tabbedPane.addTab("Khuyến mãi", new PromotionManagerPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
