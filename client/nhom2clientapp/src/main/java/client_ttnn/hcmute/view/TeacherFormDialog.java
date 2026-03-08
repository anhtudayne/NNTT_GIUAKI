package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.TeacherApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TeacherFormDialog extends JDialog {
    private final TeacherApiService apiService;
    private final boolean isEditMode;
    private final Teacher initialTeacher;
    private final Runnable onSuccess;

    private JTextField txtFullName, txtPhone, txtEmail, txtSpecialty, txtHireDate;
    private JComboBox<String> cmbStatus;

    public TeacherFormDialog(Window owner, TeacherApiService apiService,
                             boolean isEditMode, Teacher initialTeacher, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật giảng viên" : "Thêm giảng viên mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialTeacher = initialTeacher;
        this.onSuccess = onSuccess;
        setSize(820, 680);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialTeacher != null) fillForm(initialTeacher);
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
        txtFullName = new JTextField(cols);
        txtPhone = new JTextField(cols);
        txtEmail = new JTextField(cols);
        txtSpecialty = new JTextField(cols);
        txtHireDate = new JTextField(cols);
        txtHireDate.setToolTipText("yyyy-MM-dd");
        cmbStatus = new JComboBox<>(new String[]{"Active", "OnLeave", "Inactive"});
        cmbStatus.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Họ và tên:", txtFullName);
        addRow(formPanel, gbc, gbcField, row++, "Điện thoại:", txtPhone);
        addRow(formPanel, gbc, gbcField, row++, "Email:", txtEmail);
        addRow(formPanel, gbc, gbcField, row++, "Chuyên môn (VD: IELTS):", txtSpecialty);
        addRow(formPanel, gbc, gbcField, row++, "Ngày vào làm (yyyy-MM-dd):", txtHireDate);
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

    private void fillForm(Teacher t) {
        if (t == null) return;
        txtFullName.setText(str(t.getFullName()));
        txtPhone.setText(str(t.getPhone()));
        txtEmail.setText(str(t.getEmail()));
        txtSpecialty.setText(str(t.getSpecialty()));
        txtHireDate.setText(str(t.getHireDate()));
        if (t.getStatus() != null) cmbStatus.setSelectedItem(t.getStatus());
    }

    private Teacher getFromForm() {
        Teacher t = new Teacher();
        t.setFullName(txtFullName.getText().trim());
        t.setPhone(txtPhone.getText().trim());
        t.setEmail(txtEmail.getText().trim());
        t.setSpecialty(txtSpecialty.getText().trim());
        t.setHireDate(txtHireDate.getText().trim());
        t.setStatus((String) cmbStatus.getSelectedItem());
        return t;
    }

    private boolean validateForm() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            if (isEditMode && initialTeacher != null) {
                apiService.updateTeacher(initialTeacher.getTeacherId(), getFromForm());
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createTeacher(getFromForm());
                JOptionPane.showMessageDialog(this, "Thêm giảng viên thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
