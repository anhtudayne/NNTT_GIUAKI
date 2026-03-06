package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.PlacementTest;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PlacementTestApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PlacementTestManagerPanel extends JPanel {
    private final PlacementTestApiService apiService;
    private JTable placementTestTable;
    private DefaultTableModel tableModel;

    private JTextField txtStudentId;
    private JTextField txtTestDate;
    private JTextField txtScore;
    private JTextField txtRecommendedLevel;
    private JTextField txtSearchId;

    private Long selectedPlacementTestId = null;

    public PlacementTestManagerPanel() {
        apiService = new PlacementTestApiService();
        initComponents();
        loadPlacementTests();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm theo Placement Test ID:"));
        txtSearchId = new JTextField(20);
        searchPanel.add(txtSearchId);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchPlacementTestById());
        searchPanel.add(btnSearch);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPlacementTests());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Test Date", "Score", "Recommended Level"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        placementTestTable = new JTable(tableModel);
        placementTestTable.setRowHeight(30);
        placementTestTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        placementTestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        placementTestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && placementTestTable.getSelectedRow() != -1) {
                loadSelectedPlacementTest();
            }
        });

        JScrollPane scrollPane = new JScrollPane(placementTestTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10),
                UIManager.getBorder("ScrollPane.border")));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Placement Test"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(20);
        txtTestDate = new JTextField(20);
        txtScore = new JTextField(20);
        txtRecommendedLevel = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Ngày test (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTestDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Score:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtScore, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Recommended Level:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtRecommendedLevel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createPlacementTest());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updatePlacementTest());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deletePlacementTest());
        JButton btnClear = new JButton("Xóa trắng bảng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadPlacementTests() {
        try {
            updateTable(apiService.getAllPlacementTests());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách placement test: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPlacementTestById() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) {
            loadPlacementTests();
            return;
        }

        try {
            Long id = Long.parseLong(idText);
            PlacementTest placementTest = apiService.getPlacementTestById(id);
            updateTable(Collections.singletonList(placementTest));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Placement Test ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy placement test: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<PlacementTest> placementTests) {
        tableModel.setRowCount(0);
        for (PlacementTest placementTest : placementTests) {
            Student student = placementTest.getStudent();
            Object[] row = {
                    placementTest.getPlacementTestId(),
                    student != null ? student.getId() : null,
                    student != null ? student.getFullName() : "",
                    placementTest.getTestDate(),
                    placementTest.getScore(),
                    placementTest.getRecommendedLevel()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedPlacementTest() {
        int selectedRow = placementTestTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        selectedPlacementTestId = parseLongValue(tableModel.getValueAt(selectedRow, 0));
        txtStudentId.setText(valueToString(tableModel.getValueAt(selectedRow, 1)));
        txtTestDate.setText(valueToString(tableModel.getValueAt(selectedRow, 3)));
        txtScore.setText(valueToString(tableModel.getValueAt(selectedRow, 4)));
        txtRecommendedLevel.setText(valueToString(tableModel.getValueAt(selectedRow, 5)));
    }

    private void createPlacementTest() {
        if (!validateForm()) {
            return;
        }

        try {
            apiService.createPlacementTest(getPlacementTestFromForm());
            JOptionPane.showMessageDialog(this, "Thêm placement test thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPlacementTests();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm placement test: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePlacementTest() {
        if (selectedPlacementTestId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn placement test cần cập nhật!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            apiService.updatePlacementTest(selectedPlacementTestId, getPlacementTestFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật placement test thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPlacementTests();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật placement test: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePlacementTest() {
        if (selectedPlacementTestId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn placement test cần xóa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa placement test này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deletePlacementTest(selectedPlacementTestId);
                JOptionPane.showMessageDialog(this, "Xóa placement test thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadPlacementTests();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa placement test: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private PlacementTest getPlacementTestFromForm() {
        PlacementTest placementTest = new PlacementTest();

        Student student = new Student();
        student.setId(Long.parseLong(txtStudentId.getText().trim()));
        placementTest.setStudent(student);

        placementTest.setTestDate(txtTestDate.getText().trim());
        placementTest.setScore(Double.parseDouble(txtScore.getText().trim()));
        placementTest.setRecommendedLevel(txtRecommendedLevel.getText().trim());
        return placementTest;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtTestDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày test!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtScore.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập score!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtRecommendedLevel.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập recommended level!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Long.parseLong(txtStudentId.getText().trim());
            Double.parseDouble(txtScore.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID phải là số nguyên và Score phải là số thực.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        selectedPlacementTestId = null;
        txtStudentId.setText("");
        txtTestDate.setText("");
        txtScore.setText("");
        txtRecommendedLevel.setText("");
        placementTestTable.clearSelection();
    }

    private Long parseLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String valueToString(Object value) {
        return value == null ? "" : value.toString();
    }
}
