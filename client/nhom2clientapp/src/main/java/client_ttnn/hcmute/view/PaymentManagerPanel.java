package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Payment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PaymentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PaymentManagerPanel extends JPanel {
    private final PaymentApiService apiService;
    private JTable tblPayment;
    private DefaultTableModel tableModel;
    private JTextField txtPaymentIdSearch;
    private JButton btnEdit, btnDelete;
    private Long selectedPaymentId = null;

    public PaymentManagerPanel() {
        apiService = new PaymentApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadPayments();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm theo Payment ID:"));
        txtPaymentIdSearch = new JTextField(20);
        toolbarPanel.add(txtPaymentIdSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchPaymentById());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPayments());
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Enrollment ID", "Amount", "Payment Date", "Method", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPayment = new JTable(tableModel);
        tblPayment.setRowHeight(32);
        tblPayment.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblPayment.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tblPayment.getTableHeader().setBackground(new Color(245, 245, 245));
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPayment.setShowGrid(true);
        tblPayment.setGridColor(new Color(220, 220, 220));
        tblPayment.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(tblPayment);
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
        btnDelete.addActionListener(e -> deletePayment());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = tblPayment.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedPaymentId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedPaymentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        PaymentFormDialog dlg = new PaymentFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, false, null, this::loadPayments);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedPaymentId == null) return;
        try {
            Payment p = apiService.getPaymentById(selectedPaymentId);
            PaymentFormDialog dlg = new PaymentFormDialog(
                    (Window) SwingUtilities.getWindowAncestor(this), apiService, true, p, this::loadPayments);
            dlg.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Payment getSelectedFromTable() {
        int row = tblPayment.getSelectedRow();
        if (row < 0) return null;
        Payment p = new Payment();
        p.setPaymentId(selectedPaymentId);
        Student s = new Student();
        Object sid = tableModel.getValueAt(row, 1);
        if (sid != null) s.setId(((Number) sid).longValue());
        p.setStudent(s);
        Object eid = tableModel.getValueAt(row, 3);
        if (eid != null) {
            Enrollment e = new Enrollment();
            e.setEnrollmentId(((Number) eid).longValue());
            p.setEnrollment(e);
        }
        Object amt = tableModel.getValueAt(row, 4);
        if (amt != null) p.setAmount(new java.math.BigDecimal(amt.toString()));
        p.setPaymentDate(str(tableModel.getValueAt(row, 5)));
        p.setPaymentMethod(str(tableModel.getValueAt(row, 6)));
        p.setStatus(str(tableModel.getValueAt(row, 7)));
        return p;
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void loadPayments() {
        try {
            updateTable(apiService.getAllPayments());
            selectedPaymentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPaymentById() {
        String text = txtPaymentIdSearch.getText().trim();
        if (text.isEmpty()) { loadPayments(); return; }
        try {
            Long id = Long.parseLong(text);
            Payment p = apiService.getPaymentById(id);
            updateTable(Collections.singletonList(p));
            selectedPaymentId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Payment ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Payment> list) {
        tableModel.setRowCount(0);
        for (Payment p : list) {
            Student s = p.getStudent();
            Enrollment e = p.getEnrollment();
            tableModel.addRow(new Object[]{
                    p.getPaymentId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    e != null ? e.getEnrollmentId() : null,
                    p.getAmount(),
                    p.getPaymentDate(),
                    p.getPaymentMethod(),
                    p.getStatus()
            });
        }
    }

    private void deletePayment() {
        if (selectedPaymentId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thanh toán này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deletePayment(selectedPaymentId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPayments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
