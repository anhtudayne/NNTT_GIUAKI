package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.ClassesApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClassFormDialog extends JDialog {
    private final ClassesApiService apiService;
    private final boolean isEditMode;
    private final Classes initialClass;
    private final Runnable onSuccess;

    private JTextField txtClassName, txtCourseId, txtTeacherId, txtRoomId, txtStartDate, txtEndDate, txtMaxStudent;
    private JComboBox<String> cmbStatus;

    public ClassFormDialog(Window owner, ClassesApiService apiService,
                           boolean isEditMode, Classes initialClass, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật lớp học" : "Thêm lớp học mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialClass = initialClass;
        this.onSuccess = onSuccess;
        setSize(820, 720);
        setMinimumSize(new Dimension(560, 560));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialClass != null) fillForm(initialClass);
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
        txtClassName = new JTextField(cols);
        txtCourseId = new JTextField(cols);
        txtTeacherId = new JTextField(cols);
        txtRoomId = new JTextField(cols);
        txtStartDate = new JTextField(cols);
        txtStartDate.setToolTipText("yyyy-MM-dd");
        txtEndDate = new JTextField(cols);
        txtEndDate.setToolTipText("yyyy-MM-dd");
        txtMaxStudent = new JTextField(cols);
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Ongoing", "Completed", "Cancelled"});
        cmbStatus.setPreferredSize(new Dimension(txtClassName.getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Tên lớp:", txtClassName);
        addRow(formPanel, gbc, gbcField, row++, "Khóa học ID:", txtCourseId);
        addRow(formPanel, gbc, gbcField, row++, "Giảng viên ID:", txtTeacherId);
        addRow(formPanel, gbc, gbcField, row++, "Phòng ID:", txtRoomId);
        addRow(formPanel, gbc, gbcField, row++, "Ngày bắt đầu (yyyy-MM-dd):", txtStartDate);
        addRow(formPanel, gbc, gbcField, row++, "Ngày kết thúc (yyyy-MM-dd):", txtEndDate);
        addRow(formPanel, gbc, gbcField, row++, "Sĩ số tối đa:", txtMaxStudent);
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

    private void fillForm(Classes c) {
        if (c == null) return;
        txtClassName.setText(str(c.getClassName()));
        if (c.getCourse() != null && c.getCourse().getCourseId() != null) txtCourseId.setText(String.valueOf(c.getCourse().getCourseId()));
        if (c.getTeacher() != null && c.getTeacher().getTeacherId() != null) txtTeacherId.setText(String.valueOf(c.getTeacher().getTeacherId()));
        if (c.getRoom() != null && c.getRoom().getRoomId() != null) txtRoomId.setText(String.valueOf(c.getRoom().getRoomId()));
        txtStartDate.setText(str(c.getStartDate()));
        txtEndDate.setText(str(c.getEndDate()));
        if (c.getMaxStudent() != null) txtMaxStudent.setText(String.valueOf(c.getMaxStudent()));
        if (c.getStatus() != null) cmbStatus.setSelectedItem(c.getStatus());
    }

    private Classes getFromForm() {
        Classes c = new Classes();
        c.setClassName(txtClassName.getText().trim());
        if (!txtCourseId.getText().trim().isEmpty()) {
            Course co = new Course();
            try { co.setCourseId(Long.parseLong(txtCourseId.getText().trim())); } catch (NumberFormatException e) { co.setCourseId(0L); }
            c.setCourse(co);
        }
        if (!txtTeacherId.getText().trim().isEmpty()) {
            Teacher t = new Teacher();
            try { t.setTeacherId(Integer.parseInt(txtTeacherId.getText().trim())); } catch (NumberFormatException e) { t.setTeacherId(0); }
            c.setTeacher(t);
        }
        if (!txtRoomId.getText().trim().isEmpty()) {
            Room r = new Room();
            try { r.setRoomId(Long.parseLong(txtRoomId.getText().trim())); } catch (NumberFormatException e) { r.setRoomId(0L); }
            c.setRoom(r);
        }
        c.setStartDate(txtStartDate.getText().trim());
        c.setEndDate(txtEndDate.getText().trim());
        try { c.setMaxStudent(Integer.parseInt(txtMaxStudent.getText().trim())); } catch (NumberFormatException e) { c.setMaxStudent(0); }
        c.setStatus((String) cmbStatus.getSelectedItem());
        return c;
    }

    private boolean validateForm() {
        if (txtClassName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập tên lớp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtCourseId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Khóa học ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtTeacherId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Giảng viên ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtRoomId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Phòng ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtStartDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtEndDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày kết thúc.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtMaxStudent.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập sĩ số tối đa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            Classes c = getFromForm();
            if (isEditMode && initialClass != null) {
                apiService.updateClass(initialClass.getClassId(), c);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createClass(c);
                JOptionPane.showMessageDialog(this, "Thêm lớp học thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
