package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Payment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PaymentApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class PaymentManagerPanel extends JPanel {

    private final PaymentApiService apiService;
    private JTable tblPayment;
    private DefaultTableModel tableModel;

    private JTextField txtPaymentIdSearch;
    private JTextField txtStudentId;
    private JTextField txtEnrollmentId;
    private JTextField txtAmount;
    private JTextField txtPaymentDate;
    private JComboBox<String> cmbMethod;
    private JComboBox<String> cmbStatus;

    private Long selectedPaymentId = null;

    public PaymentManagerPanel() {
        this.apiService = new PaymentApiService();
        initComponents();
        loadPayments();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm theo Payment ID:"));
        txtPaymentIdSearch = new JTextField(15);
        searchPanel.add(txtPaymentIdSearch);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchPaymentById());
        searchPanel.add(btnSearch);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPayments());
        searchPanel.add(btnRefresh);
        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Student ID", "Student Name",
                "Enrollment ID", "Amount", "Payment Date", "Method", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPayment = new JTable(tableModel);
        tblPayment.setRowHeight(28);
        tblPayment.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPayment.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPayment.getSelectedRow() != -1) {
                loadSelectedPayment();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPayment);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10),
                UIManager.getBorder("ScrollPane.border")));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(20);
        txtEnrollmentId = new JTextField(20);
        txtAmount = new JTextField(20);
        txtPaymentDate = new JTextField(20);
        cmbMethod = new JComboBox<>(new String[]{"Cash", "BankTransfer", "Momo", "Card"});
        cmbStatus = new JComboBox<>(new String[]{"Success", "Failed", "Refunded"});

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Enrollment ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEnrollmentId, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số tiền:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày thanh toán (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPaymentDate, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Phương thức:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbMethod, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createPayment());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updatePayment());
        JButton btnDelete = new JButton("Xoá");
        btnDelete.addActionListener(e -> deletePayment());
        JButton btnClear = new JButton("Xoá trắng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadPayments() {
        try {
            updateTable(apiService.getAllPayments());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPaymentById() {
        String text = txtPaymentIdSearch.getText().trim();
        if (text.isEmpty()) {
            loadPayments();
            return;
        }
        try {
            Long id = Long.parseLong(text);
            Payment payment = apiService.getPaymentById(id);
            updateTable(Collections.singletonList(payment));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Payment ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thanh toán: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Payment> list) {
        tableModel.setRowCount(0);
        for (Payment p : list) {
            Student s = p.getStudent();
            Enrollment e = p.getEnrollment();
            Object[] row = {
                    p.getPaymentId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    e != null ? e.getEnrollmentId() : null,
                    p.getAmount(),
                    p.getPaymentDate(),
                    p.getPaymentMethod(),
                    p.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedPayment() {
        int row = tblPayment.getSelectedRow();
        if (row == -1) return;

        Object idVal = tableModel.getValueAt(row, 0);
        selectedPaymentId = parseLong(idVal);

        txtStudentId.setText(valueOf(tableModel.getValueAt(row, 1)));
        txtEnrollmentId.setText(valueOf(tableModel.getValueAt(row, 3)));
        txtAmount.setText(valueOf(tableModel.getValueAt(row, 4)));
        txtPaymentDate.setText(valueOf(tableModel.getValueAt(row, 5)));

        Object method = tableModel.getValueAt(row, 6);
        if (method != null) {
            cmbMethod.setSelectedItem(method.toString());
        }
        Object status = tableModel.getValueAt(row, 7);
        if (status != null) {
            cmbStatus.setSelectedItem(status.toString());
        }
    }

    private void createPayment() {
        if (!validateForm()) return;
        try {
            apiService.createPayment(getPaymentFromForm());
            JOptionPane.showMessageDialog(this, "Thêm thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPayments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePayment() {
        if (selectedPaymentId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thanh toán cần cập nhật!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        try {
            apiService.updatePayment(selectedPaymentId, getPaymentFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPayments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePayment() {
        if (selectedPaymentId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thanh toán cần xoá!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xoá thanh toán này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deletePayment(selectedPaymentId);
                JOptionPane.showMessageDialog(this, "Xoá thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadPayments();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xoá thanh toán: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Payment getPaymentFromForm() {
        Payment p = new Payment();

        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        p.setStudent(s);

        if (!txtEnrollmentId.getText().trim().isEmpty()) {
            Enrollment e = new Enrollment();
            e.setEnrollmentId(Long.parseLong(txtEnrollmentId.getText().trim()));
            p.setEnrollment(e);
        }

        String amountText = txtAmount.getText().trim().replace(",", "");
        p.setAmount(new BigDecimal(amountText));
        p.setPaymentDate(txtPaymentDate.getText().trim());
        p.setPaymentMethod((String) cmbMethod.getSelectedItem());
        p.setStatus((String) cmbStatus.getSelectedItem());
        return p;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtAmount.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Số tiền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtPaymentDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày thanh toán!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Long.parseLong(txtStudentId.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID phải là số!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtEnrollmentId.getText().trim().isEmpty()) {
            try {
                Long.parseLong(txtEnrollmentId.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enrollment ID phải là số!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        try {
            new BigDecimal(txtAmount.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedPaymentId = null;
        txtStudentId.setText("");
        txtEnrollmentId.setText("");
        txtAmount.setText("");
        txtPaymentDate.setText("");
        cmbMethod.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        tblPayment.clearSelection();
    }

    private Long parseLong(Object val) {
        if (val == null) return null;
        if (val instanceof Long) return (Long) val;
        if (val instanceof Integer) return ((Integer) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String valueOf(Object val) {
        return val == null ? "" : val.toString();
    }
}

