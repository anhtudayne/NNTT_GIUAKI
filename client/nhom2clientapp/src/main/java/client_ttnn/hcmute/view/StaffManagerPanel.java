package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Staff;
import client_ttnn.hcmute.service.StaffApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffManagerPanel extends JPanel {
    private final StaffApiService apiService;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JTextField txtSearch;
    
    // Checkbox Lọc Nhân sự theo vai trò (Demo Stream API bên Backend)
    private JComboBox<String> cmbFilterRole;
    
    private Integer selectedStaffId = null;

    public StaffManagerPanel() {
        apiService = new StaffApiService();
        initComponents();
        loadStaffList();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Nút lọc
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm kiếm (Tên):"));
        txtSearch = new JTextField(15);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchStaffs());
        searchPanel.add(btnSearch);
        
        searchPanel.add(new JLabel("  |  Lọc Role:"));
        cmbFilterRole = new JComboBox<>(new String[]{"Tất cả", "Admin", "Consultant", "Accountant"});
        cmbFilterRole.addActionListener(e -> filterStaffByRole());
        searchPanel.add(cmbFilterRole);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            cmbFilterRole.setSelectedIndex(0);
            loadStaffList();
        });
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columns = {"ID", "Họ và tên", "Chức vụ", "Điện thoại", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setRowHeight(30);
        staffTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && staffTable.getSelectedRow() != -1) {
                loadSelectedStaff();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Form điền thông tin bên phải màn hình
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Nhân sự"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFullName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        cmbRole = new JComboBox<>(new String[]{"Admin", "Consultant", "Accountant"});

        int rowCount = 0;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 1; formPanel.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Chức vụ (Role):"), gbc);
        gbc.gridx = 1; formPanel.add(cmbRole, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Điện thoại:"), gbc);
        gbc.gridx = 1; formPanel.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; formPanel.add(txtEmail, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createStaff());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateStaff());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteStaff());
        JButton btnClear = new JButton("Xóa Form");
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

    private void loadStaffList() {
        try {
            txtSearch.setText("");
            updateTable(apiService.getAllStaff());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải DS Nhân sự: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterStaffByRole() {
        String selectedRole = (String) cmbFilterRole.getSelectedItem();
        if ("Tất cả".equals(selectedRole)) {
            loadStaffList();
            return;
        }
        try {
            updateTable(apiService.getStaffByRole(selectedRole));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc Role: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStaffs() {
        String searchText = txtSearch.getText().trim();
        cmbFilterRole.setSelectedIndex(0); // Reset filter role
        if (searchText.isEmpty()) {
            loadStaffList();
            return;
        }
        try {
            updateTable(apiService.searchStaffByName(searchText));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Staff> staffs) {
        tableModel.setRowCount(0);
        for (Staff st : staffs) {
            Object[] row = {
                st.getStaffId(),
                st.getFullName(),
                st.getRole(),
                st.getPhone(),
                st.getEmail()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedStaffId = (Integer) tableModel.getValueAt(selectedRow, 0);
            txtFullName.setText(tableModel.getValueAt(selectedRow, 1) != null ? (String) tableModel.getValueAt(selectedRow, 1) : "");
            cmbRole.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
            txtPhone.setText(tableModel.getValueAt(selectedRow, 3) != null ? (String) tableModel.getValueAt(selectedRow, 3) : "");
            txtEmail.setText(tableModel.getValueAt(selectedRow, 4) != null ? (String) tableModel.getValueAt(selectedRow, 4) : "");
        }
    }

    private void createStaff() {
        if (!validateForm()) return;
        try {
            apiService.createStaff(getStaffFromForm());
            JOptionPane.showMessageDialog(this, "Thêm Nhân sự thành công!");
            loadStaffList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaff() {
        if (selectedStaffId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhân sự.");
            return;
        }
        if (!validateForm()) return;
        try {
            Staff st = getStaffFromForm();
            st.setStaffId(selectedStaffId);
            apiService.updateStaff(st);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadStaffList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaff() {
        if (selectedStaffId == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn Nhân sự cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa Nhân sự này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteStaff(selectedStaffId);
                JOptionPane.showMessageDialog(this, "Xóa thành công.");
                loadStaffList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Staff getStaffFromForm() {
        Staff st = new Staff();
        st.setFullName(txtFullName.getText().trim());
        st.setRole((String) cmbRole.getSelectedItem());
        st.setPhone(txtPhone.getText().trim());
        st.setEmail(txtEmail.getText().trim());
        return st;
    }

    private boolean validateForm() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cần nhập Họ Tên!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedStaffId = null;
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cmbRole.setSelectedIndex(0);
        staffTable.clearSelection();
    }
}
