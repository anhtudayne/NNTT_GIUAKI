package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Certificate;
import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.CertificateApiService;
import client_ttnn.hcmute.service.StudentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CertificateFormDialog extends JDialog {
    private final CertificateApiService apiService;
    private final StudentApiService studentApiService;
    private final boolean isEditMode;
    private final Certificate initial;
    private final Runnable onSuccess;

    private JTextField txtStudentSearch;
    private JComboBox<StudentItem> cmbStudent;
    private JComboBox<CourseItem> cmbCourse;
    private JTextField txtCertificateName;
    private JTextField txtIssueDate;

    public CertificateFormDialog(Window owner, CertificateApiService apiService,
                                boolean isEditMode, Certificate initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật chứng chỉ" : "Thêm chứng chỉ mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.studentApiService = new StudentApiService();
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
        content.setBackground(new Color(245, 247, 250));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel(isEditMode ? "Cập nhật chứng chỉ" : "Thêm chứng chỉ mới");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        content.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 8, 10, 8),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thông tin chứng chỉ",
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

        int cols = 42;
        txtStudentSearch = new JTextField(cols);
        txtStudentSearch.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JButton btnSearchStudent = new JButton("Tìm học viên");
        btnSearchStudent.setBackground(new Color(52, 152, 219));
        btnSearchStudent.setForeground(Color.WHITE);
        btnSearchStudent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnSearchStudent.setFocusPainted(false);
        btnSearchStudent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchStudent.addActionListener(e -> searchStudents());

        JPanel searchStudentPanel = new JPanel(new BorderLayout(8, 0));
        searchStudentPanel.setOpaque(false);
        searchStudentPanel.add(txtStudentSearch, BorderLayout.CENTER);
        searchStudentPanel.add(btnSearchStudent, BorderLayout.EAST);

        cmbStudent = new JComboBox<>();
        cmbStudent.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cmbStudent.addActionListener(e -> onStudentChanged());

        cmbCourse = new JComboBox<>();
        cmbCourse.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        txtCertificateName = new JTextField(cols);
        txtIssueDate = new JTextField(cols);
        txtCertificateName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtIssueDate.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtIssueDate.setToolTipText("yyyy-MM-dd");

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Tìm học viên:", searchStudentPanel);
        addRow(formPanel, gbc, gbcField, row++, "Học viên:", cmbStudent);
        addRow(formPanel, gbc, gbcField, row++, "Khóa học:", cmbCourse);
        addRow(formPanel, gbc, gbcField, row++, "Tên chứng chỉ:", txtCertificateName);
        addRow(formPanel, gbc, gbcField, row++, "Ngày cấp (yyyy-MM-dd):", txtIssueDate);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.add(formPanel, BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(new Color(245, 247, 250));

        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnSave.setFocusPainted(false);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        content.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);

        loadInitialStudents();
    }

    private void addRow(JPanel p, GridBagConstraints gbc, GridBagConstraints gbcField, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        p.add(field, gbcField);
    }

    private void fillForm(Certificate c) {
        if (c == null) return;
        txtCertificateName.setText(str(c.getCertificateName()));
        txtIssueDate.setText(str(c.getIssueDate()));

        Long studentId = c.getStudent() != null ? c.getStudent().getId() : null;
        Long courseId = c.getCourse() != null ? c.getCourse().getCourseId() : null;
        if (studentId != null) {
            preloadStudentAndCourses(studentId, courseId);
        }
    }

    private Certificate getFromForm() {
        Certificate c = new Certificate();
        StudentItem selectedStudent = (StudentItem) cmbStudent.getSelectedItem();
        CourseItem selectedCourse = (CourseItem) cmbCourse.getSelectedItem();

        Student s = new Student();
        s.setId(selectedStudent.getId());
        c.setStudent(s);

        Course co = new Course();
        co.setCourseId(selectedCourse.getCourseId());
        c.setCourse(co);

        c.setCertificateName(txtCertificateName.getText().trim());
        c.setIssueDate(txtIssueDate.getText().trim());
        return c;
    }

    private boolean validateForm() {
        StudentItem selectedStudent = (StudentItem) cmbStudent.getSelectedItem();
        if (selectedStudent == null) { JOptionPane.showMessageDialog(this, "Vui lòng tìm và chọn học viên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        CourseItem selectedCourse = (CourseItem) cmbCourse.getSelectedItem();
        if (selectedCourse == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học của học viên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtCertificateName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chứng chỉ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtIssueDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày cấp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try {
            LocalDate.parse(txtIssueDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày cấp phải đúng định dạng yyyy-MM-dd.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void loadInitialStudents() {
        try {
            List<Student> students = filterStudentsWithCourses(studentApiService.getAllStudents());
            setStudentItems(students, null);
        } catch (Exception ex) {
            setStudentItems(new ArrayList<>(), null);
        }
    }

    private void searchStudents() {
        String keyword = txtStudentSearch.getText().trim();
        try {
            List<Student> students = keyword.isEmpty()
                    ? studentApiService.getAllStudents()
                    : studentApiService.searchByName(keyword);
            students = filterStudentsWithCourses(students);
            setStudentItems(students, null);
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy học viên phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm học viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Student> filterStudentsWithCourses(List<Student> students) {
        List<Student> filtered = new ArrayList<>();
        for (Student s : students) {
            if (s == null || s.getId() == null) {
                continue;
            }
            try {
                List<Classes> classes = studentApiService.getStudentClasses(s.getId());
                if (classes != null && !classes.isEmpty()) {
                    filtered.add(s);
                }
            } catch (Exception ignored) {
                // Skip students that cannot load classes in selector.
            }
        }
        return filtered;
    }

    private void setStudentItems(List<Student> students, Long selectedStudentId) {
        cmbStudent.removeAllItems();
        StudentItem selectedItem = null;
        for (Student s : students) {
            if (s == null || s.getId() == null) continue;
            StudentItem item = new StudentItem(s.getId(), str(s.getFullName()));
            cmbStudent.addItem(item);
            if (selectedStudentId != null && selectedStudentId.equals(s.getId())) {
                selectedItem = item;
            }
        }
        if (selectedItem != null) {
            cmbStudent.setSelectedItem(selectedItem);
        } else if (cmbStudent.getItemCount() > 0) {
            cmbStudent.setSelectedIndex(0);
        } else {
            cmbCourse.removeAllItems();
        }
    }

    private void onStudentChanged() {
        StudentItem selected = (StudentItem) cmbStudent.getSelectedItem();
        if (selected == null) {
            cmbCourse.removeAllItems();
            return;
        }
        loadCoursesForStudent(selected.getId(), null);
    }

    private void preloadStudentAndCourses(Long studentId, Long selectedCourseId) {
        try {
            Student student = studentApiService.getStudentById(studentId);
            List<Student> oneStudent = new ArrayList<>();
            if (student != null && student.getId() != null) {
                oneStudent.add(student);
            }
            setStudentItems(oneStudent, studentId);
        } catch (Exception ex) {
            List<Student> fallback = new ArrayList<>();
            Student s = new Student();
            s.setId(studentId);
            s.setFullName("ID " + studentId);
            fallback.add(s);
            setStudentItems(fallback, studentId);
        }
        loadCoursesForStudent(studentId, selectedCourseId);
    }

    private void loadCoursesForStudent(Long studentId, Long selectedCourseId) {
        cmbCourse.removeAllItems();
        try {
            List<Classes> classes = studentApiService.getStudentClasses(studentId);
            Map<Long, CourseItem> courseMap = new LinkedHashMap<>();
            for (Classes cls : classes) {
                if (cls == null || cls.getCourse() == null || cls.getCourse().getCourseId() == null) continue;
                Course c = cls.getCourse();
                courseMap.putIfAbsent(c.getCourseId(), new CourseItem(c.getCourseId(), str(c.getCourseName())));
            }
            CourseItem selectedItem = null;
            for (CourseItem item : courseMap.values()) {
                cmbCourse.addItem(item);
                if (selectedCourseId != null && selectedCourseId.equals(item.getCourseId())) {
                    selectedItem = item;
                }
            }
            if (selectedItem != null) {
                cmbCourse.setSelectedItem(selectedItem);
            } else if (cmbCourse.getItemCount() > 0) {
                cmbCourse.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải khóa học của học viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class StudentItem {
        private final Long id;
        private final String name;

        private StudentItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            String displayName = name == null || name.isBlank() ? "(Không có tên)" : name;
            return id + " - " + displayName;
        }
    }

    private static class CourseItem {
        private final Long courseId;
        private final String courseName;

        private CourseItem(Long courseId, String courseName) {
            this.courseId = courseId;
            this.courseName = courseName;
        }

        public Long getCourseId() {
            return courseId;
        }

        @Override
        public String toString() {
            String displayName = courseName == null || courseName.isBlank() ? "(Không có tên)" : courseName;
            return courseId + " - " + displayName;
        }
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
