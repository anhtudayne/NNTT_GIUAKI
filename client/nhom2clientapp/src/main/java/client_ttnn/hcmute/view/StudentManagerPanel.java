package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.StudentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;
import client_ttnn.hcmute.util.ButtonStyles;

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
        toolbarPanel.setBackground(Color.WHITE);
        
        toolbarPanel.add(new JLabel("Tìm kiếm học viên:"));
        txtSearch = new JTextField(22);
        toolbarPanel.add(txtSearch);
        
        JButton btnSearch = ButtonStyles.createPrimaryButton("Tìm");
        btnSearch.addActionListener(e -> searchStudents());
        toolbarPanel.add(btnSearch);
        
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        chkActiveOnly = new JCheckBox("Chỉ hiển thị Active");
        chkActiveOnly.setBackground(Color.WHITE);
        chkActiveOnly.addActionListener(e -> applyCurrentFilter());
        toolbarPanel.add(chkActiveOnly);

        JButton btnRefresh = ButtonStyles.createNeutralButton("Làm mới");
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
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(studentTable);
        
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        JButton btnAdd = ButtonStyles.createPrimaryButton("Thêm");
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = ButtonStyles.createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());

        btnDetails = ButtonStyles.createNeutralButton("Chi tiết");
        btnDetails.setEnabled(false);
        btnDetails.addActionListener(e -> openDetailDialog());

        btnDelete = ButtonStyles.createDangerButton("Xóa");
        btnDelete.setEnabled(false);
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
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return apiService.getAllStudents();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Student> students = get();
                    updateTable(students);
                    selectedStudentId = null;
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnDetails.setEnabled(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentManagerPanel.this,
                            "Lỗi khi tải danh sách: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
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
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return apiService.searchByName(searchText);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<Student> students = get();
                    updateTable(students);
                    selectedStudentId = null;
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnDetails.setEnabled(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentManagerPanel.this,
                            "Lỗi khi tìm kiếm: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
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

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Long idToDelete = selectedStudentId;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                apiService.deleteStudent(idToDelete);
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    get();
                    JOptionPane.showMessageDialog(StudentManagerPanel.this,
                            "Đã xóa học viên.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadStudents();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentManagerPanel.this,
                            "Lỗi khi xóa: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
