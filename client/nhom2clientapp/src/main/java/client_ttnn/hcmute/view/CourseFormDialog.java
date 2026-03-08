package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.service.CourseApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CourseFormDialog extends JDialog {

    private final CourseApiService apiService;
    private final boolean isEditMode;
    private final Course initialCourse;
    private final Runnable onSuccess;

    private JTextField txtCourseName;
    private JTextField txtDescription;
    private JComboBox<String> cmbLevel;
    private JTextField txtDuration;
    private JTextField txtFee;
    private JComboBox<String> cmbStatus;

    public CourseFormDialog(Window owner, CourseApiService apiService,
                            boolean isEditMode, Course initialCourse, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật khóa học" : "Thêm khóa học mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialCourse = initialCourse;
        this.onSuccess = onSuccess;

        setSize(820, 680);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialCourse != null) {
            fillForm(initialCourse);
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
        txtCourseName = new JTextField(fieldCols);
        txtDescription = new JTextField(fieldCols);
        cmbLevel = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        cmbLevel.setPreferredSize(new Dimension(txtCourseName.getPreferredSize().width, cmbLevel.getPreferredSize().height));
        txtDuration = new JTextField(fieldCols);
        txtFee = new JTextField(fieldCols);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setPreferredSize(new Dimension(txtCourseName.getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tên khóa học:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtCourseName, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtDescription, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Cấp độ:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbLevel, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Thời lượng (giờ):"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtDuration, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Học phí:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtFee, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

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

    private void fillForm(Course c) {
        if (c == null) return;
        txtCourseName.setText(c.getCourseName() != null ? c.getCourseName() : "");
        txtDescription.setText(c.getDescription() != null ? c.getDescription() : "");
        if (c.getLevel() != null) cmbLevel.setSelectedItem(c.getLevel());
        txtDuration.setText(c.getDuration() != null ? c.getDuration().toString() : "");
        txtFee.setText(c.getFee() != null ? c.getFee().toString() : "");
        if (c.getStatus() != null) cmbStatus.setSelectedItem(c.getStatus());
    }

    private Course getCourseFromForm() {
        Course c = new Course();
        c.setCourseName(txtCourseName.getText().trim());
        c.setDescription(txtDescription.getText().trim());
        c.setLevel((String) cmbLevel.getSelectedItem());
        try {
            c.setDuration(Integer.parseInt(txtDuration.getText().trim()));
        } catch (NumberFormatException e) {
            c.setDuration(0);
        }
        try {
            c.setFee(Double.parseDouble(txtFee.getText().trim()));
        } catch (NumberFormatException e) {
            c.setFee(0.0);
        }
        c.setStatus((String) cmbStatus.getSelectedItem());
        return c;
    }

    private boolean validateForm() {
        if (txtCourseName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khóa học.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtDuration.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Thời lượng phải là số nguyên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Double.parseDouble(txtFee.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Học phí phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        Course course = getCourseFromForm();
        try {
            if (isEditMode && initialCourse != null) {
                apiService.updateCourse(initialCourse.getCourseId(), course);
                JOptionPane.showMessageDialog(this, "Cập nhật khóa học thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createCourse(course);
                JOptionPane.showMessageDialog(this, "Thêm khóa học thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
