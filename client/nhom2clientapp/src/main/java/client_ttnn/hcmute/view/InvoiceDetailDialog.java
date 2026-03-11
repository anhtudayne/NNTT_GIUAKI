package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Payment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PaymentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.ButtonStyles;

/**
 * Dialog chi tiết hóa đơn: thông tin hóa đơn + bảng thanh toán + nút "Thêm thanh toán".
 */
public class InvoiceDetailDialog extends JDialog {
    private final InvoiceApiService invoiceApiService;
    private final PaymentApiService paymentApiService;
    private final Long invoiceId;
    private final Runnable onInvoiceUpdated;

    private JLabel lblInvoiceId, lblStudent, lblTotalAmount, lblIssueDate, lblStatus;
    private DefaultTableModel paymentsTableModel;
    private JTable tblPayments;

    public InvoiceDetailDialog(Window owner, Long invoiceId,
                              InvoiceApiService invoiceApiService, PaymentApiService paymentApiService,
                              Runnable onInvoiceUpdated) {
        super(owner, "Chi tiết hóa đơn & thanh toán", ModalityType.APPLICATION_MODAL);
        this.invoiceId = invoiceId;
        this.invoiceApiService = invoiceApiService;
        this.paymentApiService = paymentApiService;
        this.onInvoiceUpdated = onInvoiceUpdated;
        setSize(700, 520);
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(new EmptyBorder(16, 20, 16, 20));
        content.setBackground(Color.WHITE);

        // --- Thông tin hóa đơn ---
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 6));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Thông tin hóa đơn"),
                new EmptyBorder(8, 10, 10, 10)));

        infoPanel.add(new JLabel("Mã hóa đơn:"));
        lblInvoiceId = new JLabel("--");
        infoPanel.add(lblInvoiceId);
        infoPanel.add(new JLabel("Học viên:"));
        lblStudent = new JLabel("--");
        infoPanel.add(lblStudent);
        infoPanel.add(new JLabel("Tổng tiền:"));
        lblTotalAmount = new JLabel("--");
        infoPanel.add(lblTotalAmount);
        infoPanel.add(new JLabel("Ngày xuất:"));
        lblIssueDate = new JLabel("--");
        infoPanel.add(lblIssueDate);
        infoPanel.add(new JLabel("Trạng thái thanh toán:"));
        lblStatus = new JLabel("--");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));
        infoPanel.add(lblStatus);

        content.add(infoPanel, BorderLayout.NORTH);

        // --- Bảng thanh toán ---
        String[] columns = {"ID", "Số tiền", "Ngày", "Phương thức", "Trạng thái"};
        paymentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPayments = new JTable(paymentsTableModel);
        tblPayments.setRowHeight(28);
        tblPayments.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tblPayments);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Các khoản thanh toán"),
                new EmptyBorder(4, 8, 8, 8)));

        JPanel centerWrap = new JPanel(new BorderLayout(0, 8));
        centerWrap.setBackground(Color.WHITE);
        centerWrap.add(scroll, BorderLayout.CENTER);

        JButton btnAddPayment = ButtonStyles.createPrimaryButton("➕ Thêm thanh toán");
        btnAddPayment.addActionListener(e -> openAddPayment());
        centerWrap.add(btnAddPayment, BorderLayout.SOUTH);

        content.add(centerWrap, BorderLayout.CENTER);

        JButton btnClose = ButtonStyles.createNeutralButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(Color.WHITE);
        southPanel.add(btnClose);
        content.add(southPanel, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                Invoice inv = invoiceApiService.getInvoiceById(invoiceId);
                if (inv != null) {
                    lblInvoiceId.setText(String.valueOf(inv.getInvoiceId()));
                    Student s = inv.getStudent();
                    lblStudent.setText(s != null ? (s.getFullName() != null ? s.getFullName() : "ID " + s.getId()) : "--");
                    lblTotalAmount.setText(inv.getTotalAmount() != null ? String.format("%,.0f VNĐ", inv.getTotalAmount()) : "--");
                    lblIssueDate.setText(str(inv.getIssueDate()));
                    lblStatus.setText(str(inv.getStatus()));
                }
                List<Payment> payments = invoiceApiService.getPaymentsByInvoiceId(invoiceId);
                updatePaymentsTable(payments);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updatePaymentsTable(List<Payment> list) {
        paymentsTableModel.setRowCount(0);
        if (list != null) {
            for (Payment p : list) {
                paymentsTableModel.addRow(new Object[]{
                        p.getPaymentId(),
                        p.getAmount() != null ? String.format("%,.0f", p.getAmount()) : "",
                        str(p.getPaymentDate()),
                        str(p.getPaymentMethod()),
                        str(p.getStatus())
                });
            }
        }
    }

    private void openAddPayment() {
        try {
            Invoice inv = invoiceApiService.getInvoiceById(invoiceId);
            Long studentId = inv != null && inv.getStudent() != null ? inv.getStudent().getId() : null;
            PaymentFormDialog dlg = new PaymentFormDialog(
                    getOwner(), paymentApiService,
                    false, null, this::loadData,
                    invoiceId, studentId);
            dlg.setVisible(true);
            if (onInvoiceUpdated != null) onInvoiceUpdated.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
