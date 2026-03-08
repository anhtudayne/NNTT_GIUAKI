package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.PlacementTest;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PlacementTestApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PlacementTestManagerPanel extends JPanel {
    private final PlacementTestApiService apiService;
    private JTable placementTestTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearchId;
    private JButton btnEdit, btnDelete;
    private Long selectedPlacementTestId = null;

    public PlacementTestManagerPanel() {
        apiService = new PlacementTestApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadPlacementTests();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm theo Placement Test ID:"));
        txtSearchId = new JTextField(20);
        toolbarPanel.add(txtSearchId);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchById());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPlacementTests());
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Test Date", "Score", "Recommended Level"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        placementTestTable = new JTable(tableModel);
        placementTestTable.setRowHeight(32);
        placementTestTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        placementTestTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        placementTestTable.getTableHeader().setBackground(new Color(245, 245, 245));
        placementTestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        placementTestTable.setShowGrid(true);
        placementTestTable.setGridColor(new Color(220, 220, 220));
        placementTestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(placementTestTable);
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
        btnDelete.addActionListener(e -> deleteTest());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = placementTestTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedPlacementTestId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedPlacementTestId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        PlacementTestFormDialog dlg = new PlacementTestFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, false, null, this::loadPlacementTests);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedPlacementTestId == null) return;
        PlacementTest p = getSelectedFromTable();
        if (p == null) return;
        PlacementTestFormDialog dlg = new PlacementTestFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, true, p, this::loadPlacementTests);
        dlg.setVisible(true);
    }

    private PlacementTest getSelectedFromTable() {
        int row = placementTestTable.getSelectedRow();
        if (row < 0) return null;
        PlacementTest p = new PlacementTest();
        p.setPlacementTestId(selectedPlacementTestId);
        Student s = new Student();
        Object sid = tableModel.getValueAt(row, 1);
        if (sid != null) s.setId(((Number) sid).longValue());
        p.setStudent(s);
        p.setTestDate(str(tableModel.getValueAt(row, 3)));
        Object score = tableModel.getValueAt(row, 4);
        if (score != null) p.setScore(((Number) score).doubleValue());
        p.setRecommendedLevel(str(tableModel.getValueAt(row, 5)));
        return p;
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void loadPlacementTests() {
        try {
            updateTable(apiService.getAllPlacementTests());
            selectedPlacementTestId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchById() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) { loadPlacementTests(); return; }
        try {
            Long id = Long.parseLong(idText);
            PlacementTest p = apiService.getPlacementTestById(id);
            updateTable(Collections.singletonList(p));
            selectedPlacementTestId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<PlacementTest> list) {
        tableModel.setRowCount(0);
        for (PlacementTest p : list) {
            Student s = p.getStudent();
            tableModel.addRow(new Object[]{
                    p.getPlacementTestId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    p.getTestDate(),
                    p.getScore(),
                    p.getRecommendedLevel()
            });
        }
    }

    private void deleteTest() {
        if (selectedPlacementTestId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deletePlacementTest(selectedPlacementTestId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPlacementTests();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
