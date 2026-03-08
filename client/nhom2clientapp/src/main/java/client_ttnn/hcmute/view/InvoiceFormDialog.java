package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InvoiceFormDialog extends JDialog {
    private final InvoiceApiService apiService;
    private final PromotionApiService promotionService;
    private final boolean isEditMode;
    private final Invoice initial;
    private final Runnable onSuccess;

    private JTextField txtStudentId, txtTotalAmount, txtAmountAfterDiscount, txtIssueDate;
    private JComboBox<String> cmbStatus;
    private JComboBox<Promotion> cmbPromotion;

    public InvoiceFormDialog(Window owner, InvoiceApiService apiService, PromotionApiService promotionService,
                             boolean isEditMode, Invoice initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật hóa đơn" : "Thêm hóa đơn mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.promotionService = promotionService;
        this.isEditMode = isEditMode;
        this.initial = initial;
        this.onSuccess = onSuccess;
        setSize(820, 660);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        loadPromotions();
        if (initial != null) fillForm(initial);
        recalcDiscountedAmount();
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
        txtTotalAmount = new JTextField(cols);
        txtAmountAfterDiscount = new JTextField(cols);
        txtAmountAfterDiscount.setEditable(false);
        txtIssueDate = new JTextField(cols);
        txtIssueDate.setToolTipText("yyyy-MM-dd");
        cmbStatus = new JComboBox<>(new String[]{"Unpaid", "Partial", "Paid"});
        cmbPromotion = new JComboBox<>();
        cmbPromotion.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbPromotion.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Student ID:", txtStudentId);
        addRow(formPanel, gbc, gbcField, row++, "Tổng tiền (trước KM):", txtTotalAmount);
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Khuyến mãi:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbPromotion, gbcField);
        row++;
        addRow(formPanel, gbc, gbcField, row++, "Sau khuyến mãi:", txtAmountAfterDiscount);
        addRow(formPanel, gbc, gbcField, row++, "Ngày xuất (yyyy-MM-dd):", txtIssueDate);
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        txtTotalAmount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { recalcDiscountedAmount(); }
            @Override
            public void removeUpdate(DocumentEvent e) { recalcDiscountedAmount(); }
            @Override
            public void changedUpdate(DocumentEvent e) { recalcDiscountedAmount(); }
        });
        cmbPromotion.addActionListener(e -> recalcDiscountedAmount());

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

    private void loadPromotions() {
        try {
            cmbPromotion.removeAllItems();
            List<Promotion> list = promotionService.getActivePromotions(null);
            for (Promotion pr : list) cmbPromotion.addItem(pr);
            cmbPromotion.insertItemAt(null, 0);
            cmbPromotion.setSelectedIndex(0);
        } catch (Exception e) {
            // ignore
        }
    }

    private void recalcDiscountedAmount() {
        String amountText = txtTotalAmount.getText().trim().replace(",", "");
        if (amountText.isEmpty()) {
            txtAmountAfterDiscount.setText("");
            return;
        }
        try {
            BigDecimal base = new BigDecimal(amountText);
            Promotion pr = (Promotion) cmbPromotion.getSelectedItem();
            BigDecimal finalAmount = base;
            if (pr != null && pr.getDiscountPercent() != null) {
                BigDecimal factor = BigDecimal.ONE.subtract(pr.getDiscountPercent().divide(BigDecimal.valueOf(100)));
                finalAmount = base.multiply(factor);
                if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;
            }
            txtAmountAfterDiscount.setText(String.format("%,.0f", finalAmount));
        } catch (Exception ex) {
            txtAmountAfterDiscount.setText("");
        }
    }

    private void fillForm(Invoice inv) {
        if (inv == null) return;
        if (inv.getStudent() != null && inv.getStudent().getId() != null) txtStudentId.setText(String.valueOf(inv.getStudent().getId()));
        if (inv.getTotalAmount() != null) txtTotalAmount.setText(inv.getTotalAmount().toPlainString());
        txtIssueDate.setText(str(inv.getIssueDate()));
        if (inv.getStatus() != null) cmbStatus.setSelectedItem(inv.getStatus());
        recalcDiscountedAmount();
    }

    private Invoice getFromForm() {
        Invoice inv = new Invoice();
        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        inv.setStudent(s);
        // Lưu số tiền sau khuyến mãi (đã hiển thị ở ô "Sau khuyến mãi")
        String amountText = txtAmountAfterDiscount.getText().trim().replace(",", "");
        if (amountText.isEmpty()) amountText = txtTotalAmount.getText().trim().replace(",", "");
        inv.setTotalAmount(new BigDecimal(amountText));
        inv.setIssueDate(txtIssueDate.getText().trim());
        inv.setStatus((String) cmbStatus.getSelectedItem());
        return inv;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtTotalAmount.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Tổng tiền.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtIssueDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày xuất.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtStudentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Student ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { new BigDecimal(txtTotalAmount.getText().trim().replace(",", "")); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Tổng tiền không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            Invoice inv = getFromForm();
            if (isEditMode && initial != null) {
                apiService.updateInvoice(initial.getInvoiceId(), inv);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createInvoice(inv);
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
