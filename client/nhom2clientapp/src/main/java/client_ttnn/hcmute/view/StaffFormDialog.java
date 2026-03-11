package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Staff;
import client_ttnn.hcmute.service.StaffApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.regex.Pattern;
import client_ttnn.hcmute.util.ButtonStyles;

public class StaffFormDialog extends JDialog {

    private final StaffApiService apiService;
    private final boolean isEditMode;
    private final Staff initialStaff;
    private final Runnable onSuccess;

    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,11}$");

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
        content.setBackground(new Color(245, 247, 250));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel(isEditMode ? "Cập nhật nhân sự" : "Thêm nhân sự mới");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        content.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 10, 15, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thông tin nhân sự",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                new Color(52, 73, 94)
            )
        ));
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
        txtFullName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        txtPhone = new JTextField(fieldCols);
        txtPhone.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtPhone.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePhone();
            }
        });
        
        txtEmail = new JTextField(fieldCols);
        txtEmail.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtEmail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });
        
        cmbRole = new JComboBox<>(new String[]{"Admin", "Consultant", "Accountant"});
        cmbRole.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cmbRole.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, 30));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblName = new JLabel("Họ và tên:");
        lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblName, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtFullName, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPhone = new JLabel("Điện thoại:");
        lblPhone.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblPhone, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtPhone, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblEmail, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtEmail, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblRole = new JLabel("Chức vụ:");
        lblRole.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblRole, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbRole, gbcField);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton btnSave = ButtonStyles.createPrimaryButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        
        JButton btnCancel = ButtonStyles.createNeutralButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));

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

    private boolean validateEmail() {
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            txtEmail.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            txtEmail.setToolTipText("Email không hợp lệ");
            return false;
        } else {
            txtEmail.setBorder(UIManager.getBorder("TextField.border"));
            txtEmail.setToolTipText(null);
            return true;
        }
    }
    
    private boolean validatePhone() {
        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            txtPhone.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            txtPhone.setToolTipText("Số điện thoại phải có 10-11 chữ số");
            return false;
        } else {
            txtPhone.setBorder(UIManager.getBorder("TextField.border"));
            txtPhone.setToolTipText(null);
            return true;
        }
    }
    
    private boolean validateForm() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtFullName.requestFocus();
            return false;
        }
        if (!txtEmail.getText().trim().isEmpty() && !validateEmail()) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        if (!txtPhone.getText().trim().isEmpty() && !validatePhone()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
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
