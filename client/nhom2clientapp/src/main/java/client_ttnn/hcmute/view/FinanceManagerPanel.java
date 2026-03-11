package client_ttnn.hcmute.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Panel gộp chung: Hóa đơn & Thanh toán (gắn với hóa đơn), Khuyến mãi.
 * Tab "Hóa đơn & Thanh toán": Danh sách hóa đơn (có cột Chi tiết → xem thanh toán, thêm thanh toán) + Tất cả thanh toán.
 * Logic: Hóa đơn là gốc; thanh toán gắn InvoiceID; trạng thái hóa đơn tự cập nhật theo tổng đã thu.
 */
public class FinanceManagerPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color HEADER_BG = new Color(25, 32, 72);
    private static final Color TEXT_ON_DARK = new Color(240, 245, 255);

    public FinanceManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel titleLabel = new JLabel("Tài chính");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Hóa đơn • Thanh toán • Khuyến mãi");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_ON_DARK);
        JPanel titleWrap = new JPanel();
        titleWrap.setOpaque(false);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(titleLabel);
        titleWrap.add(Box.createRigidArea(new Dimension(0, 4)));
        titleWrap.add(subtitle);
        header.add(titleWrap, BorderLayout.WEST);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(20, 25, 60)),
                header.getBorder()
        ));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        JTabbedPane invoicePaymentTabs = new JTabbedPane(JTabbedPane.TOP);
        invoicePaymentTabs.addTab("Danh sách hóa đơn", new InvoiceManagerPanel());
        invoicePaymentTabs.addTab("Tất cả thanh toán", new PaymentManagerPanel());
        tabbedPane.addTab("Hóa đơn & Thanh toán", invoicePaymentTabs);
        tabbedPane.addTab("Khuyến mãi", new PromotionManagerPanel());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(14, 16, 16, 16));
        wrap.add(tabbedPane, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);
    }
}
