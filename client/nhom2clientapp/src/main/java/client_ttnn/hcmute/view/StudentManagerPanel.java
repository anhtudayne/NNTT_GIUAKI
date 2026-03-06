package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.StudentApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentManagerPanel extends JPanel {
    private final StudentApiService apiService;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtFullName;
    private JTextField txtDateOfBirth;
    private JComboBox<String> cmbGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JComboBox<String> cmbStatus;
    private JTextField txtSearch;
    
    private Long selectedStudentId = null;

    public StudentManagerPanel() {
        apiService = new StudentApiService();
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm kiếm theo tên:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchStudents());
        searchPanel.add(btnSearch);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadStudents());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Họ và tên", "Ngày sinh", "Giới tính", "Điện thoại", "Email", "Địa chỉ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                loadSelectedStudent();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        txtFullName = new JTextField(20);
        txtDateOfBirth = new JTextField(20);
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtAddress = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Ngày sinh (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDateOfBirth, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbGender, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Điện thoại:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtAddress, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createStudent());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateStudent());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteStudent());
        JButton btnClear = new JButton("Xóa trắng bảng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadStudents() {
        try {
            List<Student> students = apiService.getAllStudents();
            updateTable(students);
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách sinh viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudents() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadStudents();
            return;
        }
        
        try {
            List<Student> students = apiService.searchByName(searchText);
            updateTable(students);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm sinh viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            Object[] row = {
                student.getId(),
                student.getFullName(),
                student.getDateOfBirth(),
                student.getGender(),
                student.getPhone(),
                student.getEmail(),
                student.getAddress(),
                student.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            // Check null pointers for attributes
            selectedStudentId = (Long) tableModel.getValueAt(selectedRow, 0);
            System.out.println("Selected Student ID: " + selectedStudentId);
            txtFullName.setText(tableModel.getValueAt(selectedRow, 1) != null ? (String) tableModel.getValueAt(selectedRow, 1) : "");
            txtDateOfBirth.setText(tableModel.getValueAt(selectedRow, 2) != null ? (String) tableModel.getValueAt(selectedRow, 2) : "");
            cmbGender.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
            txtPhone.setText(tableModel.getValueAt(selectedRow, 4) != null ? (String) tableModel.getValueAt(selectedRow, 4) : "");
            txtEmail.setText(tableModel.getValueAt(selectedRow, 5) != null ? (String) tableModel.getValueAt(selectedRow, 5) : "");
            txtAddress.setText(tableModel.getValueAt(selectedRow, 6) != null ? (String) tableModel.getValueAt(selectedRow, 6) : "");
            cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 7));
        }
    }

    private void createStudent() {
        if (!validateForm()) {
            return;
        }

        Student student = getStudentFromForm();
        try {
            apiService.createStudent(student);
            JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadStudents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm sinh viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần cập nhật!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        Student student = getStudentFromForm();
        try {
            apiService.updateStudent(selectedStudentId, student);
            JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadStudents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sinh viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa sinh viên này?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteStudent(selectedStudentId);
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sinh viên: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Student getStudentFromForm() {
        Student student = new Student();
        student.setFullName(txtFullName.getText().trim());
        student.setDateOfBirth(txtDateOfBirth.getText().trim());
        student.setGender((String) cmbGender.getSelectedItem());
        student.setPhone(txtPhone.getText().trim());
        student.setEmail(txtEmail.getText().trim());
        student.setAddress(txtAddress.getText().trim());
        student.setStatus((String) cmbStatus.getSelectedItem());
        return student;
    }

    private boolean validateForm() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDateOfBirth.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày sinh!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedStudentId = null;
        txtFullName.setText("");
        txtDateOfBirth.setText("");
        cmbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        cmbStatus.setSelectedIndex(0);
        studentTable.clearSelection();
    }
}
