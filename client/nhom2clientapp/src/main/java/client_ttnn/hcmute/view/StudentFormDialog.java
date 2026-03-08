package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.StudentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Cửa sổ dialog dùng cho Thêm mới / Cập nhật học viên.
 * Hiển thị form nhập liệu và nút Lưu / Hủy.
 */
public class StudentFormDialog extends JDialog {

    private final StudentApiService apiService;
    private final boolean isEditMode;
    private final Student initialStudent;
    private final Runnable onSuccess;

    private JTextField txtFullName;
    private JTextField txtDateOfBirth;
    private JComboBox<String> cmbGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JComboBox<String> cmbStatus;

    public StudentFormDialog(Window owner, StudentApiService apiService,
                             boolean isEditMode, Student initialStudent, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật học viên" : "Thêm học viên mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialStudent = initialStudent;
        this.onSuccess = onSuccess;

        setSize(820, 680);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialStudent != null) {
            fillForm(initialStudent);
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
        txtFullName = new JTextField(fieldCols);
        txtDateOfBirth = new JTextField(fieldCols);
        txtDateOfBirth.setToolTipText("yyyy-MM-dd");
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cmbGender.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, cmbGender.getPreferredSize().height));
        txtPhone = new JTextField(fieldCols);
        txtEmail = new JTextField(fieldCols);
        txtAddress = new JTextField(fieldCols);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtFullName, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ngày sinh (yyyy-MM-dd):"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtDateOfBirth, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbGender, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Điện thoại:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtPhone, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtEmail, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtAddress, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(Color.WHITE);
        // Lấy kích thước nút chuẩn (giống 3 nút Thêm/Sửa/Xóa ở màn hình chính)
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

    private void fillForm(Student s) {
        if (s == null) return;
        txtFullName.setText(s.getFullName() != null ? s.getFullName() : "");
        txtDateOfBirth.setText(s.getDateOfBirth() != null ? s.getDateOfBirth() : "");
        if (s.getGender() != null) cmbGender.setSelectedItem(s.getGender());
        txtPhone.setText(s.getPhone() != null ? s.getPhone() : "");
        txtEmail.setText(s.getEmail() != null ? s.getEmail() : "");
        txtAddress.setText(s.getAddress() != null ? s.getAddress() : "");
        if (s.getStatus() != null) cmbStatus.setSelectedItem(s.getStatus());
    }

    private Student getStudentFromForm() {
        Student s = new Student();
        s.setFullName(txtFullName.getText().trim());
        s.setDateOfBirth(txtDateOfBirth.getText().trim());
        s.setGender((String) cmbGender.getSelectedItem());
        s.setPhone(txtPhone.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setAddress(txtAddress.getText().trim());
        s.setStatus((String) cmbStatus.getSelectedItem());
        return s;
    }

    private boolean validateForm() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDateOfBirth.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày sinh.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        Student student = getStudentFromForm();
        try {
            if (isEditMode && initialStudent != null) {
                apiService.updateStudent(initialStudent.getId(), student);
                JOptionPane.showMessageDialog(this, "Cập nhật học viên thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createStudent(student);
                JOptionPane.showMessageDialog(this, "Thêm học viên thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
