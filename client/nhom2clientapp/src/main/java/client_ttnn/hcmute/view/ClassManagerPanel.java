package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.ClassesApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClassManagerPanel extends JPanel {
    private final ClassesApiService apiService;
    private JTable classTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtClassName;
    private JTextField txtCourseId;
    private JTextField txtTeacherId;
    private JTextField txtRoomId;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtMaxStudent;
    private JComboBox<String> cmbStatus;
    private JTextField txtSearch;
    
    private Long selectedClassId = null;

    public ClassManagerPanel() {
        apiService = new ClassesApiService();
        initComponents();
        loadClasses();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm kiếm lớp học:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchClasses());
        searchPanel.add(btnSearch);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadClasses());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Tên lớp", "Khóa học", "Giảng viên", "Phòng", "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số tối đa", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classTable = new JTable(tableModel);
        classTable.setRowHeight(30);
        classTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && classTable.getSelectedRow() != -1) {
                loadSelectedClass();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lớp học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        txtClassName = new JTextField(20);
        txtCourseId = new JTextField(20);
        txtTeacherId = new JTextField(20);
        txtRoomId = new JTextField(20);
        txtStartDate = new JTextField(20);
        txtEndDate = new JTextField(20);
        txtMaxStudent = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Ongoing", "Completed", "Cancelled"});

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên lớp:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtClassName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Khóa học ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCourseId, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giảng viên ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTeacherId, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phòng ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtRoomId, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEndDate, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Sĩ số tối đa:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMaxStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createClass());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateClass());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteClass());
        JButton btnClear = new JButton("Xóa trắng bảng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadClasses() {
        try {
            List<Classes> classes = apiService.getAllClasses();
            updateTable(classes);
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách lớp học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchClasses() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadClasses();
            return;
        }
        
        try {
            List<Classes> classes = apiService.searchByName(searchText);
            updateTable(classes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm lớp học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Classes> classes) {
        tableModel.setRowCount(0);
        for (Classes c : classes) {
            Object[] row = {
                c.getClassId(),
                c.getClassName(),
                c.getCourse() != null ? c.getCourse().getCourseId() : "",
                c.getTeacher() != null ? c.getTeacher().getFullName() : "",
                c.getRoom() != null ? c.getRoom().getRoomName() : "",
                c.getStartDate(),
                c.getEndDate(),
                c.getMaxStudent(),
                c.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow != -1) {
            // Handle ID conversion
            Object idValue = tableModel.getValueAt(selectedRow, 0);
            System.out.println("Class ID Value: " + idValue + ", Type: " + (idValue != null ? idValue.getClass().getName() : "null"));
            
            if (idValue instanceof Long) {
                selectedClassId = (Long) idValue;
            } else if (idValue instanceof Integer) {
                selectedClassId = ((Integer) idValue).longValue();
            } else if (idValue != null) {
                try {
                    selectedClassId = Long.parseLong(idValue.toString());
                } catch (NumberFormatException e) {
                    System.err.println("Cannot parse Class ID: " + idValue);
                    selectedClassId = null;
                }
            } else {
                selectedClassId = null;
            }
            
            System.out.println("Selected Class ID: " + selectedClassId);
            txtClassName.setText(tableModel.getValueAt(selectedRow, 1) != null ? (String) tableModel.getValueAt(selectedRow, 1) : "");
            txtCourseId.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
            txtTeacherId.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
            txtRoomId.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
            txtStartDate.setText(tableModel.getValueAt(selectedRow, 5) != null ? (String) tableModel.getValueAt(selectedRow, 5) : "");
            txtEndDate.setText(tableModel.getValueAt(selectedRow, 6) != null ? (String) tableModel.getValueAt(selectedRow, 6) : "");
            txtMaxStudent.setText(tableModel.getValueAt(selectedRow, 7) != null ? tableModel.getValueAt(selectedRow, 7).toString() : "");
            cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 8));
        }
    }

    private void createClass() {
        if (!validateForm()) {
            return;
        }

        Classes classes = getClassFromForm();
        try {
            apiService.createClass(classes);
            JOptionPane.showMessageDialog(this, "Thêm lớp học thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadClasses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm lớp học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClass() {
        if (selectedClassId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học cần cập nhật!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        Classes classes = getClassFromForm();
        try {
            apiService.updateClass(selectedClassId, classes);
            JOptionPane.showMessageDialog(this, "Cập nhật lớp học thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadClasses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật lớp học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClass() {
        if (selectedClassId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học cần xóa!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa lớp học này?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteClass(selectedClassId);
                JOptionPane.showMessageDialog(this, "Xóa lớp học thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadClasses();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa lớp học: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Classes getClassFromForm() {
        Classes classes = new Classes();
        classes.setClassName(txtClassName.getText().trim());
        
        // Create Course object
        if (!txtCourseId.getText().trim().isEmpty()) {
            Course course = new Course();
            try {
                course.setCourseId(Long.parseLong(txtCourseId.getText().trim()));
            } catch (NumberFormatException e) {
                course.setCourseId(0L);
            }
            classes.setCourse(course);
        }
        
        // Create Teacher object
        if (!txtTeacherId.getText().trim().isEmpty()) {
            Teacher teacher = new Teacher();
            try {
                teacher.setTeacherId(Integer.parseInt(txtTeacherId.getText().trim()));
            } catch (NumberFormatException e) {
                teacher.setTeacherId(0);
            }
            classes.setTeacher(teacher);
        }
        
        // Create Room object
        if (!txtRoomId.getText().trim().isEmpty()) {
            Room room = new Room();
            try {
                room.setRoomId(Long.parseLong(txtRoomId.getText().trim()));
            } catch (NumberFormatException e) {
                room.setRoomId(0L);
            }
            classes.setRoom(room);
        }
        
        classes.setStartDate(txtStartDate.getText().trim());
        classes.setEndDate(txtEndDate.getText().trim());
        
        try {
            classes.setMaxStudent(Integer.parseInt(txtMaxStudent.getText().trim()));
        } catch (NumberFormatException e) {
            classes.setMaxStudent(0);
        }
        
        classes.setStatus((String) cmbStatus.getSelectedItem());
        return classes;
    }

    private boolean validateForm() {
        if (txtClassName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên lớp!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtCourseId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Khóa học ID!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtTeacherId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Giảng viên ID!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtRoomId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Phòng ID!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtStartDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtEndDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày kết thúc!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtMaxStudent.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập sĩ số tối đa!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedClassId = null;
        txtClassName.setText("");
        txtCourseId.setText("");
        txtTeacherId.setText("");
        txtRoomId.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        txtMaxStudent.setText("");
        cmbStatus.setSelectedIndex(0);
        classTable.clearSelection();
    }
}
