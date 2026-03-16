package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.TeacherApiService;
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

public class TeacherFormDialog extends JDialog {
    private final TeacherApiService apiService;
    private final boolean isEditMode;
    private final Teacher initialTeacher;
    private final Runnable onSuccess;

    private JTextField txtFullName, txtPhone, txtEmail, txtSpecialty;
    private JDateChooser dateChooserHire;
    private JComboBox<String> cmbStatus;
    
    // Classes tab
    private JTable tblClasses;
    private DefaultTableModel classesTableModel;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,11}$");

    public TeacherFormDialog(Window owner, TeacherApiService apiService,
                             boolean isEditMode, Teacher initialTeacher, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật giảng viên" : "Thêm giảng viên mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialTeacher = initialTeacher;
        this.onSuccess = onSuccess;
        setSize(1000, 720);
        setMinimumSize(new Dimension(800, 620));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialTeacher != null) {
            fillForm(initialTeacher);
            if (isEditMode && initialTeacher.getTeacherId() != null) {
                loadTeacherClasses();
            }
        }
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setBackground(new Color(245, 247, 250));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(155, 89, 182));
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel(isEditMode ? "Cập nhật giảng viên" : "Thêm giảng viên mới");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        content.add(titlePanel, BorderLayout.NORTH);
        
        // Create Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        
        // Tab 1: Basic Info
        JPanel infoTab = createBasicInfoPanel();
        tabbedPane.addTab("📋 Thông tin cơ bản", infoTab);
        
        // Tab 2: Classes (only in edit mode)
        if (isEditMode && initialTeacher != null) {
            JPanel classesTab = createClassesPanel();
            tabbedPane.addTab("🎓 Lớp học giảng dạy", classesTab);
        }
        
        content.add(tabbedPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(new Color(245, 247, 250));
        
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
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        p.add(lbl, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        p.add(field, gbcField);
    }

    private void fillForm(Teacher t) {
        if (t == null) return;
        txtFullName.setText(str(t.getFullName()));
        txtPhone.setText(str(t.getPhone()));
        txtEmail.setText(str(t.getEmail()));
        txtSpecialty.setText(str(t.getSpecialty()));
        if (t.getHireDate() != null && !t.getHireDate().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(t.getHireDate());
                dateChooserHire.setDate(date);
            } catch (Exception e) {
                dateChooserHire.setDate(null);
            }
        }
        if (t.getStatus() != null) cmbStatus.setSelectedItem(t.getStatus());
    }

    private Teacher getFromForm() {
        Teacher t = new Teacher();
        t.setFullName(txtFullName.getText().trim());
        t.setPhone(txtPhone.getText().trim());
        t.setEmail(txtEmail.getText().trim());
        t.setSpecialty(txtSpecialty.getText().trim());
        if (dateChooserHire.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            t.setHireDate(sdf.format(dateChooserHire.getDate()));
        } else {
            t.setHireDate("");
        }
        t.setStatus((String) cmbStatus.getSelectedItem());
        return t;
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

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 10, 15, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thông tin giảng viên",
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
        txtFullName = new JTextField(cols);
        txtFullName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        txtPhone = new JTextField(cols);
        txtPhone.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtPhone.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePhone();
            }
        });
        
        txtEmail = new JTextField(cols);
        txtEmail.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtEmail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });
        
        txtSpecialty = new JTextField(cols);
        txtSpecialty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        dateChooserHire = new JDateChooser();
        dateChooserHire.setDateFormatString("yyyy-MM-dd");
        dateChooserHire.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        dateChooserHire.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, 30));
        
        cmbStatus = new JComboBox<>(new String[]{"Active", "OnLeave", "Inactive"});
        cmbStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cmbStatus.setPreferredSize(new Dimension(txtFullName.getPreferredSize().width, 30));

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Họ và tên:", txtFullName);
        addRow(formPanel, gbc, gbcField, row++, "Điện thoại:", txtPhone);
        addRow(formPanel, gbc, gbcField, row++, "Email:", txtEmail);
        addRow(formPanel, gbc, gbcField, row++, "Chuyên môn (VD: IELTS):", txtSpecialty);
        addRow(formPanel, gbc, gbcField, row++, "Ngày vào làm:", dateChooserHire);
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Table Header
        JLabel headerLabel = new JLabel("🎓 Danh sách lớp học giảng dạy");
        headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        headerLabel.setForeground(new Color(155, 89, 182));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Tên lớp", "Khóa học", "Phòng học", "Sức chứa", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        classesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblClasses = new JTable(classesTableModel);
        tblClasses.setRowHeight(32);
        tblClasses.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblClasses.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tblClasses.getTableHeader().setBackground(new Color(52, 73, 94));
        tblClasses.getTableHeader().setForeground(Color.WHITE);
        tblClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblClasses.setSelectionBackground(new Color(195, 155, 211));
        tblClasses.setSelectionForeground(new Color(44, 62, 80));
        tblClasses.setGridColor(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tblClasses);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadTeacherClasses() {
        if (initialTeacher == null || initialTeacher.getTeacherId() == null) return;
        
        try {
            List<Classes> classes = apiService.getTeacherClasses(initialTeacher.getTeacherId());
            classesTableModel.setRowCount(0);
            
            for (Classes cls : classes) {
                String courseName = cls.getCourse() != null ? cls.getCourse().getCourseName() : "N/A";
                String roomName = cls.getRoom() != null ? cls.getRoom().getRoomName() : "N/A";
                Integer capacity = cls.getRoom() != null ? cls.getRoom().getCapacity() : null;
                
                classesTableModel.addRow(new Object[]{
                    cls.getClassId(),
                    cls.getClassName(),
                    courseName,
                    roomName,
                    capacity,
                    cls.getStartDate(),
                    cls.getEndDate(),
                    cls.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                "Lỗi khi tải danh sách lớp: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
