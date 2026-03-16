package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.ClassesApiService;
import client_ttnn.hcmute.service.EnrollmentApiService;
import client_ttnn.hcmute.service.StudentApiService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import client_ttnn.hcmute.util.ButtonStyles;

/**
 * Cửa sổ dialog dùng cho Thêm mới / Cập nhật học viên.
 * Hiển thị form nhập liệu và nút Lưu / Hủy.
 */
public class StudentFormDialog extends JDialog {

    private final StudentApiService apiService;
    private final EnrollmentApiService enrollmentService;
    private final ClassesApiService classesService;
    private final boolean isEditMode;
    private final Student initialStudent;
    private final Runnable onSuccess;

    private JTextField txtFullName;
    private JDateChooser dateChooserBirth;
    private JComboBox<String> cmbGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JComboBox<String> cmbStatus;
    
    // Enrollment management
    private JTable tblEnrollments;
    private DefaultTableModel enrollmentTableModel;
    private JButton btnAddEnrollment;
    private JButton btnRemoveEnrollment;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,11}$");

    public StudentFormDialog(Window owner, StudentApiService apiService,
                             boolean isEditMode, Student initialStudent, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật học viên" : "Thêm học viên mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.enrollmentService = new EnrollmentApiService();
        this.classesService = new ClassesApiService();
        this.isEditMode = isEditMode;
        this.initialStudent = initialStudent;
        this.onSuccess = onSuccess;

        setSize(1000, 720);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialStudent != null) {
            fillForm(initialStudent);
            if (isEditMode) {
                loadEnrollments();
            }
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
        JLabel titleLabel = new JLabel(isEditMode ? "Cập nhật học viên" : "Thêm học viên mới");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        content.add(titlePanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        
        // Tab 1: Basic Info
        JPanel infoTab = createBasicInfoPanel();
        tabbedPane.addTab("📋 Thông tin cơ bản", infoTab);
        
        // Tab 2: Enrollments (only in edit mode)
        if (isEditMode && initialStudent != null) {
            JPanel enrollmentTab = createEnrollmentPanel();
            tabbedPane.addTab("🎓 Đăng ký lớp học", enrollmentTab);
        }
        
        content.add(tabbedPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(new Color(245, 247, 250));

        Dimension refBtn = new JButton("Tìm kiếm").getPreferredSize();
        JButton btnSave = ButtonStyles.createPrimaryButton("Lưu");
        btnSave.setPreferredSize(refBtn);
        btnSave.setMinimumSize(refBtn);
        
        JButton btnCancel = ButtonStyles.createNeutralButton("Hủy");
        btnCancel.setPreferredSize(refBtn);
        btnCancel.setMinimumSize(refBtn);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);
    }

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 10, 15, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thông tin học viên",
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
        
        dateChooserBirth = new JDateChooser();
        dateChooserBirth.setDateFormatString("yyyy-MM-dd");
        dateChooserBirth.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        dateChooserBirth.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, 30));
        
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cmbGender.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cmbGender.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, 30));
        
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
        
        txtAddress = new JTextField(fieldCols);
        txtAddress.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cmbStatus.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, 30));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblName = new JLabel("Họ và tên:");
        lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblName, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtFullName, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblBirth = new JLabel("Ngày sinh:");
        lblBirth.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblBirth, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(dateChooserBirth, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblGender = new JLabel("Giới tính:");
        lblGender.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblGender, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbGender, gbcField);
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
        JLabel lblAddress = new JLabel("Địa chỉ:");
        lblAddress.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblAddress, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtAddress, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBackground(new Color(236, 240, 241));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        btnAddEnrollment = client_ttnn.hcmute.util.ButtonStyles.createPrimaryButton("➕ Đăng ký lớp mới");
        btnAddEnrollment.addActionListener(e -> addEnrollment());

        btnRemoveEnrollment = client_ttnn.hcmute.util.ButtonStyles.createDangerButton("🗑️ Hủy đăng ký");
        btnRemoveEnrollment.setEnabled(false);
        btnRemoveEnrollment.addActionListener(e -> removeEnrollment());

        JButton btnRefresh = client_ttnn.hcmute.util.ButtonStyles.createNeutralButton("⟳ Làm mới");
        btnRefresh.addActionListener(e -> loadEnrollments());

        toolbarPanel.add(btnAddEnrollment);
        toolbarPanel.add(btnRemoveEnrollment);
        toolbarPanel.add(btnRefresh);

        panel.add(toolbarPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Lớp học", "Khóa học", "Giảng viên", "Ngày đăng ký", "Trạng thái", "Kết quả"};
        enrollmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblEnrollments = new JTable(enrollmentTableModel);
        tblEnrollments.setRowHeight(32);
        tblEnrollments.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblEnrollments.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tblEnrollments.getTableHeader().setBackground(new Color(52, 73, 94));
        tblEnrollments.getTableHeader().setForeground(Color.WHITE);
        tblEnrollments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEnrollments.setSelectionBackground(new Color(174, 214, 241));
        tblEnrollments.setSelectionForeground(new Color(44, 62, 80));
        tblEnrollments.setGridColor(new Color(220, 220, 220));

        tblEnrollments.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnRemoveEnrollment.setEnabled(tblEnrollments.getSelectedRow() >= 0);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblEnrollments);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadEnrollments() {
        if (initialStudent == null || initialStudent.getId() == null) return;
        
        try {
            List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
            enrollmentTableModel.setRowCount(0);
            
            for (Enrollment e : enrollments) {
                if (e.getStudent() != null && initialStudent.getId().equals(e.getStudent().getId())) {
                    String className = e.getClassEntity() != null ? e.getClassEntity().getClassName() : "N/A";
                    String courseName = e.getClassEntity() != null && e.getClassEntity().getCourse() != null 
                        ? e.getClassEntity().getCourse().getCourseName() : "N/A";
                    String teacherName = e.getClassEntity() != null && e.getClassEntity().getTeacher() != null 
                        ? e.getClassEntity().getTeacher().getFullName() : "N/A";
                    
                    enrollmentTableModel.addRow(new Object[]{
                        e.getEnrollmentId(),
                        className,
                        courseName,
                        teacherName,
                        e.getEnrollmentDate(),
                        e.getStatus(),
                        e.getResult()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách đăng ký: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEnrollment() {
        try {
            List<Classes> allClasses = classesService.getAllClasses();
            if (allClasses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Không có lớp học nào khả dụng.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create ComboBox with available classes
            JComboBox<ClassItem> cmbClass = new JComboBox<>();
            for (Classes cls : allClasses) {
                if ("Ongoing".equals(cls.getStatus()) || "Pending".equals(cls.getStatus())) {
                    cmbClass.addItem(new ClassItem(cls));
                }
            }

            JDateChooser dateChooser = new JDateChooser();
            dateChooser.setDateFormatString("yyyy-MM-dd");
            dateChooser.setDate(new java.util.Date());

            JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Registered", "Studying", "Completed", "Dropped"});
            JComboBox<String> cmbResult = new JComboBox<>(new String[]{"Pending", "Pass", "Fail"});

            JPanel dialogPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            dialogPanel.add(new JLabel("Lớp học:"));
            dialogPanel.add(cmbClass);
            dialogPanel.add(new JLabel("Ngày đăng ký:"));
            dialogPanel.add(dateChooser);
            dialogPanel.add(new JLabel("Trạng thái:"));
            dialogPanel.add(cmbStatus);
            dialogPanel.add(new JLabel("Kết quả:"));
            dialogPanel.add(cmbResult);

            int result = JOptionPane.showConfirmDialog(this, dialogPanel,
                "Đăng ký lớp học", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION && cmbClass.getSelectedItem() != null && dateChooser.getDate() != null) {
                ClassItem selectedClass = (ClassItem) cmbClass.getSelectedItem();
                
                Enrollment enrollment = new Enrollment();
                
                Student studentRef = new Student();
                studentRef.setId(initialStudent.getId());
                enrollment.setStudent(studentRef);
                
                Classes classRef = new Classes();
                classRef.setClassId(selectedClass.getClassEntity().getClassId());
                enrollment.setClassEntity(classRef);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                enrollment.setEnrollmentDate(sdf.format(dateChooser.getDate()));
                enrollment.setStatus((String) cmbStatus.getSelectedItem());
                enrollment.setResult((String) cmbResult.getSelectedItem());

                enrollmentService.createEnrollment(enrollment);
                JOptionPane.showMessageDialog(this, "Đăng ký lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi đăng ký lớp: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeEnrollment() {
        int row = tblEnrollments.getSelectedRow();
        if (row < 0) return;
        
        Long enrollmentId = (Long) enrollmentTableModel.getValueAt(row, 0);
        String className = (String) enrollmentTableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn hủy đăng ký lớp " + className + "?",
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                enrollmentService.deleteEnrollment(enrollmentId);
                JOptionPane.showMessageDialog(this, "Đã hủy đăng ký.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi hủy đăng ký: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper class for ComboBox items
    private static class ClassItem {
        private final Classes classEntity;

        public ClassItem(Classes classEntity) {
            this.classEntity = classEntity;
        }

        public Classes getClassEntity() {
            return classEntity;
        }

        @Override
        public String toString() {
            String courseName = classEntity.getCourse() != null ? classEntity.getCourse().getCourseName() : "N/A";
            String teacherName = classEntity.getTeacher() != null ? classEntity.getTeacher().getFullName() : "N/A";
            return classEntity.getClassName() + " - " + courseName + " (GV: " + teacherName + ")";
        }
    }

    private void fillForm(Student s) {
        if (s == null) return;
        txtFullName.setText(s.getFullName() != null ? s.getFullName() : "");
        if (s.getDateOfBirth() != null && !s.getDateOfBirth().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(s.getDateOfBirth());
                dateChooserBirth.setDate(date);
            } catch (Exception e) {
                dateChooserBirth.setDate(null);
            }
        }
        if (s.getGender() != null) cmbGender.setSelectedItem(s.getGender());
        txtPhone.setText(s.getPhone() != null ? s.getPhone() : "");
        txtEmail.setText(s.getEmail() != null ? s.getEmail() : "");
        txtAddress.setText(s.getAddress() != null ? s.getAddress() : "");
        if (s.getStatus() != null) cmbStatus.setSelectedItem(s.getStatus());
    }

    private Student getStudentFromForm() {
        Student s = new Student();
        s.setFullName(txtFullName.getText().trim());
        if (dateChooserBirth.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            s.setDateOfBirth(sdf.format(dateChooserBirth.getDate()));
        } else {
            s.setDateOfBirth("");
        }
        s.setGender((String) cmbGender.getSelectedItem());
        s.setPhone(txtPhone.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setAddress(txtAddress.getText().trim());
        s.setStatus((String) cmbStatus.getSelectedItem());
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
        if (dateChooserBirth.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return false;
        }
        if (!validatePhone()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ. Phải có 10-11 chữ số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        if (!validateEmail()) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
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
