package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class PromotionFormDialog extends JDialog {

    private final PromotionApiService apiService;
    private final boolean isEditMode;
    private final Promotion initialPromotion;
    private final Runnable onSuccess;

    private JTextField txtPromoCode;
    private JTextField txtDiscountPercent;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextArea txtDescription;

    public PromotionFormDialog(Window owner, PromotionApiService apiService,
                               boolean isEditMode, Promotion initialPromotion, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật khuyến mãi" : "Thêm khuyến mãi mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialPromotion = initialPromotion;
        this.onSuccess = onSuccess;

        setSize(820, 680);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialPromotion != null) {
            fillForm(initialPromotion);
        }
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

        int fieldCols = 42;
        txtPromoCode = new JTextField(fieldCols);
        txtDiscountPercent = new JTextField(fieldCols);
        txtStartDate = new JTextField(fieldCols);
        txtStartDate.setToolTipText("yyyy-MM-dd");
        txtEndDate = new JTextField(fieldCols);
        txtEndDate.setToolTipText("yyyy-MM-dd");
        txtDescription = new JTextArea(4, fieldCols);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mã khuyến mãi:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtPromoCode, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Giảm giá (%):"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtDiscountPercent, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtStartDate, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtEndDate, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(new JScrollPane(txtDescription), gbcField);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(Color.WHITE);
        Dimension refBtnSize = new JButton("Tìm kiếm").getPreferredSize();

        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(refBtnSize);
        btnSave.setMinimumSize(refBtnSize);
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(refBtnSize);
        btnCancel.setMinimumSize(refBtnSize);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);
    }

    private void fillForm(Promotion p) {
        if (p == null) return;
        txtPromoCode.setText(p.getPromoCode() != null ? p.getPromoCode() : "");
        txtDiscountPercent.setText(p.getDiscountPercent() != null ? p.getDiscountPercent().toPlainString() : "");
        txtStartDate.setText(p.getStartDate() != null ? p.getStartDate() : "");
        txtEndDate.setText(p.getEndDate() != null ? p.getEndDate() : "");
        txtDescription.setText(p.getDescription() != null ? p.getDescription() : "");
    }

    private Promotion getPromotionFromForm() {
        Promotion p = new Promotion();
        p.setPromoCode(txtPromoCode.getText().trim());
        p.setDiscountPercent(new BigDecimal(txtDiscountPercent.getText().trim()));
        p.setStartDate(txtStartDate.getText().trim());
        p.setEndDate(txtEndDate.getText().trim());
        p.setDescription(txtDescription.getText().trim());
        return p;
    }

    private boolean validateForm() {
        if (txtPromoCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khuyến mãi.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(txtDiscountPercent.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Phần trăm giảm giá phải là số hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        Promotion promotion = getPromotionFromForm();
        try {
            if (isEditMode && initialPromotion != null) {
                apiService.updatePromotion(initialPromotion.getPromotionId(), promotion);
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createPromotion(promotion);
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
