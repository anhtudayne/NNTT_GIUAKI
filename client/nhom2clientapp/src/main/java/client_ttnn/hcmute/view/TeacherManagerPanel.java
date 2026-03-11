package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.service.TeacherApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;

public class TeacherManagerPanel extends JPanel {
    private final TeacherApiService apiService;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JCheckBox chkOnlyActive;
    private JButton btnEdit, btnDelete;
    private Integer selectedTeacherId = null;

    public TeacherManagerPanel() {
        apiService = new TeacherApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadTeachers();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        
        toolbarPanel.add(new JLabel("Tìm kiếm GV:"));
        txtSearch = new JTextField(22);
        toolbarPanel.add(txtSearch);
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchTeachers());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        chkOnlyActive = new JCheckBox("Chỉ Giảng viên Active");
        chkOnlyActive.setBackground(Color.WHITE);
        chkOnlyActive.addActionListener(e -> {
            if (chkOnlyActive.isSelected()) loadActiveTeachers();
            else loadTeachers();
        });
        toolbarPanel.add(chkOnlyActive);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> { chkOnlyActive.setSelected(false); loadTeachers(); });
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Họ và tên", "Điện thoại", "Email", "Chuyên môn", "Ngày vào làm", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        teacherTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(teacherTable);
        
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
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
        btnDelete.addActionListener(e -> deleteTeacher());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = teacherTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedTeacherId = idVal != null ? ((Number) idVal).intValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedTeacherId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        TeacherFormDialog dlg = new TeacherFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, false, null, this::loadTeachers);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedTeacherId == null) return;
        Teacher t = getSelectedFromTable();
        if (t == null) return;
        TeacherFormDialog dlg = new TeacherFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, true, t, this::loadTeachers);
        dlg.setVisible(true);
    }

    private Teacher getSelectedFromTable() {
        int row = teacherTable.getSelectedRow();
        if (row < 0) return null;
        Teacher t = new Teacher();
        t.setTeacherId(selectedTeacherId);
        t.setFullName(str(tableModel.getValueAt(row, 1)));
        t.setPhone(str(tableModel.getValueAt(row, 2)));
        t.setEmail(str(tableModel.getValueAt(row, 3)));
        t.setSpecialty(str(tableModel.getValueAt(row, 4)));
        t.setHireDate(str(tableModel.getValueAt(row, 5)));
        t.setStatus(str(tableModel.getValueAt(row, 6)));
        return t;
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void loadTeachers() {
        try {
            updateTable(apiService.getAllTeachers());
            selectedTeacherId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadActiveTeachers() {
        try {
            updateTable(apiService.getActiveTeachers());
            selectedTeacherId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTeachers() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) { loadTeachers(); return; }
        try {
            updateTable(apiService.searchByName(q));
            selectedTeacherId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Teacher> list) {
        tableModel.setRowCount(0);
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                    t.getTeacherId(), t.getFullName(), t.getPhone(), t.getEmail(),
                    t.getSpecialty(), t.getHireDate(), t.getStatus()
            });
        }
    }

    private void deleteTeacher() {
        if (selectedTeacherId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa giảng viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteTeacher(selectedTeacherId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadTeachers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
