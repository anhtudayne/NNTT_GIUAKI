package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.ClassesApiService;
import client_ttnn.hcmute.util.CacheManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.ButtonStyles;

public class ClassFormDialog extends JDialog {
    private final ClassesApiService apiService;
    private final boolean isEditMode;
    private final Classes initialClass;
    private final Runnable onSuccess;

    private JTextField txtClassName, txtStartDate, txtEndDate, txtMaxStudent;
    private JComboBox<CourseItem> cmbCourse;
    private JComboBox<TeacherItem> cmbTeacher;
    private JComboBox<RoomItem> cmbRoom;
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
        loadComboBoxData();
        // fillForm sẽ được gọi sau khi loadComboBoxData hoàn thành (bên trong hàm done của SwingWorker)
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
        cmbCourse = new JComboBox<>();
        cmbTeacher = new JComboBox<>();
        cmbRoom = new JComboBox<>();
        txtStartDate = new JTextField(cols);
        txtStartDate.setToolTipText("yyyy-MM-dd");
        txtEndDate = new JTextField(cols);
        txtEndDate.setToolTipText("yyyy-MM-dd");
        txtMaxStudent = new JTextField(cols);
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Ongoing", "Completed", "Cancelled"});
        cmbStatus.setPreferredSize(new Dimension(txtClassName.getPreferredSize().width, cmbStatus.getPreferredSize().height));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Tên lớp:", txtClassName);
        addRow(formPanel, gbc, gbcField, row++, "Khóa học:", cmbCourse);
        addRow(formPanel, gbc, gbcField, row++, "Giảng viên:", cmbTeacher);
        addRow(formPanel, gbc, gbcField, row++, "Phòng học:", cmbRoom);
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

    private void loadComboBoxData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cmbCourse.setEnabled(false);
        cmbTeacher.setEnabled(false);
        cmbRoom.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Course> courses;
            List<Teacher> teachers;
            List<Room> rooms;

            @Override
            protected Void doInBackground() {
                courses = CacheManager.getInstance().getCourses();
                teachers = CacheManager.getInstance().getTeachers();
                rooms = CacheManager.getInstance().getRooms();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                cmbCourse.setEnabled(true);
                cmbTeacher.setEnabled(true);
                cmbRoom.setEnabled(true);

                if (courses != null) {
                    for (Course c : courses) cmbCourse.addItem(new CourseItem(c));
                }
                if (teachers != null) {
                    for (Teacher t : teachers) cmbTeacher.addItem(new TeacherItem(t));
                }
                if (rooms != null) {
                    for (Room r : rooms) cmbRoom.addItem(new RoomItem(r));
                }
                
                // Mới điền form lại nếu có initialClass vì lúc này combobox mới có cục data
                if (initialClass != null) fillForm(initialClass);
            }
        };
        worker.execute();
    }

    private void fillForm(Classes c) {
        if (c == null) return;
        txtClassName.setText(str(c.getClassName()));
        
        if (c.getCourse() != null && c.getCourse().getCourseId() != null) {
            for (int i = 0; i < cmbCourse.getItemCount(); i++) {
                if (cmbCourse.getItemAt(i).c.getCourseId().equals(c.getCourse().getCourseId())) {
                    cmbCourse.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        if (c.getTeacher() != null && c.getTeacher().getTeacherId() != null) {
            for (int i = 0; i < cmbTeacher.getItemCount(); i++) {
                if (cmbTeacher.getItemAt(i).t.getTeacherId().equals(c.getTeacher().getTeacherId())) {
                    cmbTeacher.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        if (c.getRoom() != null && c.getRoom().getRoomId() != null) {
            for (int i = 0; i < cmbRoom.getItemCount(); i++) {
                if (cmbRoom.getItemAt(i).r.getRoomId().equals(c.getRoom().getRoomId())) {
                    cmbRoom.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        txtStartDate.setText(str(c.getStartDate()));
        txtEndDate.setText(str(c.getEndDate()));
        if (c.getMaxStudent() != null) txtMaxStudent.setText(String.valueOf(c.getMaxStudent()));
        if (c.getStatus() != null) cmbStatus.setSelectedItem(c.getStatus());
    }

    private Classes getFromForm() {
        Classes c = new Classes();
        c.setClassName(txtClassName.getText().trim());
        
        if (cmbCourse.getSelectedItem() != null) {
            c.setCourse(((CourseItem) cmbCourse.getSelectedItem()).c);
        }
        if (cmbTeacher.getSelectedItem() != null) {
            c.setTeacher(((TeacherItem) cmbTeacher.getSelectedItem()).t);
        }
        if (cmbRoom.getSelectedItem() != null) {
            c.setRoom(((RoomItem) cmbRoom.getSelectedItem()).r);
        }
        
        c.setStartDate(txtStartDate.getText().trim());
        c.setEndDate(txtEndDate.getText().trim());
        try { c.setMaxStudent(Integer.parseInt(txtMaxStudent.getText().trim())); } catch (NumberFormatException e) { c.setMaxStudent(0); }
        c.setStatus((String) cmbStatus.getSelectedItem());
        return c;
    }

    private boolean validateForm() {
        if (txtClassName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập tên lớp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (cmbCourse.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (cmbTeacher.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (cmbRoom.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
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

    static class CourseItem {
        Course c;
        public CourseItem(Course c) { this.c = c; }
        @Override public String toString() { return c.getCourseName(); }
    }
    static class TeacherItem {
        Teacher t;
        public TeacherItem(Teacher t) { this.t = t; }
        @Override public String toString() { return t.getFullName(); }
    }
    static class RoomItem {
        Room r;
        public RoomItem(Room r) { this.r = r; }
        @Override public String toString() { return r.getRoomName(); }
    }
}
