package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.service.CourseApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;

public class CourseManagerPanel extends JPanel {

    private final CourseApiService apiService;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnEdit;
    private JButton btnDelete;
    private Long selectedCourseId = null;

    public CourseManagerPanel() {
        apiService = new CourseApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadCourses();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm kiếm khóa học:"));
        txtSearch = new JTextField(22);
        toolbarPanel.add(txtSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchCourses());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadCourses());
        toolbarPanel.add(btnRefresh);

        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên khóa học", "Mô tả", "Cấp độ", "Thời lượng", "Học phí", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(courseTable);
        
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateSelectionState();
        });

        JScrollPane scrollPane = new JScrollPane(courseTable);
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
        btnDelete.addActionListener(e -> deleteCourse());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = courseTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedCourseId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedCourseId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        CourseFormDialog dlg = new CourseFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                false,
                null,
                this::loadCourses
        );
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedCourseId == null) return;
        Course selected = getSelectedCourseFromTable();
        if (selected == null) return;
        CourseFormDialog dlg = new CourseFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                true,
                selected,
                this::loadCourses
        );
        dlg.setVisible(true);
    }

    private Course getSelectedCourseFromTable() {
        int row = courseTable.getSelectedRow();
        if (row < 0) return null;
        Course c = new Course();
        c.setCourseId(selectedCourseId);
        c.setCourseName(str(tableModel.getValueAt(row, 1)));
        c.setDescription(str(tableModel.getValueAt(row, 2)));
        c.setLevel(str(tableModel.getValueAt(row, 3)));
        Object dur = tableModel.getValueAt(row, 4);
        c.setDuration(dur != null ? ((Number) dur).intValue() : null);
        Object feeObj = tableModel.getValueAt(row, 5);
        if (feeObj != null) {
            try {
                c.setFee(Double.parseDouble(feeObj.toString()));
            } catch (NumberFormatException e) {
                c.setFee(null);
            }
        }
        c.setStatus(str(tableModel.getValueAt(row, 6)));
        return c;
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private void loadCourses() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<Course>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                // Fetch direct from API for the latest list
                return apiService.getAllCourses();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Course> list = get();
                    updateTable(list);
                    selectedCourseId = null;
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CourseManagerPanel.this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void searchCourses() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            loadCourses();
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<Course>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return apiService.searchByName(searchText);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Course> list = get();
                    updateTable(list);
                    selectedCourseId = null;
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CourseManagerPanel.this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTable(List<Course> courses) {
        tableModel.setRowCount(0);
        for (Course c : courses) {
            tableModel.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseName(),
                    c.getDescription(),
                    c.getLevel(),
                    c.getDuration(),
                    c.getFee() != null ? c.getFee() : "",
                    c.getStatus()
            });
        }
    }

    private void deleteCourse() {
        if (selectedCourseId == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khóa học này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteCourse(selectedCourseId);
            JOptionPane.showMessageDialog(this, "Đã xóa khóa học.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCourses();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
