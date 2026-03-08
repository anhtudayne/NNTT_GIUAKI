package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Staff;
import client_ttnn.hcmute.service.StaffApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffManagerPanel extends JPanel {

    private final StaffApiService apiService;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterRole;
    private JButton btnEdit;
    private JButton btnDelete;
    private Integer selectedStaffId = null;

    public StaffManagerPanel() {
        apiService = new StaffApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadStaffList();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(new Color(236, 240, 241));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 126, 34)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblSearch = new JLabel("👤 Tìm theo tên:");
        lblSearch.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        toolbarPanel.add(lblSearch);
        txtSearch = new JTextField(22);
        txtSearch.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        toolbarPanel.add(txtSearch);
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(230, 126, 34));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> searchStaffByName());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();

        toolbarPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        JLabel lblRole = new JLabel("Chức vụ:");
        lblRole.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        toolbarPanel.add(lblRole);
        cmbFilterRole = new JComboBox<>(new String[]{"Tất cả", "Admin", "Consultant", "Accountant"});
        cmbFilterRole.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cmbFilterRole.addActionListener(e -> filterStaffByRole());
        toolbarPanel.add(cmbFilterRole);

        JButton btnRefresh = new JButton("⟳ Làm mới");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadStaffList());
        toolbarPanel.add(btnRefresh);

        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Họ và tên", "Chức vụ", "Điện thoại", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setRowHeight(36);
        staffTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        staffTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        staffTable.getTableHeader().setBackground(new Color(52, 73, 94));
        staffTable.getTableHeader().setForeground(Color.WHITE);
        staffTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setSelectionBackground(new Color(243, 156, 18));
        staffTable.setSelectionForeground(Color.WHITE);
        staffTable.setShowGrid(true);
        staffTable.setGridColor(new Color(220, 220, 220));
        staffTable.setIntercellSpacing(new Dimension(1, 1));

        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateSelectionState();
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));

        Dimension btnSize = new Dimension(130, 40);
        JButton btnAdd = new JButton("➕ Thêm");
        btnAdd.setPreferredSize(btnSize);
        btnAdd.setMinimumSize(btnSize);
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = new JButton("✏️ Xem/Sửa");
        btnEdit.setPreferredSize(btnSize);
        btnEdit.setMinimumSize(btnSize);
        btnEdit.setEnabled(false);
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorderPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> openEditDialog());

        btnDelete = new JButton("🗑️ Xóa");
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setMinimumSize(btnSize);
        btnDelete.setEnabled(false);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteStaff());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = staffTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedStaffId = idVal != null ? ((Number) idVal).intValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedStaffId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        StaffFormDialog dlg = new StaffFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                false,
                null,
                this::loadStaffList
        );
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedStaffId == null) return;
        Staff selected = getSelectedStaffFromTable();
        if (selected == null) return;
        StaffFormDialog dlg = new StaffFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                true,
                selected,
                this::loadStaffList
        );
        dlg.setVisible(true);
    }

    private Staff getSelectedStaffFromTable() {
        int row = staffTable.getSelectedRow();
        if (row < 0) return null;
        Staff s = new Staff();
        s.setStaffId(selectedStaffId);
        s.setFullName(str(tableModel.getValueAt(row, 1)));
        s.setRole(str(tableModel.getValueAt(row, 2)));
        s.setPhone(str(tableModel.getValueAt(row, 3)));
        s.setEmail(str(tableModel.getValueAt(row, 4)));
        return s;
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private void loadStaffList() {
        try {
            List<Staff> list = apiService.getAllStaff();
            updateTable(list);
            selectedStaffId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStaffByName() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadStaffList();
            return;
        }
        try {
            List<Staff> list = apiService.searchStaffByName(searchText);
            updateTable(list);
            selectedStaffId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterStaffByRole() {
        String selectedRole = (String) cmbFilterRole.getSelectedItem();
        if ("Tất cả".equals(selectedRole)) {
            loadStaffList();
            return;
        }
        try {
            List<Staff> list = apiService.getStaffByRole(selectedRole);
            updateTable(list);
            selectedStaffId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc chức vụ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Staff> staffs) {
        tableModel.setRowCount(0);
        for (Staff st : staffs) {
            tableModel.addRow(new Object[]{
                    st.getStaffId(),
                    st.getFullName(),
                    st.getRole(),
                    st.getPhone(),
                    st.getEmail()
            });
        }
    }

    private void deleteStaff() {
        if (selectedStaffId == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân sự này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (!apiService.deleteStaff(selectedStaffId)) {
                throw new Exception("Xóa thất bại.");
            }
            JOptionPane.showMessageDialog(this, "Đã xóa nhân sự.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadStaffList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
