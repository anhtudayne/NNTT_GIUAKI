package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.service.CourseApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseManagerPanel extends JPanel {
    private final CourseApiService apiService;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtCourseName;
    private JTextField txtDescription;
    private JComboBox<String> cmbLevel;
    private JTextField txtDuration;
    private JTextField txtFee;
    private JComboBox<String> cmbStatus;
    private JTextField txtSearch;
    
    private Long selectedCourseId = null;

    public CourseManagerPanel() {
        apiService = new CourseApiService();
        initComponents();
        loadCourses();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm kiếm khóa học:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchCourses());
        searchPanel.add(btnSearch);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadCourses());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Tên khóa học", "Mô tả", "Cấp độ", "Thời lượng (giờ)", "Học phí (VND)", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && courseTable.getSelectedRow() != -1) {
                loadSelectedCourse();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khóa học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        txtCourseName = new JTextField(20);
        txtDescription = new JTextField(20);
        cmbLevel = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        txtDuration = new JTextField(20);
        txtFee = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên khóa học:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCourseName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDescription, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Cấp độ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbLevel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Thời lượng (giờ):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDuration, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Học phí (VND):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFee, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createCourse());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateCourse());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteCourse());
        JButton btnClear = new JButton("Xóa trắng bảng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadCourses() {
        try {
            List<Course> courses = apiService.getAllCourses();
            updateTable(courses);
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách khóa học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCourses() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadCourses();
            return;
        }
        
        try {
            List<Course> courses = apiService.searchByName(searchText);
            updateTable(courses);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm khóa học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Course> courses) {
        tableModel.setRowCount(0);
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseName(),
                course.getDescription(),
                course.getLevel(),
                course.getDuration(),
                String.format("%.0f", course.getFee() != null ? course.getFee() : 0),
                course.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            // Handle ID conversion - can be Integer or Long
            Object idValue = tableModel.getValueAt(selectedRow, 0);
            System.out.println("Course ID Value: " + idValue + ", Type: " + (idValue != null ? idValue.getClass().getName() : "null"));
            
            if (idValue instanceof Long) {
                selectedCourseId = (Long) idValue;
            } else if (idValue instanceof Integer) {
                selectedCourseId = ((Integer) idValue).longValue();
            } else if (idValue != null) {
                try {
                    selectedCourseId = Long.parseLong(idValue.toString());
                } catch (NumberFormatException e) {
                    System.err.println("Cannot parse Course ID: " + idValue);
                    selectedCourseId = null;
                }
            } else {
                selectedCourseId = null;
            }
            
            System.out.println("Selected Course ID: " + selectedCourseId);
            txtCourseName.setText(tableModel.getValueAt(selectedRow, 1) != null ? (String) tableModel.getValueAt(selectedRow, 1) : "");
            txtDescription.setText(tableModel.getValueAt(selectedRow, 2) != null ? (String) tableModel.getValueAt(selectedRow, 2) : "");
            cmbLevel.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
            txtDuration.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
            txtFee.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
            cmbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 6));
        }
    }

    private void createCourse() {
        if (!validateForm()) {
            return;
        }

        Course course = getCourseFromForm();
        try {
            apiService.createCourse(course);
            JOptionPane.showMessageDialog(this, "Thêm khóa học thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCourses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khóa học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourse() {
        if (selectedCourseId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học cần cập nhật!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        Course course = getCourseFromForm();
        try {
            apiService.updateCourse(selectedCourseId, course);
            JOptionPane.showMessageDialog(this, "Cập nhật khóa học thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCourses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khóa học: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        if (selectedCourseId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học cần xóa!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa khóa học này?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteCourse(selectedCourseId);
                JOptionPane.showMessageDialog(this, "Xóa khóa học thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khóa học: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Course getCourseFromForm() {
        Course course = new Course();
        course.setCourseName(txtCourseName.getText().trim());
        course.setDescription(txtDescription.getText().trim());
        course.setLevel((String) cmbLevel.getSelectedItem());
        try {
            course.setDuration(Integer.parseInt(txtDuration.getText().trim()));
        } catch (NumberFormatException e) {
            course.setDuration(0);
        }
        try {
            course.setFee(Double.parseDouble(txtFee.getText().trim()));
        } catch (NumberFormatException e) {
            course.setFee(0.0);
        }
        course.setStatus((String) cmbStatus.getSelectedItem());
        return course;
    }

    private boolean validateForm() {
        if (txtCourseName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khóa học!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDescription.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mô tả!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDuration.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thời lượng!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtFee.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập học phí!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedCourseId = null;
        txtCourseName.setText("");
        txtDescription.setText("");
        cmbLevel.setSelectedIndex(0);
        txtDuration.setText("");
        txtFee.setText("");
        cmbStatus.setSelectedIndex(0);
        courseTable.clearSelection();
    }
}
