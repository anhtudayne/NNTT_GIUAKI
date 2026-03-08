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
    private JButton btnEdit;
    private JButton btnDelete;
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
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm theo tên:"));
        txtSearch = new JTextField(22);
        toolbarPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchStudents());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();

        JButton btnRefresh = new JButton("Làm mới");
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
        studentTable.setRowHeight(32);
        studentTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(245, 245, 245));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(220, 220, 220));

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
        Dimension btnSize = new Dimension(refButtonSize.width, refButtonSize.height);
        JButton btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(btnSize);
        btnAdd.setMinimumSize(btnSize);
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = new JButton("Sửa");
        btnEdit.setPreferredSize(btnSize);
        btnEdit.setMinimumSize(btnSize);
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());

        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setMinimumSize(btnSize);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteStudent());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
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
        } else {
            selectedStudentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
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
        for (Student s : students) {
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
