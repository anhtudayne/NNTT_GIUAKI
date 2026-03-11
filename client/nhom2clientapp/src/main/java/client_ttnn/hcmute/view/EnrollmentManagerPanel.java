package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.EnrollmentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;
import client_ttnn.hcmute.util.ButtonStyles;

public class EnrollmentManagerPanel extends JPanel {
    private final EnrollmentApiService apiService;
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearchId;
    private JButton btnEdit, btnDelete;
    private Long selectedEnrollmentId = null;

    public EnrollmentManagerPanel() {
        apiService = new EnrollmentApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadEnrollments();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm theo Enrollment ID:"));
        txtSearchId = new JTextField(20);
        toolbarPanel.add(txtSearchId);
        JButton btnSearch = ButtonStyles.createPrimaryButton("Tìm");
        btnSearch.addActionListener(e -> searchEnrollmentById());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = ButtonStyles.createNeutralButton("Làm mới");
        btnRefresh.addActionListener(e -> loadEnrollments());
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Class ID", "Class Name", "Enrollment Date", "Status", "Result"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        enrollmentTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(enrollmentTable);
        
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
        Dimension btnSize = new Dimension(refButtonSize.width, refButtonSize.height);
        JButton btnAdd = ButtonStyles.createPrimaryButton("Thêm");
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit = ButtonStyles.createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete = ButtonStyles.createDangerButton("Xóa");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteEnrollment());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = enrollmentTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedEnrollmentId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedEnrollmentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        EnrollmentFormDialog dlg = new EnrollmentFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, false, null, this::loadEnrollments);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedEnrollmentId == null) return;
        Enrollment e = getSelectedFromTable();
        if (e == null) return;
        EnrollmentFormDialog dlg = new EnrollmentFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, true, e, this::loadEnrollments);
        dlg.setVisible(true);
    }

    private Enrollment getSelectedFromTable() {
        int row = enrollmentTable.getSelectedRow();
        if (row < 0) return null;
        Enrollment e = new Enrollment();
        e.setEnrollmentId(selectedEnrollmentId);
        Student s = new Student();
        Object sid = tableModel.getValueAt(row, 1);
        if (sid != null) s.setId(((Number) sid).longValue());
        e.setStudent(s);
        Classes c = new Classes();
        Object cid = tableModel.getValueAt(row, 3);
        if (cid != null) c.setClassId(((Number) cid).longValue());
        e.setClassEntity(c);
        e.setEnrollmentDate(str(tableModel.getValueAt(row, 5)));
        e.setStatus(str(tableModel.getValueAt(row, 6)));
        e.setResult(str(tableModel.getValueAt(row, 7)));
        return e;
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void loadEnrollments() {
        try {
            updateTable(apiService.getAllEnrollments());
            selectedEnrollmentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEnrollmentById() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) { loadEnrollments(); return; }
        try {
            Long id = Long.parseLong(idText);
            Enrollment enrollment = apiService.getEnrollmentById(id);
            updateTable(Collections.singletonList(enrollment));
            selectedEnrollmentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enrollment ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Enrollment> list) {
        tableModel.setRowCount(0);
        for (Enrollment e : list) {
            Student s = e.getStudent();
            Classes c = e.getClassEntity();
            tableModel.addRow(new Object[]{
                    e.getEnrollmentId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    c != null ? c.getClassId() : null,
                    c != null ? c.getClassName() : "",
                    e.getEnrollmentDate(),
                    e.getStatus(),
                    e.getResult()
            });
        }
    }

    private void deleteEnrollment() {
        if (selectedEnrollmentId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ghi danh này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteEnrollment(selectedEnrollmentId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
