package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.TeacherApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherManagerPanel extends JPanel {
    private final TeacherApiService apiService;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtSpecialty;
    private JTextField txtHireDate;
    private JComboBox<String> cmbStatus;
    private JTextField txtSearch;
    
    // Checkbox Lọc GV đang hoạt động (Demo Stream API bên Backend)
    private JCheckBox chkOnlyActive;
    
    private Integer selectedTeacherId = null;

    public TeacherManagerPanel() {
        apiService = new TeacherApiService();
        initComponents();
        loadTeachers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Nút lọc
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm kiếm GV:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchTeachers());
        searchPanel.add(btnSearch);
        
        chkOnlyActive = new JCheckBox("Chỉ Giảng viên Active");
        chkOnlyActive.addActionListener(e -> {
            if (chkOnlyActive.isSelected()) {
                loadActiveTeachers();
            } else {
                loadTeachers();
            }
        });
        searchPanel.add(chkOnlyActive);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            chkOnlyActive.setSelected(false);
            loadTeachers();
        });
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columns = {"ID", "Họ và tên", "Điện thoại", "Email", "Chuyên môn", "Ngày vào làm", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);
        teacherTable.setRowHeight(30); // Tăng chiều cao dòng cho dễ nhìn
        teacherTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        // teacherTable.setShowVerticalLines(false); // Tuỳ chọn ẩn gạch dọc
        
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && teacherTable.getSelectedRow() != -1) {
                loadSelectedTeacher();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        // Tạo khoảng trắng giữa Panel Search và Bảng
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Form điền thông tin bên phải màn hình
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Điền Giảng Viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFullName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtSpecialty = new JTextField(20);
        txtHireDate = new JTextField(20); 
        cmbStatus = new JComboBox<>(new String[]{"Active", "OnLeave", "Inactive"});

        int rowCount = 0;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1; formPanel.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Điện thoại:"), gbc);
        gbc.gridx = 1; formPanel.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; formPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Chuyên môn (VD: IELTS):"), gbc);
        gbc.gridx = 1; formPanel.add(txtSpecialty, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Ngày tạo (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; formPanel.add(txtHireDate, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; formPanel.add(cmbStatus, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createTeacher());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateTeacher());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteTeacher());
        JButton btnClear = new JButton("Xóa Trắng Bảng Form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadTeachers() {
        try {
            updateTable(apiService.getAllTeachers());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải Danh sách GV: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadActiveTeachers() {
        try {
            updateTable(apiService.getActiveTeachers());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải GV Active: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTeachers() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadTeachers();
            return;
        }
        try {
            updateTable(apiService.searchByName(searchText));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Teacher> teachers) {
        tableModel.setRowCount(0);
        for (Teacher teacher : teachers) {
            Object[] row = {
                teacher.getTeacherId(),
                teacher.getFullName(),
                teacher.getPhone(),
                teacher.getEmail(),
                teacher.getSpecialty(),
                teacher.getHireDate(),
                teacher.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow != -1) {
            // Check null pointers for attributes
            selectedTeacherId = (Integer) tableModel.getValueAt(selectedRow, 0);
            txtFullName.setText(tableModel.getValueAt(selectedRow, 1) != null ? (String) tableModel.getValueAt(selectedRow, 1) : "");
            txtPhone.setText(tableModel.getValueAt(selectedRow, 2) != null ? (String) tableModel.getValueAt(selectedRow, 2) : "");
            txtEmail.setText(tableModel.getValueAt(selectedRow, 3) != null ? (String) tableModel.getValueAt(selectedRow, 3) : "");
            txtSpecialty.setText(tableModel.getValueAt(selectedRow, 4) != null ? (String) tableModel.getValueAt(selectedRow, 4) : "");
            // Date format processing can be tricky, cast toString properly 
            Object dateObj = tableModel.getValueAt(selectedRow, 5);
            txtHireDate.setText(dateObj != null ? dateObj.toString() : "");
            cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 6));
        }
    }

    private void createTeacher() {
        if (!validateForm()) return;
        try {
            apiService.createTeacher(getTeacherFromForm());
            JOptionPane.showMessageDialog(this, "Thêm GV thành công!");
            loadTeachers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeacher() {
        if (selectedTeacherId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Giảng viên.");
            return;
        }
        if (!validateForm()) return;
        try {
            apiService.updateTeacher(selectedTeacherId, getTeacherFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadTeachers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTeacher() {
        if (selectedTeacherId == null) {
            JOptionPane.showMessageDialog(this, "Chưa có chọn dòng cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa Giảng viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteTeacher(selectedTeacherId);
                JOptionPane.showMessageDialog(this, "Xóa thành công.");
                loadTeachers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Teacher getTeacherFromForm() {
        Teacher teacher = new Teacher();
        teacher.setFullName(txtFullName.getText().trim());
        teacher.setPhone(txtPhone.getText().trim());
        teacher.setEmail(txtEmail.getText().trim());
        teacher.setSpecialty(txtSpecialty.getText().trim());
        teacher.setHireDate(txtHireDate.getText().trim());
        teacher.setStatus((String) cmbStatus.getSelectedItem());
        return teacher;
    }

    private boolean validateForm() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cần nhập Họ Tên!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedTeacherId = null;
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtSpecialty.setText("");
        txtHireDate.setText("");
        cmbStatus.setSelectedIndex(0);
        teacherTable.clearSelection();
    }
}
