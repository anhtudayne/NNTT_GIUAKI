package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Certificate;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.CertificateApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CertificateManagerPanel extends JPanel {
    private final CertificateApiService apiService;
    private JTable certificateTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearchName;
    private JButton btnEdit, btnDelete;
    private Long selectedCertificateId = null;

    public CertificateManagerPanel() {
        apiService = new CertificateApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadCertificates();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(new Color(236, 240, 241));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblSearch = new JLabel("Tìm theo tên chứng chỉ:");
        lblSearch.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        toolbarPanel.add(lblSearch);
        txtSearchName = new JTextField(20);
        txtSearchName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtSearchName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        toolbarPanel.add(txtSearchName);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> searchByName());
        toolbarPanel.add(btnSearch);

        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadCertificates());
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Course ID", "Course Name", "Certificate Name", "Issue Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        certificateTable = new JTable(tableModel);
        certificateTable.setRowHeight(36);
        certificateTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        certificateTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        certificateTable.getTableHeader().setBackground(new Color(52, 73, 94));
        certificateTable.getTableHeader().setForeground(Color.WHITE);
        certificateTable.setSelectionBackground(new Color(174, 214, 241));
        certificateTable.setSelectionForeground(new Color(44, 62, 80));
        certificateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        certificateTable.setShowGrid(true);
        certificateTable.setGridColor(new Color(220, 220, 220));
        certificateTable.setIntercellSpacing(new Dimension(1, 1));
        certificateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(certificateTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
        Dimension btnSize = new Dimension(130, 40);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setPreferredSize(btnSize);
        btnAdd.setMinimumSize(btnSize);
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = new JButton("Sửa");
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

        btnDelete = new JButton("Xóa");
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setMinimumSize(btnSize);
        btnDelete.setEnabled(false);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteCertificate());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = certificateTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedCertificateId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedCertificateId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        CertificateFormDialog dlg = new CertificateFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, false, null, this::loadCertificates);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedCertificateId == null) return;
        Certificate c = getSelectedFromTable();
        if (c == null) return;
        CertificateFormDialog dlg = new CertificateFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, true, c, this::loadCertificates);
        dlg.setVisible(true);
    }

    private Certificate getSelectedFromTable() {
        int row = certificateTable.getSelectedRow();
        if (row < 0) return null;
        Certificate c = new Certificate();
        c.setCertificateId(selectedCertificateId);
        Student s = new Student();
        Object sid = tableModel.getValueAt(row, 1);
        if (sid != null) s.setId(((Number) sid).longValue());
        c.setStudent(s);
        Course co = new Course();
        Object cid = tableModel.getValueAt(row, 3);
        if (cid != null) co.setCourseId(((Number) cid).longValue());
        c.setCourse(co);
        c.setCertificateName(str(tableModel.getValueAt(row, 5)));
        c.setIssueDate(str(tableModel.getValueAt(row, 6)));
        return c;
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void loadCertificates() {
        try {
            updateTable(apiService.getAllCertificates());
            selectedCertificateId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByName() {
        String nameText = txtSearchName.getText().trim();
        if (nameText.isEmpty()) { loadCertificates(); return; }
        try {
            List<Certificate> allCertificates = apiService.getAllCertificates();
            List<Certificate> filtered = new ArrayList<>();
            String keyword = nameText.toLowerCase();
            for (Certificate c : allCertificates) {
                String certName = c != null ? str(c.getCertificateName()) : "";
                if (certName.toLowerCase().contains(keyword)) {
                    filtered.add(c);
                }
            }
            updateTable(filtered);
            selectedCertificateId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chứng chỉ phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Certificate> list) {
        tableModel.setRowCount(0);
        for (Certificate c : list) {
            Student s = c.getStudent();
            Course co = c.getCourse();
            tableModel.addRow(new Object[]{
                    c.getCertificateId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    co != null ? co.getCourseId() : null,
                    co != null ? co.getCourseName() : "",
                    c.getCertificateName(),
                    c.getIssueDate()
            });
        }
    }

    private void deleteCertificate() {
        if (selectedCertificateId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chứng chỉ này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteCertificate(selectedCertificateId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCertificates();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
