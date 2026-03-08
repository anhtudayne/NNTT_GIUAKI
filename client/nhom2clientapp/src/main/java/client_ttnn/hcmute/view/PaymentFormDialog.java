package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Payment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PaymentApiService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

public class PaymentFormDialog extends JDialog {
    private final PaymentApiService apiService;
    private final boolean isEditMode;
    private final Payment initial;
    private final Runnable onSuccess;

    private JTextField txtStudentId, txtEnrollmentId, txtAmount, txtPaymentDate;
    private JComboBox<String> cmbMethod, cmbStatus;

    public PaymentFormDialog(Window owner, PaymentApiService apiService,
                             boolean isEditMode, Payment initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật thanh toán" : "Thêm thanh toán mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initial = initial;
        this.onSuccess = onSuccess;
        setSize(820, 620);
        setMinimumSize(new Dimension(560, 480));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initial != null) fillForm(initial);
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setBackground(Color.WHITE);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.insets = new Insets(8, 10, 8, 10);
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.anchor = GridBagConstraints.WEST;
        gbcField.weightx = 1.0;

        int cols = 42;
        txtStudentId = new JTextField(cols);
        txtEnrollmentId = new JTextField(cols);
        txtAmount = new JTextField(cols);
        txtPaymentDate = new JTextField(cols);
        txtPaymentDate.setToolTipText("yyyy-MM-dd");
        cmbMethod = new JComboBox<>(new String[]{"Cash", "BankTransfer", "Momo", "Card"});
        cmbStatus = new JComboBox<>(new String[]{"Success", "Failed", "Refunded"});
        cmbMethod.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbMethod.getPreferredSize().height));
        cmbStatus.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Student ID:", txtStudentId);
        addRow(formPanel, gbc, gbcField, row++, "Enrollment ID (tùy chọn):", txtEnrollmentId);
        addRow(formPanel, gbc, gbcField, row++, "Số tiền:", txtAmount);
        addRow(formPanel, gbc, gbcField, row++, "Ngày thanh toán (yyyy-MM-dd):", txtPaymentDate);
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phương thức:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbMethod, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(Color.WHITE);
        Dimension refBtn = new JButton("Tìm kiếm").getPreferredSize();
        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(refBtn);
        btnSave.setMinimumSize(refBtn);
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(refBtn);
        btnCancel.setMinimumSize(refBtn);
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        content.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, GridBagConstraints gbcField, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        p.add(field, gbcField);
    }

    private void fillForm(Payment p) {
        if (p == null) return;
        if (p.getStudent() != null && p.getStudent().getId() != null) txtStudentId.setText(String.valueOf(p.getStudent().getId()));
        if (p.getEnrollment() != null && p.getEnrollment().getEnrollmentId() != null) txtEnrollmentId.setText(String.valueOf(p.getEnrollment().getEnrollmentId()));
        if (p.getAmount() != null) txtAmount.setText(p.getAmount().toPlainString());
        txtPaymentDate.setText(str(p.getPaymentDate()));
        if (p.getPaymentMethod() != null) cmbMethod.setSelectedItem(p.getPaymentMethod());
        if (p.getStatus() != null) cmbStatus.setSelectedItem(p.getStatus());
    }

    private Payment getFromForm() {
        Payment p = new Payment();
        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        p.setStudent(s);
        if (!txtEnrollmentId.getText().trim().isEmpty()) {
            Enrollment e = new Enrollment();
            e.setEnrollmentId(Long.parseLong(txtEnrollmentId.getText().trim()));
            p.setEnrollment(e);
        }
        String amountText = txtAmount.getText().trim().replace(",", "");
        p.setAmount(new BigDecimal(amountText));
        p.setPaymentDate(txtPaymentDate.getText().trim());
        p.setPaymentMethod((String) cmbMethod.getSelectedItem());
        p.setStatus((String) cmbStatus.getSelectedItem());
        return p;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtAmount.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Số tiền.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtPaymentDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày thanh toán.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtStudentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Student ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (!txtEnrollmentId.getText().trim().isEmpty()) {
            try { Long.parseLong(txtEnrollmentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enrollment ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        }
        try { new BigDecimal(txtAmount.getText().trim().replace(",", "")); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            Payment p = getFromForm();
            if (isEditMode && initial != null) {
                apiService.updatePayment(initial.getPaymentId(), p);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Payment created = apiService.createPayment(p);
                JOptionPane.showMessageDialog(this, "Thêm thanh toán thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if ("Momo".equals(p.getPaymentMethod()) && created != null) {
                    showMomoQrDialog(created.getAmount(), created.getPaymentId());
                }
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMomoQrDialog(BigDecimal amount, Long paymentId) {
        String amountStr = amount != null ? String.format("%,d", amount.longValue()) : "0";
        String qrContent = String.format("MOMO|%s|Thanh toán học phí|ID:%s",
                amount != null ? amount.longValue() : 0,
                paymentId != null ? paymentId : "");
        BufferedImage qrImage = createQrImage(qrContent, 260, 260);
        if (qrImage == null) return;

        Window owner = getOwner() != null ? getOwner() : (Window) this;
        JDialog qrDialog = new JDialog(owner, "Quét mã MoMo", ModalityType.MODELESS);
        qrDialog.setLocationRelativeTo(owner);
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        panel.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("Quét mã QR để thanh toán bằng MoMo");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(lblTitle, BorderLayout.NORTH);
        JLabel lblQr = new JLabel(new ImageIcon(qrImage));
        panel.add(lblQr, BorderLayout.CENTER);
        JLabel lblAmount = new JLabel("Số tiền: " + amountStr + " VND");
        lblAmount.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        lblAmount.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblAmount, BorderLayout.SOUTH);
        qrDialog.setContentPane(panel);
        qrDialog.pack();
        qrDialog.setLocationRelativeTo(this);
        qrDialog.setVisible(true);
    }

    private static BufferedImage createQrImage(String content, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (Exception e) {
            return null;
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
