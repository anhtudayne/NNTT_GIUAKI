package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.StudentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab Quản lý Học viên: bảng danh sách phía trên, thanh tìm kiếm trên cùng,
 * ba nút Thêm / Sửa / Xóa phía dưới. Thêm và Sửa mở cửa sổ dialog; Xóa (và Sửa) chỉ bật khi có chọn dòng.
 */
public class StudentManagerPanel extends JPanel {

    private final StudentApiService apiService;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JCheckBox chkActiveOnly;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnDetails;
    private Long selectedStudentId = null;

    public StudentManagerPanel() {
        apiService = new StudentApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        // ----- Thanh công cụ: tìm kiếm + Làm mới -----
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(new Color(236, 240, 241));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblSearch = new JLabel("🔍 Tìm theo tên:");
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
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> searchStudents());
        toolbarPanel.add(btnSearch);
        
        toolbarPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        chkActiveOnly = new JCheckBox("✓ Chỉ hiển thị Active");
        chkActiveOnly.setBackground(new Color(236, 240, 241));
        chkActiveOnly.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        chkActiveOnly.addActionListener(e -> applyCurrentFilter());
        toolbarPanel.add(chkActiveOnly);

        JButton btnRefresh = new JButton("⟳ Làm mới");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadStudents());
        toolbarPanel.add(btnRefresh);

        add(toolbarPanel, BorderLayout.NORTH);

        // ----- Bảng danh sách (DataGridView) -----
        String[] columns = {"ID", "Họ và tên", "Ngày sinh", "Giới tính", "Điện thoại", "Email", "Địa chỉ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(36);
        studentTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(52, 73, 94));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setSelectionBackground(new Color(174, 214, 241));
        studentTable.setSelectionForeground(new Color(44, 62, 80));
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(220, 220, 220));
        studentTable.setIntercellSpacing(new Dimension(1, 1));

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateSelectionState();
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        // ----- Khu vực phía dưới: 3 nút chức năng -----
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));

        // Lấy kích thước nút "Tìm kiếm" làm chuẩn cho Thêm / Sửa / Xóa
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

        btnDetails = new JButton("📊 Chi tiết");
        btnDetails.setPreferredSize(btnSize);
        btnDetails.setMinimumSize(btnSize);
        btnDetails.setEnabled(false);
        btnDetails.setBackground(new Color(155, 89, 182));
        btnDetails.setForeground(Color.WHITE);
        btnDetails.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnDetails.setFocusPainted(false);
        btnDetails.setBorderPainted(false);
        btnDetails.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetails.addActionListener(e -> openDetailDialog());

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
        btnDelete.addActionListener(e -> deleteStudent());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDetails);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = studentTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedStudentId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
            btnDetails.setEnabled(true);
        } else {
            selectedStudentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
            btnDetails.setEnabled(false);
        }
    }

    private void openAddDialog() {
        StudentFormDialog dlg = new StudentFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                false,
                null,
                this::loadStudents
        );
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedStudentId == null) return;
        Student selected = getSelectedStudentFromTable();
        if (selected == null) return;
        StudentFormDialog dlg = new StudentFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                true,
                selected,
                this::loadStudents
        );
        dlg.setVisible(true);
    }

    private void openDetailDialog() {
        if (selectedStudentId == null) return;
        Student selected = getSelectedStudentFromTable();
        if (selected == null) return;
        StudentDetailDialog dlg = new StudentDetailDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                selected
        );
        dlg.setVisible(true);
    }

    private Student getSelectedStudentFromTable() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return null;
        Student s = new Student();
        s.setId(selectedStudentId);
        s.setFullName(str(tableModel.getValueAt(row, 1)));
        s.setDateOfBirth(str(tableModel.getValueAt(row, 2)));
        s.setGender(str(tableModel.getValueAt(row, 3)));
        s.setPhone(str(tableModel.getValueAt(row, 4)));
        s.setEmail(str(tableModel.getValueAt(row, 5)));
        s.setAddress(str(tableModel.getValueAt(row, 6)));
        s.setStatus(str(tableModel.getValueAt(row, 7)));
        return s;
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private void loadStudents() {
        try {
            List<Student> students = apiService.getAllStudents();
            updateTable(students);
            selectedStudentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyCurrentFilter() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadStudents();
        } else {
            searchStudents();
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
            selectedStudentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        boolean filterActive = chkActiveOnly.isSelected();
        for (Student s : students) {
            if (filterActive && !"Active".equalsIgnoreCase(s.getStatus())) {
                continue;
            }
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getFullName(),
                    s.getDateOfBirth(),
                    s.getGender(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getAddress(),
                    s.getStatus()
            });
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == null) {
            btnDelete.setEnabled(false);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa học viên này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteStudent(selectedStudentId);
            JOptionPane.showMessageDialog(this, "Đã xóa học viên.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadStudents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
