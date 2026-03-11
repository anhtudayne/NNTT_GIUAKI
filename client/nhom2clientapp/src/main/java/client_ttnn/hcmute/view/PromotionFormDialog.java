package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.service.PromotionApiService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigDecimal;
import client_ttnn.hcmute.util.ButtonStyles;

public class PromotionFormDialog extends JDialog {

    private final PromotionApiService apiService;
    private final boolean isEditMode;
    private final Promotion initialPromotion;
    private final Runnable onSuccess;

    private JTextField txtPromoCode;
    private JTextField txtDiscountPercent;
    private JDateChooser dcStartDate;
    private JDateChooser dcEndDate;
    private JTextArea txtDescription;

    public PromotionFormDialog(Window owner, PromotionApiService apiService,
                               boolean isEditMode, Promotion initialPromotion, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật khuyến mãi" : "Thêm khuyến mãi mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialPromotion = initialPromotion;
        this.onSuccess = onSuccess;

        setSize(900, 720);
        setMinimumSize(new Dimension(640, 560));
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
        dcStartDate = new JDateChooser();
        dcStartDate.setDateFormatString("yyyy-MM-dd");
        dcEndDate = new JDateChooser();
        dcEndDate.setDateFormatString("yyyy-MM-dd");
        txtDescription = new JTextArea(9, fieldCols);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setPreferredSize(new Dimension(520, 190));

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
        formPanel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(dcStartDate, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(dcEndDate, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        gbcField.fill = GridBagConstraints.BOTH;
        gbcField.weighty = 1.0;
        formPanel.add(descriptionScroll, gbcField);
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.weighty = 0;

        content.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(Color.WHITE);
        Dimension refBtnSize = new JButton("Tìm kiếm").getPreferredSize();

        JButton btnSave = ButtonStyles.createPrimaryButton("Lưu");
        btnSave.setPreferredSize(refBtnSize);
        btnSave.setMinimumSize(refBtnSize);
        JButton btnCancel = ButtonStyles.createNeutralButton("Hủy");
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
        dcStartDate.setDate(parseDate(p.getStartDate()));
        dcEndDate.setDate(parseDate(p.getEndDate()));
        txtDescription.setText(p.getDescription() != null ? p.getDescription() : "");
    }

    private Promotion getPromotionFromForm() {
        Promotion p = new Promotion();
        p.setPromoCode(txtPromoCode.getText().trim());
        p.setDiscountPercent(new BigDecimal(txtDiscountPercent.getText().trim()));
        p.setStartDate(formatDate(dcStartDate.getDate()));
        p.setEndDate(formatDate(dcEndDate.getDate()));
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
        if (dcStartDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (dcEndDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày kết thúc.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private static String formatDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    private static Date parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}
