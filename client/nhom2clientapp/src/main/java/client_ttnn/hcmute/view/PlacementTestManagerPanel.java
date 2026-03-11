package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.PlacementTest;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PlacementTestApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import client_ttnn.hcmute.util.ButtonStyles;

public class PlacementTestManagerPanel extends JPanel {
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color HEADER_BG = new Color(250, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(95, 99, 104);
    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color DANGER = new Color(211, 47, 47);

    private final PlacementTestApiService apiService;
    private JTable placementTestTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearchId;
    private JButton btnEdit, btnDelete;
    private Long selectedPlacementTestId = null;

    public PlacementTestManagerPanel() {
        apiService = new PlacementTestApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadPlacementTests();
    }

    private void initComponents() {
        JPanel topWrap = new JPanel();
        topWrap.setOpaque(false);
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Placement Test (Đầu vào)");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        title.setForeground(new Color(33, 33, 33));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(title);
        topWrap.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(CARD_BG);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblSearch = new JLabel("Placement Test ID:");
        lblSearch.setForeground(TEXT_SECONDARY);
        toolbarPanel.add(lblSearch);
        txtSearchId = new JTextField(18);
        txtSearchId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        toolbarPanel.add(txtSearchId);
        JButton btnSearch = ButtonStyles.createPrimaryButton("Tìm");
        btnSearch.addActionListener(e -> searchById());
        toolbarPanel.add(btnSearch);
        JButton btnRefresh = ButtonStyles.createNeutralButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPlacementTests());
        toolbarPanel.add(btnRefresh);
        toolbarPanel.add(Box.createHorizontalStrut(8));
        JLabel hint = new JLabel("Mỗi học viên nên có 1 placement test gần nhất để tư vấn khóa phù hợp.");
        hint.setForeground(TEXT_SECONDARY);
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        toolbarPanel.add(hint);

        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(toolbarPanel);
        add(topWrap, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Test Date", "Score", "Recommended Level"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        placementTestTable = new JTable(tableModel);
        placementTestTable.setRowHeight(34);
        placementTestTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        placementTestTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        placementTestTable.getTableHeader().setBackground(HEADER_BG);
        placementTestTable.getTableHeader().setForeground(new Color(33, 33, 33));
        placementTestTable.getTableHeader().setReorderingAllowed(false);
        placementTestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        placementTestTable.setShowGrid(true);
        placementTestTable.setGridColor(new Color(235, 238, 245));
        placementTestTable.setSelectionBackground(new Color(227, 242, 253));
        placementTestTable.setSelectionForeground(new Color(33, 33, 33));
        placementTestTable.setDefaultRenderer(Object.class, zebraRenderer());
        placementTestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(placementTestTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(BG);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
        JButton btnAdd = ButtonStyles.createPrimaryButton("Thêm placement test");
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit = ButtonStyles.createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete = ButtonStyles.createDangerButton("Xóa");
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

    private DefaultTableCellRenderer zebraRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 251, 253));
                }
                c.setForeground(new Color(33, 33, 33));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
    }
}
