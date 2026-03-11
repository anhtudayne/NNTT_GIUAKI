package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PromotionApiService;
import client_ttnn.hcmute.service.StudentApiService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceFormDialog extends JDialog {
    private final InvoiceApiService apiService;
    private final PromotionApiService promotionService;
    private final StudentApiService studentService;
    private final boolean isEditMode;
    private final Invoice initial;
    private final Runnable onSuccess;

    private JComboBox<Student> cmbStudent;
    private JTextField txtTotalAmount, txtAmountAfterDiscount;
    private JDateChooser dcIssueDate;
    private JComboBox<String> cmbStatus;
    private JComboBox<Promotion> cmbPromotion;
    private List<Student> allStudents = new ArrayList<>();

    public InvoiceFormDialog(Window owner, InvoiceApiService apiService, PromotionApiService promotionService,
                             boolean isEditMode, Invoice initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật hóa đơn" : "Thêm hóa đơn mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.promotionService = promotionService;
        this.studentService = new StudentApiService();
        this.isEditMode = isEditMode;
        this.initial = initial;
        this.onSuccess = onSuccess;
        setSize(820, 660);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        loadStudents();
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
        cmbStudent = new JComboBox<>();
        cmbStudent.setEditable(true);
        cmbStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student s) {
                    String id = s.getId() != null ? String.valueOf(s.getId()) : "--";
                    String name = s.getFullName() != null ? s.getFullName() : "";
                    setText(id + " - " + name);
                }
                return this;
            }
        });
        txtTotalAmount = new JTextField(cols);
        txtAmountAfterDiscount = new JTextField(cols);
        txtAmountAfterDiscount.setEditable(false);
        dcIssueDate = new JDateChooser();
        dcIssueDate.setDateFormatString("yyyy-MM-dd");
        cmbStatus = new JComboBox<>(new String[]{"Unpaid", "Partial", "Paid"});
        cmbPromotion = new JComboBox<>();
        cmbPromotion.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbPromotion.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Học viên:", cmbStudent);
        addRow(formPanel, gbc, gbcField, row++, "Tổng tiền (ban đầu):", txtTotalAmount);
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Khuyến mãi:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbPromotion, gbcField);
        row++;
        addRow(formPanel, gbc, gbcField, row++, "Còn phải thu (sau KM):", txtAmountAfterDiscount);
        addRow(formPanel, gbc, gbcField, row++, "Ngày xuất:", dcIssueDate);
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
        hookStudentFilter();

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

    private void loadStudents() {
        try {
            allStudents = studentService.getAllStudents();
            refreshStudentCombo(allStudents);
        } catch (Exception e) {
            // ignore
        }
    }

    private void refreshStudentCombo(List<Student> students) {
        Student selected = (Student) cmbStudent.getSelectedItem();
        DefaultComboBoxModel<Student> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        if (students != null) {
            for (Student s : students) model.addElement(s);
        }
        cmbStudent.setModel(model);
        if (selected != null) cmbStudent.setSelectedItem(selected);
    }

    private void hookStudentFilter() {
        Component editorComp = cmbStudent.getEditor().getEditorComponent();
        if (!(editorComp instanceof JTextField editor)) return;
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String q = editor.getText() != null ? editor.getText().trim().toLowerCase() : "";
                if (q.isEmpty()) {
                    refreshStudentCombo(allStudents);
                    cmbStudent.hidePopup();
                    return;
                }
                List<Student> filtered = new ArrayList<>();
                for (Student s : allStudents) {
                    String id = s.getId() != null ? String.valueOf(s.getId()) : "";
                    String name = s.getFullName() != null ? s.getFullName().toLowerCase() : "";
                    if (id.contains(q) || name.contains(q)) filtered.add(s);
                }
                refreshStudentCombo(filtered);
                cmbStudent.setSelectedItem(editor.getText());
                cmbStudent.showPopup();
            }
        });
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
        if (inv.getStudent() != null) cmbStudent.setSelectedItem(inv.getStudent());
        if (inv.getTotalAmount() != null) txtTotalAmount.setText(inv.getTotalAmount().toPlainString());
        dcIssueDate.setDate(parseDate(inv.getIssueDate()));
        if (inv.getStatus() != null) cmbStatus.setSelectedItem(inv.getStatus());
        recalcDiscountedAmount();
    }

    private Invoice getFromForm() {
        Invoice inv = new Invoice();
        Student selected = (Student) cmbStudent.getSelectedItem();
        if (selected != null && selected.getId() != null) {
            Student s = new Student();
            s.setId(selected.getId());
            inv.setStudent(s);
        }
        // Lưu số tiền sau khuyến mãi (đã hiển thị ở ô "Sau khuyến mãi")
        String amountText = txtAmountAfterDiscount.getText().trim().replace(",", "");
        if (amountText.isEmpty()) amountText = txtTotalAmount.getText().trim().replace(",", "");
        inv.setTotalAmount(new BigDecimal(amountText));
        inv.setIssueDate(formatDate(dcIssueDate.getDate()));
        inv.setStatus((String) cmbStatus.getSelectedItem());
        return inv;
    }

    private boolean validateForm() {
        Student selected = (Student) cmbStudent.getSelectedItem();
        if (selected == null || selected.getId() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtTotalAmount.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Tổng tiền.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (dcIssueDate.getDate() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày xuất.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
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
