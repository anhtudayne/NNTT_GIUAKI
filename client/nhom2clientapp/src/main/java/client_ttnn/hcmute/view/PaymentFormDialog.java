package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Payment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PaymentApiService;
import client_ttnn.hcmute.service.StudentApiService;
import com.toedter.calendar.JDateChooser;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentFormDialog extends JDialog {
    private final PaymentApiService apiService;
    private final InvoiceApiService invoiceApiService;
    private final StudentApiService studentApiService;
    private final boolean isEditMode;
    private final Payment initial;
    private final Runnable onSuccess;
    /** Khi thêm thanh toán từ chi tiết hóa đơn: gắn payment với invoice này. */
    private final Long forInvoiceId;
    private final Long forStudentId;

    private JComboBox<Invoice> cmbInvoice;
    private JComboBox<Student> cmbStudent;
    private JTextField txtEnrollmentId, txtAmount;
    private JDateChooser dcPaymentDate;
    private JComboBox<String> cmbMethod, cmbStatus;
    private List<Student> allStudents = new ArrayList<>();
    private List<Invoice> selectableInvoices = new ArrayList<>();

    public PaymentFormDialog(Window owner, PaymentApiService apiService,
                             boolean isEditMode, Payment initial, Runnable onSuccess) {
        this(owner, apiService, isEditMode, initial, onSuccess, null, null);
    }

    /** Mở form thêm thanh toán gắn với hóa đơn (pre-fill studentId, gửi invoiceId khi lưu). */
    public PaymentFormDialog(Window owner, PaymentApiService apiService,
                             boolean isEditMode, Payment initial, Runnable onSuccess,
                             Long forInvoiceId, Long forStudentId) {
        super(owner, isEditMode ? "Cập nhật thanh toán" : "Thêm thanh toán mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.invoiceApiService = new InvoiceApiService();
        this.studentApiService = new StudentApiService();
        this.isEditMode = isEditMode;
        this.initial = initial;
        this.onSuccess = onSuccess;
        this.forInvoiceId = forInvoiceId;
        this.forStudentId = forStudentId;
        setSize(820, 620);
        setMinimumSize(new Dimension(560, 480));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        loadStudents();
        loadInvoicesForSelection();
        if (initial != null) fillForm(initial);
        else if (forInvoiceId != null) preselectInvoice(forInvoiceId);
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
        cmbInvoice = new JComboBox<>();
        cmbInvoice.setEditable(true);
        cmbInvoice.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Invoice inv) {
                    String id = inv.getInvoiceId() != null ? String.valueOf(inv.getInvoiceId()) : "--";
                    String st = inv.getStatus() != null ? inv.getStatus() : "";
                    String student = (inv.getStudent() != null && inv.getStudent().getFullName() != null) ? inv.getStudent().getFullName() : "";
                    setText("HD#" + id + " - " + student + " (" + st + ")");
                }
                return this;
            }
        });

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
        txtEnrollmentId = new JTextField(cols);
        txtAmount = new JTextField(cols);
        dcPaymentDate = new JDateChooser();
        dcPaymentDate.setDateFormatString("yyyy-MM-dd");
        cmbMethod = new JComboBox<>(new String[]{"Cash", "BankTransfer", "Momo", "Card"});
        cmbStatus = new JComboBox<>(new String[]{"Success", "Failed", "Refunded"});
        cmbMethod.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbMethod.getPreferredSize().height));
        cmbStatus.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        if (!isEditMode) {
            addRow(formPanel, gbc, gbcField, row++, "Hóa đơn (Partial/Unpaid):", cmbInvoice);
        }
        addRow(formPanel, gbc, gbcField, row++, "Học viên:", cmbStudent);
        addRow(formPanel, gbc, gbcField, row++, "Enrollment ID (tùy chọn):", txtEnrollmentId);
        addRow(formPanel, gbc, gbcField, row++, "Số tiền:", txtAmount);
        addRow(formPanel, gbc, gbcField, row++, "Ngày thanh toán:", dcPaymentDate);
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phương thức:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbMethod, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        hookInvoiceFilter();
        hookStudentFilter();
        cmbInvoice.addActionListener(e -> onInvoiceSelected());

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
        if (p.getStudent() != null) cmbStudent.setSelectedItem(p.getStudent());
        if (p.getEnrollment() != null && p.getEnrollment().getEnrollmentId() != null) txtEnrollmentId.setText(String.valueOf(p.getEnrollment().getEnrollmentId()));
        if (p.getAmount() != null) txtAmount.setText(p.getAmount().toPlainString());
        dcPaymentDate.setDate(parseDate(p.getPaymentDate()));
        if (p.getPaymentMethod() != null) cmbMethod.setSelectedItem(p.getPaymentMethod());
        if (p.getStatus() != null) cmbStatus.setSelectedItem(p.getStatus());
    }

    private Payment getFromForm() {
        Payment p = new Payment();
        Student selectedStudent = (Student) cmbStudent.getSelectedItem();
        if (selectedStudent != null && selectedStudent.getId() != null) {
            Student s = new Student();
            s.setId(selectedStudent.getId());
            p.setStudent(s);
        }
        if (!txtEnrollmentId.getText().trim().isEmpty()) {
            Enrollment e = new Enrollment();
            e.setEnrollmentId(Long.parseLong(txtEnrollmentId.getText().trim()));
            p.setEnrollment(e);
        }
        Long invoiceId = resolveInvoiceIdForSave();
        if (invoiceId != null) {
            Invoice inv = new Invoice();
            inv.setInvoiceId(invoiceId);
            p.setInvoice(inv);
        }
        String amountText = txtAmount.getText().trim().replace(",", "");
        p.setAmount(new BigDecimal(amountText));
        p.setPaymentDate(formatDate(dcPaymentDate.getDate()));
        p.setPaymentMethod((String) cmbMethod.getSelectedItem());
        p.setStatus((String) cmbStatus.getSelectedItem());
        return p;
    }

    private boolean validateForm() {
        if (!isEditMode) {
            Long invId = resolveInvoiceIdForSave();
            if (invId == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn (Partial/Unpaid).", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        }
        Student selected = (Student) cmbStudent.getSelectedItem();
        if (selected == null || selected.getId() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtAmount.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Số tiền.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (dcPaymentDate.getDate() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày thanh toán.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (!txtEnrollmentId.getText().trim().isEmpty()) {
            try { Long.parseLong(txtEnrollmentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enrollment ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        }
        try { new BigDecimal(txtAmount.getText().trim().replace(",", "")); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void loadStudents() {
        try {
            allStudents = studentApiService.getAllStudents();
            refreshStudentCombo(allStudents);
        } catch (Exception e) {
            // ignore
        }
    }

    private void loadInvoicesForSelection() {
        if (isEditMode) return;
        try {
            // merge Unpaid + Partial, preserve order
            Map<Long, Invoice> map = new LinkedHashMap<>();
            for (Invoice inv : invoiceApiService.getInvoicesByStatus("Unpaid")) {
                if (inv.getInvoiceId() != null) map.put(inv.getInvoiceId(), inv);
            }
            for (Invoice inv : invoiceApiService.getInvoicesByStatus("Partial")) {
                if (inv.getInvoiceId() != null) map.put(inv.getInvoiceId(), inv);
            }
            selectableInvoices = new ArrayList<>(map.values());
            refreshInvoiceCombo(selectableInvoices);
        } catch (Exception e) {
            // ignore
        }
    }

    private void refreshInvoiceCombo(List<Invoice> invoices) {
        Invoice selected = (Invoice) cmbInvoice.getSelectedItem();
        DefaultComboBoxModel<Invoice> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        if (invoices != null) {
            for (Invoice inv : invoices) model.addElement(inv);
        }
        cmbInvoice.setModel(model);
        if (selected != null) cmbInvoice.setSelectedItem(selected);
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

    private void hookInvoiceFilter() {
        if (cmbInvoice == null) return;
        Component editorComp = cmbInvoice.getEditor().getEditorComponent();
        if (!(editorComp instanceof JTextField editor)) return;
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String q = editor.getText() != null ? editor.getText().trim().toLowerCase() : "";
                if (q.isEmpty()) {
                    refreshInvoiceCombo(selectableInvoices);
                    cmbInvoice.hidePopup();
                    return;
                }
                List<Invoice> filtered = new ArrayList<>();
                for (Invoice inv : selectableInvoices) {
                    String id = inv.getInvoiceId() != null ? String.valueOf(inv.getInvoiceId()) : "";
                    String st = inv.getStatus() != null ? inv.getStatus().toLowerCase() : "";
                    String name = (inv.getStudent() != null && inv.getStudent().getFullName() != null) ? inv.getStudent().getFullName().toLowerCase() : "";
                    if (id.contains(q) || name.contains(q) || st.contains(q)) filtered.add(inv);
                }
                refreshInvoiceCombo(filtered);
                cmbInvoice.setSelectedItem(editor.getText());
                cmbInvoice.showPopup();
            }
        });
    }

    private void hookStudentFilter() {
        Component editorComp = cmbStudent.getEditor().getEditorComponent();
        if (!(editorComp instanceof JTextField editor)) return;
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                if (!cmbStudent.isEnabled()) return;
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

    private void preselectInvoice(Long invoiceId) {
        if (isEditMode) return;
        if (invoiceId == null) return;
        for (Invoice inv : selectableInvoices) {
            if (inv.getInvoiceId() != null && inv.getInvoiceId().equals(invoiceId)) {
                cmbInvoice.setSelectedItem(inv);
                cmbInvoice.setEnabled(false);
                onInvoiceSelected();
                return;
            }
        }
        // fallback: keep value typed
        cmbInvoice.getEditor().setItem(String.valueOf(invoiceId));
        cmbInvoice.setEnabled(false);
    }

    private void onInvoiceSelected() {
        if (isEditMode) return;
        if (forInvoiceId != null) {
            cmbStudent.setEnabled(false);
            return;
        }
        Object selected = cmbInvoice.getSelectedItem();
        if (!(selected instanceof Invoice inv)) {
            cmbStudent.setEnabled(true);
            return;
        }
        if (inv.getStudent() != null && inv.getStudent().getId() != null) {
            cmbStudent.setSelectedItem(inv.getStudent());
            cmbStudent.setEnabled(false);
        } else {
            cmbStudent.setEnabled(true);
        }
    }

    private Long resolveInvoiceIdForSave() {
        if (forInvoiceId != null) return forInvoiceId;
        if (isEditMode) {
            return initial != null && initial.getInvoice() != null ? initial.getInvoice().getInvoiceId() : null;
        }
        Object selected = cmbInvoice.getSelectedItem();
        if (selected instanceof Invoice inv && inv.getInvoiceId() != null) return inv.getInvoiceId();
        return null;
    }

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
