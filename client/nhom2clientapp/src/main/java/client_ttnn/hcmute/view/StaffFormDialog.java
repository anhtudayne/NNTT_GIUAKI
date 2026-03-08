package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Staff;
import client_ttnn.hcmute.service.StaffApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaffFormDialog extends JDialog {

    private final StaffApiService apiService;
    private final boolean isEditMode;
    private final Staff initialStaff;
    private final Runnable onSuccess;

    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;

    public StaffFormDialog(Window owner, StaffApiService apiService,
                           boolean isEditMode, Staff initialStaff, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật nhân sự" : "Thêm nhân sự mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialStaff = initialStaff;
        this.onSuccess = onSuccess;

        setSize(820, 680);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialStaff != null) {
            fillForm(initialStaff);
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
        txtPhone = new JTextField(fieldCols);
        txtEmail = new JTextField(fieldCols);
        cmbRole = new JComboBox<>(new String[]{"Admin", "Consultant", "Accountant"});
        cmbRole.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, cmbRole.getPreferredSize().height));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtFullName, gbcField);
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
        formPanel.add(new JLabel("Chức vụ:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbRole, gbcField);

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

    private void fillForm(Staff s) {
        if (s == null) return;
        txtFullName.setText(s.getFullName() != null ? s.getFullName() : "");
        txtPhone.setText(s.getPhone() != null ? s.getPhone() : "");
        txtEmail.setText(s.getEmail() != null ? s.getEmail() : "");
        if (s.getRole() != null) cmbRole.setSelectedItem(s.getRole());
    }

    private Staff getStaffFromForm() {
        Staff s = new Staff();
        s.setFullName(txtFullName.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setRole((String) cmbRole.getSelectedItem());
        return s;
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
        Staff staff = getStaffFromForm();
        try {
            if (isEditMode && initialStaff != null) {
                staff.setStaffId(initialStaff.getStaffId());
                if (!apiService.updateStaff(staff)) {
                    throw new Exception("Cập nhật thất bại.");
                }
                JOptionPane.showMessageDialog(this, "Cập nhật nhân sự thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (apiService.createStaff(staff) == null) {
                    throw new Exception("Thêm nhân sự thất bại.");
                }
                JOptionPane.showMessageDialog(this, "Thêm nhân sự thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
