package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.EnrollmentApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class EnrollmentManagerPanel extends JPanel {
    private final EnrollmentApiService apiService;
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;

    private JTextField txtStudentId;
    private JTextField txtClassId;
    private JTextField txtEnrollmentDate;
    private JComboBox<String> cmbStatus;
    private JComboBox<String> cmbResult;
    private JTextField txtSearchId;

    private Long selectedEnrollmentId = null;

    public EnrollmentManagerPanel() {
        apiService = new EnrollmentApiService();
        initComponents();
        loadEnrollments();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm theo Enrollment ID:"));
        txtSearchId = new JTextField(20);
        searchPanel.add(txtSearchId);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchEnrollmentById());
        searchPanel.add(btnSearch);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadEnrollments());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Student ID", "Student Name", "Class ID", "Class Name", "Enrollment Date", "Status", "Result"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setRowHeight(30);
        enrollmentTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && enrollmentTable.getSelectedRow() != -1) {
                loadSelectedEnrollment();
            }
        });

        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10),
                UIManager.getBorder("ScrollPane.border")));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin ghi danh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(20);
        txtClassId = new JTextField(20);
        txtEnrollmentDate = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"Registered", "Studying", "Completed", "Cancelled"});
        cmbResult = new JComboBox<>(new String[]{"Pending", "Pass", "Fail"});

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Class ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtClassId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Ngày ghi danh (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEnrollmentDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Kết quả:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbResult, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createEnrollment());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateEnrollment());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteEnrollment());
        JButton btnClear = new JButton("Xóa trắng bảng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadEnrollments() {
        try {
            updateTable(apiService.getAllEnrollments());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách ghi danh: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEnrollmentById() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) {
            loadEnrollments();
            return;
        }

        try {
            Long id = Long.parseLong(idText);
            Enrollment enrollment = apiService.getEnrollmentById(id);
            updateTable(Collections.singletonList(enrollment));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enrollment ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy enrollment: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Enrollment> enrollments) {
        tableModel.setRowCount(0);
        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();
            Classes classEntity = enrollment.getClassEntity();

            Object[] row = {
                    enrollment.getEnrollmentId(),
                    student != null ? student.getId() : null,
                    student != null ? student.getFullName() : "",
                    classEntity != null ? classEntity.getClassId() : null,
                    classEntity != null ? classEntity.getClassName() : "",
                    enrollment.getEnrollmentDate(),
                    enrollment.getStatus(),
                    enrollment.getResult()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 0);
        selectedEnrollmentId = parseLongValue(idValue);

        txtStudentId.setText(valueToString(tableModel.getValueAt(selectedRow, 1)));
        txtClassId.setText(valueToString(tableModel.getValueAt(selectedRow, 3)));
        txtEnrollmentDate.setText(valueToString(tableModel.getValueAt(selectedRow, 5)));

        Object statusValue = tableModel.getValueAt(selectedRow, 6);
        cmbStatus.setSelectedItem(statusValue != null ? statusValue.toString() : "Registered");

        Object resultValue = tableModel.getValueAt(selectedRow, 7);
        cmbResult.setSelectedItem(resultValue != null ? resultValue.toString() : "Pending");
    }

    private void createEnrollment() {
        if (!validateForm()) {
            return;
        }

        try {
            apiService.createEnrollment(getEnrollmentFromForm());
            JOptionPane.showMessageDialog(this, "Thêm enrollment thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm enrollment: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEnrollment() {
        if (selectedEnrollmentId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn enrollment cần cập nhật!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            apiService.updateEnrollment(selectedEnrollmentId, getEnrollmentFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật enrollment thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật enrollment: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEnrollment() {
        if (selectedEnrollmentId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn enrollment cần xóa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa enrollment này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteEnrollment(selectedEnrollmentId);
                JOptionPane.showMessageDialog(this, "Xóa enrollment thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa enrollment: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Enrollment getEnrollmentFromForm() {
        Enrollment enrollment = new Enrollment();

        Student student = new Student();
        student.setId(Long.parseLong(txtStudentId.getText().trim()));
        enrollment.setStudent(student);

        Classes classEntity = new Classes();
        classEntity.setClassId(Long.parseLong(txtClassId.getText().trim()));
        enrollment.setClassEntity(classEntity);

        enrollment.setEnrollmentDate(txtEnrollmentDate.getText().trim());
        enrollment.setStatus((String) cmbStatus.getSelectedItem());
        enrollment.setResult((String) cmbResult.getSelectedItem());

        return enrollment;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtClassId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Class ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtEnrollmentDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày ghi danh!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Long.parseLong(txtStudentId.getText().trim());
            Long.parseLong(txtClassId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID và Class ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        selectedEnrollmentId = null;
        txtStudentId.setText("");
        txtClassId.setText("");
        txtEnrollmentDate.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbResult.setSelectedIndex(0);
        enrollmentTable.clearSelection();
    }

    private Long parseLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String valueToString(Object value) {
        return value == null ? "" : value.toString();
    }
}
