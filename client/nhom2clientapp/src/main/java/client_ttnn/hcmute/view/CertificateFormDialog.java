package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Certificate;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.CertificateApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import client_ttnn.hcmute.util.ButtonStyles;

public class CertificateFormDialog extends JDialog {
    private final CertificateApiService apiService;
    private final boolean isEditMode;
    private final Certificate initial;
    private final Runnable onSuccess;

    private JTextField txtStudentId, txtCourseId, txtCertificateName, txtIssueDate;

    public CertificateFormDialog(Window owner, CertificateApiService apiService,
                                boolean isEditMode, Certificate initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật chứng chỉ" : "Thêm chứng chỉ mới", ModalityType.APPLICATION_MODAL);
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
        txtCourseId = new JTextField(cols);
        txtCertificateName = new JTextField(cols);
        txtIssueDate = new JTextField(cols);
        txtIssueDate.setToolTipText("yyyy-MM-dd");

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Student ID:", txtStudentId);
        addRow(formPanel, gbc, gbcField, row++, "Course ID:", txtCourseId);
        addRow(formPanel, gbc, gbcField, row++, "Tên chứng chỉ:", txtCertificateName);
        addRow(formPanel, gbc, gbcField, row++, "Ngày cấp (yyyy-MM-dd):", txtIssueDate);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(Color.WHITE);
        Dimension refBtn = new JButton("Tìm kiếm").getPreferredSize();
        JButton btnSave = ButtonStyles.createPrimaryButton("Lưu");
        btnSave.setPreferredSize(refBtn);
        btnSave.setMinimumSize(refBtn);
        JButton btnCancel = ButtonStyles.createNeutralButton("Hủy");
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

    private void fillForm(Certificate c) {
        if (c == null) return;
        if (c.getStudent() != null && c.getStudent().getId() != null) txtStudentId.setText(String.valueOf(c.getStudent().getId()));
        if (c.getCourse() != null && c.getCourse().getCourseId() != null) txtCourseId.setText(String.valueOf(c.getCourse().getCourseId()));
        txtCertificateName.setText(str(c.getCertificateName()));
        txtIssueDate.setText(str(c.getIssueDate()));
    }

    private Certificate getFromForm() {
        Certificate c = new Certificate();
        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        c.setStudent(s);
        Course co = new Course();
        co.setCourseId(Long.parseLong(txtCourseId.getText().trim()));
        c.setCourse(co);
        c.setCertificateName(txtCertificateName.getText().trim());
        c.setIssueDate(txtIssueDate.getText().trim());
        return c;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtCourseId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Course ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtCertificateName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chứng chỉ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtIssueDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày cấp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtStudentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Student ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtCourseId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Course ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            Certificate c = getFromForm();
            if (isEditMode && initial != null) {
                apiService.updateCertificate(initial.getCertificateId(), c);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createCertificate(c);
                JOptionPane.showMessageDialog(this, "Thêm chứng chỉ thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
