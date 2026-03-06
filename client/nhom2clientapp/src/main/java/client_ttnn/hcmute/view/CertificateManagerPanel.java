package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Certificate;
import client_ttnn.hcmute.model.Course;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.CertificateApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class CertificateManagerPanel extends JPanel {
    private final CertificateApiService apiService;
    private JTable certificateTable;
    private DefaultTableModel tableModel;

    private JTextField txtStudentId;
    private JTextField txtCourseId;
    private JTextField txtCertificateName;
    private JTextField txtIssueDate;
    private JTextField txtSearchId;

    private Long selectedCertificateId = null;

    public CertificateManagerPanel() {
        apiService = new CertificateApiService();
        initComponents();
        loadCertificates();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm theo Certificate ID:"));
        txtSearchId = new JTextField(20);
        searchPanel.add(txtSearchId);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchCertificateById());
        searchPanel.add(btnSearch);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadCertificates());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Student ID", "Student Name", "Course ID", "Course Name", "Certificate Name", "Issue Date"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        certificateTable = new JTable(tableModel);
        certificateTable.setRowHeight(30);
        certificateTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        certificateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        certificateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && certificateTable.getSelectedRow() != -1) {
                loadSelectedCertificate();
            }
        });

        JScrollPane scrollPane = new JScrollPane(certificateTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10),
                UIManager.getBorder("ScrollPane.border")));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chứng chỉ"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(20);
        txtCourseId = new JTextField(20);
        txtCertificateName = new JTextField(20);
        txtIssueDate = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Course ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCourseId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tên chứng chỉ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCertificateName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày cấp (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtIssueDate, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createCertificate());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateCertificate());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteCertificate());
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

    private void loadCertificates() {
        try {
            updateTable(apiService.getAllCertificates());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách chứng chỉ: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCertificateById() {
        String idText = txtSearchId.getText().trim();
        if (idText.isEmpty()) {
            loadCertificates();
            return;
        }

        try {
            Long id = Long.parseLong(idText);
            Certificate certificate = apiService.getCertificateById(id);
            updateTable(Collections.singletonList(certificate));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Certificate ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy certificate: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Certificate> certificates) {
        tableModel.setRowCount(0);
        for (Certificate certificate : certificates) {
            Student student = certificate.getStudent();
            Course course = certificate.getCourse();

            Object[] row = {
                    certificate.getCertificateId(),
                    student != null ? student.getId() : null,
                    student != null ? student.getFullName() : "",
                    course != null ? course.getCourseId() : null,
                    course != null ? course.getCourseName() : "",
                    certificate.getCertificateName(),
                    certificate.getIssueDate()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedCertificate() {
        int selectedRow = certificateTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        selectedCertificateId = parseLongValue(tableModel.getValueAt(selectedRow, 0));
        txtStudentId.setText(valueToString(tableModel.getValueAt(selectedRow, 1)));
        txtCourseId.setText(valueToString(tableModel.getValueAt(selectedRow, 3)));
        txtCertificateName.setText(valueToString(tableModel.getValueAt(selectedRow, 5)));
        txtIssueDate.setText(valueToString(tableModel.getValueAt(selectedRow, 6)));
    }

    private void createCertificate() {
        if (!validateForm()) {
            return;
        }

        try {
            apiService.createCertificate(getCertificateFromForm());
            JOptionPane.showMessageDialog(this, "Thêm certificate thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCertificates();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm certificate: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCertificate() {
        if (selectedCertificateId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn certificate cần cập nhật!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            apiService.updateCertificate(selectedCertificateId, getCertificateFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật certificate thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadCertificates();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật certificate: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCertificate() {
        if (selectedCertificateId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn certificate cần xóa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa certificate này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteCertificate(selectedCertificateId);
                JOptionPane.showMessageDialog(this, "Xóa certificate thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCertificates();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa certificate: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Certificate getCertificateFromForm() {
        Certificate certificate = new Certificate();

        Student student = new Student();
        student.setId(Long.parseLong(txtStudentId.getText().trim()));
        certificate.setStudent(student);

        Course course = new Course();
        course.setCourseId(Long.parseLong(txtCourseId.getText().trim()));
        certificate.setCourse(course);

        certificate.setCertificateName(txtCertificateName.getText().trim());
        certificate.setIssueDate(txtIssueDate.getText().trim());
        return certificate;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtCourseId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Course ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtCertificateName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chứng chỉ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtIssueDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày cấp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Long.parseLong(txtStudentId.getText().trim());
            Long.parseLong(txtCourseId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID và Course ID phải là số.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        selectedCertificateId = null;
        txtStudentId.setText("");
        txtCourseId.setText("");
        txtCertificateName.setText("");
        txtIssueDate.setText("");
        certificateTable.clearSelection();
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
