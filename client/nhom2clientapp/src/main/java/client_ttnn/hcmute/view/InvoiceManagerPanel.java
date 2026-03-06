package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class InvoiceManagerPanel extends JPanel {

    private final InvoiceApiService apiService;
    private final PromotionApiService promotionService;
    private JTable tblInvoice;
    private DefaultTableModel tableModel;

    private JTextField txtInvoiceIdSearch;
    private JTextField txtStudentId;
    private JTextField txtTotalAmount;
    private JTextField txtAmountAfterDiscount;
    private JTextField txtIssueDate;
    private JComboBox<String> cmbStatus;
    private JComboBox<Promotion> cmbPromotion;

    private Long selectedInvoiceId = null;

    public InvoiceManagerPanel() {
        this.apiService = new InvoiceApiService();
        this.promotionService = new PromotionApiService();
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm theo Invoice ID:"));
        txtInvoiceIdSearch = new JTextField(15);
        searchPanel.add(txtInvoiceIdSearch);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchInvoiceById());
        searchPanel.add(btnSearch);
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadInvoices());
        searchPanel.add(btnRefresh);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Student ID", "Student Name", "Total Amount", "Issue Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInvoice = new JTable(tableModel);
        tblInvoice.setRowHeight(28);
        tblInvoice.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInvoice.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblInvoice.getSelectedRow() != -1) {
                loadSelectedInvoice();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblInvoice);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10),
                UIManager.getBorder("ScrollPane.border")));
        add(scrollPane, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hoá đơn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(20);
        txtTotalAmount = new JTextField(20);
        txtAmountAfterDiscount = new JTextField(20);
        txtAmountAfterDiscount.setEditable(false);
        txtIssueDate = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"Unpaid", "Partial", "Paid"});
        cmbPromotion = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTotalAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Khuyến mãi:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbPromotion, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Sau khuyến mãi:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtAmountAfterDiscount, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày xuất (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtIssueDate, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createInvoice());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateInvoice());
        JButton btnDelete = new JButton("Xoá");
        btnDelete.addActionListener(e -> deleteInvoice());
        JButton btnClear = new JButton("Xoá trắng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);

        // Load active promotions for dropdown
        loadActivePromotions();

        // Recalculate discounted amount when base amount or promotion changes
        txtTotalAmount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { recalcDiscountedAmount(); }

            @Override
            public void removeUpdate(DocumentEvent e) { recalcDiscountedAmount(); }

            @Override
            public void changedUpdate(DocumentEvent e) { recalcDiscountedAmount(); }
        });
        cmbPromotion.addActionListener(e -> recalcDiscountedAmount());
    }

    private void loadInvoices() {
        try {
            updateTable(apiService.getAllInvoices());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách hoá đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadActivePromotions() {
        try {
            cmbPromotion.removeAllItems();
            List<Promotion> promotions = promotionService.getActivePromotions(null);
            for (Promotion p : promotions) {
                cmbPromotion.addItem(p);
            }
            cmbPromotion.insertItemAt(null, 0);
            cmbPromotion.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách khuyến mãi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchInvoiceById() {
        String text = txtInvoiceIdSearch.getText().trim();
        if (text.isEmpty()) {
            loadInvoices();
            return;
        }
        try {
            Long id = Long.parseLong(text);
            Invoice invoice = apiService.getInvoiceById(id);
            updateTable(Collections.singletonList(invoice));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invoice ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hoá đơn: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Invoice> invoices) {
        tableModel.setRowCount(0);
        for (Invoice inv : invoices) {
            Student s = inv.getStudent();
            Object[] row = {
                    inv.getInvoiceId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    inv.getTotalAmount(),
                    inv.getIssueDate(),
                    inv.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedInvoice() {
        int row = tblInvoice.getSelectedRow();
        if (row == -1) return;

        Object idVal = tableModel.getValueAt(row, 0);
        selectedInvoiceId = parseLong(idVal);
        txtStudentId.setText(valueOf(tableModel.getValueAt(row, 1)));
        txtTotalAmount.setText(valueOf(tableModel.getValueAt(row, 3)));
        txtAmountAfterDiscount.setText(valueOf(tableModel.getValueAt(row, 3)));
        txtIssueDate.setText(valueOf(tableModel.getValueAt(row, 4)));
        Object status = tableModel.getValueAt(row, 5);
        if (status != null) {
            cmbStatus.setSelectedItem(status.toString());
        }
    }

    private void createInvoice() {
        if (!validateForm()) return;
        try {
            apiService.createInvoice(getInvoiceFromForm());
            JOptionPane.showMessageDialog(this, "Thêm hoá đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadInvoices();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm hoá đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInvoice() {
        if (selectedInvoiceId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hoá đơn cần cập nhật!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        try {
            apiService.updateInvoice(selectedInvoiceId, getInvoiceFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật hoá đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadInvoices();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hoá đơn: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInvoice() {
        if (selectedInvoiceId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hoá đơn cần xoá!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xoá hoá đơn này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteInvoice(selectedInvoiceId);
                JOptionPane.showMessageDialog(this, "Xoá hoá đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadInvoices();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xoá hoá đơn: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Invoice getInvoiceFromForm() {
        Invoice inv = new Invoice();
        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        inv.setStudent(s);

        String amountText = txtTotalAmount.getText().trim().replace(",", "");
        BigDecimal baseAmount = new BigDecimal(amountText);

        Promotion selectedPromo = (Promotion) cmbPromotion.getSelectedItem();
        BigDecimal finalAmount = baseAmount;
        if (selectedPromo != null && selectedPromo.getDiscountPercent() != null) {
            BigDecimal hundred = BigDecimal.valueOf(100);
            BigDecimal factor = BigDecimal.ONE.subtract(selectedPromo.getDiscountPercent().divide(hundred));
            finalAmount = baseAmount.multiply(factor);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
        }

        inv.setTotalAmount(finalAmount);
        inv.setIssueDate(txtIssueDate.getText().trim());
        inv.setStatus((String) cmbStatus.getSelectedItem());
        return inv;
    }

    private void recalcDiscountedAmount() {
        String amountText = txtTotalAmount.getText().trim().replace(",", "");
        if (amountText.isEmpty()) {
            txtAmountAfterDiscount.setText("");
            return;
        }
        try {
            BigDecimal baseAmount = new BigDecimal(amountText);
            Promotion selectedPromo = (Promotion) cmbPromotion.getSelectedItem();
            BigDecimal finalAmount = baseAmount;
            if (selectedPromo != null && selectedPromo.getDiscountPercent() != null) {
                BigDecimal hundred = BigDecimal.valueOf(100);
                BigDecimal factor = BigDecimal.ONE.subtract(selectedPromo.getDiscountPercent().divide(hundred));
                finalAmount = baseAmount.multiply(factor);
                if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    finalAmount = BigDecimal.ZERO;
                }
            }
            txtAmountAfterDiscount.setText(String.format("%,.0f", finalAmount));
        } catch (NumberFormatException ex) {
            txtAmountAfterDiscount.setText("");
        }
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtTotalAmount.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tổng tiền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtIssueDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày xuất hoá đơn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Long.parseLong(txtStudentId.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID phải là số!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(txtTotalAmount.getText().trim().replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tổng tiền không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedInvoiceId = null;
        txtStudentId.setText("");
        txtTotalAmount.setText("");
        txtIssueDate.setText("");
        cmbStatus.setSelectedIndex(0);
        tblInvoice.clearSelection();
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

