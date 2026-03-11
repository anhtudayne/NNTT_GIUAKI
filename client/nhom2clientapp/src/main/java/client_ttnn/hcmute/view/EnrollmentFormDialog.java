package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.EnrollmentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import client_ttnn.hcmute.util.ButtonStyles;

public class EnrollmentFormDialog extends JDialog {
    private final EnrollmentApiService apiService;
    private final boolean isEditMode;
    private final Enrollment initial;
    private final Runnable onSuccess;

    private JTextField txtStudentId, txtClassId, txtEnrollmentDate;
    private JComboBox<String> cmbStatus, cmbResult;

    public EnrollmentFormDialog(Window owner, EnrollmentApiService apiService,
                                boolean isEditMode, Enrollment initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật ghi danh" : "Thêm ghi danh mới", ModalityType.APPLICATION_MODAL);
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
        txtClassId = new JTextField(cols);
        txtEnrollmentDate = new JTextField(cols);
        txtEnrollmentDate.setToolTipText("yyyy-MM-dd");
        cmbStatus = new JComboBox<>(new String[]{"Registered", "Studying", "Dropped", "Completed"});
        cmbResult = new JComboBox<>(new String[]{"Pending", "Pass", "Fail"});
        cmbStatus.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbStatus.getPreferredSize().height));
        cmbResult.setPreferredSize(new Dimension(new JTextField(cols).getPreferredSize().width, cmbResult.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Student ID:", txtStudentId);
        addRow(formPanel, gbc, gbcField, row++, "Class ID:", txtClassId);
        addRow(formPanel, gbc, gbcField, row++, "Ngày ghi danh (yyyy-MM-dd):", txtEnrollmentDate);
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Kết quả:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbResult, gbcField);

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

    private void fillForm(Enrollment e) {
        if (e == null) return;
        if (e.getStudent() != null && e.getStudent().getId() != null) txtStudentId.setText(String.valueOf(e.getStudent().getId()));
        if (e.getClassEntity() != null && e.getClassEntity().getClassId() != null) txtClassId.setText(String.valueOf(e.getClassEntity().getClassId()));
        txtEnrollmentDate.setText(str(e.getEnrollmentDate()));
        if (e.getStatus() != null) cmbStatus.setSelectedItem(e.getStatus());
        if (e.getResult() != null) cmbResult.setSelectedItem(e.getResult());
    }

    private Enrollment getFromForm() {
        Enrollment e = new Enrollment();
        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        e.setStudent(s);
        Classes c = new Classes();
        c.setClassId(Long.parseLong(txtClassId.getText().trim()));
        e.setClassEntity(c);
        e.setEnrollmentDate(txtEnrollmentDate.getText().trim());
        e.setStatus((String) cmbStatus.getSelectedItem());
        e.setResult((String) cmbResult.getSelectedItem());
        return e;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtClassId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Class ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtEnrollmentDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày ghi danh.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtStudentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Student ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtClassId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Class ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            Enrollment e = getFromForm();
            if (isEditMode && initial != null) {
                apiService.updateEnrollment(initial.getEnrollmentId(), e);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createEnrollment(e);
                JOptionPane.showMessageDialog(this, "Thêm ghi danh thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
