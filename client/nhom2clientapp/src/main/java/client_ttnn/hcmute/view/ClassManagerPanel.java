package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.service.ClassesApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;

public class ClassManagerPanel extends JPanel {
    private final ClassesApiService apiService;
    private JTable classTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnEdit, btnDelete;
    private Long selectedClassId = null;

    public ClassManagerPanel() {
        apiService = new ClassesApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadClasses();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm kiếm lớp học:"));
        txtSearch = new JTextField(22);
        toolbarPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchClasses());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadClasses());
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên lớp", "Khóa học", "Giảng viên", "Phòng", "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số tối đa", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        classTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(classTable);
        
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(classTable);
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
        btnDelete.addActionListener(e -> deleteClass());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = classTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedClassId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedClassId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        ClassFormDialog dlg = new ClassFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, false, null, this::loadClasses);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedClassId == null) return;
        try {
            Classes c = apiService.getClassById(selectedClassId);
            ClassFormDialog dlg = new ClassFormDialog(
                    (Window) SwingUtilities.getWindowAncestor(this), apiService, true, c, this::loadClasses);
            dlg.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadClasses() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<Classes>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Classes> doInBackground() throws Exception {
                // Fetch direct from API for the latest list. Caching could be used, 
                // but since this is the management screen, we want the freshest data.
                return apiService.getAllClasses();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Classes> list = get();
                    updateTable(list);
                    selectedClassId = null;
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClassManagerPanel.this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void searchClasses() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) { loadClasses(); return; }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<List<Classes>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Classes> doInBackground() throws Exception {
                return apiService.searchByName(q);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Classes> list = get();
                    updateTable(list);
                    selectedClassId = null;
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClassManagerPanel.this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTable(List<Classes> list) {
        tableModel.setRowCount(0);
        for (Classes c : list) {
            tableModel.addRow(new Object[]{
                    c.getClassId(),
                    c.getClassName(),
                    c.getCourse() != null ? c.getCourse().getCourseId() : "",
                    c.getTeacher() != null ? c.getTeacher().getFullName() : "",
                    c.getRoom() != null ? c.getRoom().getRoomName() : "",
                    c.getStartDate(),
                    c.getEndDate(),
                    c.getMaxStudent(),
                    c.getStatus()
            });
        }
    }

    private void deleteClass() {
        if (selectedClassId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa lớp học này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteClass(selectedClassId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadClasses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
