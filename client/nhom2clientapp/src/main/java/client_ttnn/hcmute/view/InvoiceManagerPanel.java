package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;

public class InvoiceManagerPanel extends JPanel {
    private final InvoiceApiService apiService;
    private final PromotionApiService promotionService;
    private JTable tblInvoice;
    private DefaultTableModel tableModel;
    private JTextField txtInvoiceIdSearch;
    private JButton btnEdit, btnDelete;
    private Long selectedInvoiceId = null;

    public InvoiceManagerPanel() {
        apiService = new InvoiceApiService();
        promotionService = new PromotionApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm theo Invoice ID:"));
        txtInvoiceIdSearch = new JTextField(20);
        toolbarPanel.add(txtInvoiceIdSearch);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> searchInvoiceById());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadInvoices());
        toolbarPanel.add(btnRefresh);
        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Total Amount", "Issue Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblInvoice = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(tblInvoice);
        
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInvoice.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(tblInvoice);
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
        btnDelete.addActionListener(e -> deleteInvoice());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = tblInvoice.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedInvoiceId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedInvoiceId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        InvoiceFormDialog dlg = new InvoiceFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this), apiService, promotionService, false, null, this::loadInvoices);
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedInvoiceId == null) return;
        try {
            Invoice inv = apiService.getInvoiceById(selectedInvoiceId);
            InvoiceFormDialog dlg = new InvoiceFormDialog(
                    (Window) SwingUtilities.getWindowAncestor(this), apiService, promotionService, true, inv, this::loadInvoices);
            dlg.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInvoices() {
        try {
            updateTable(apiService.getAllInvoices());
            selectedInvoiceId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchInvoiceById() {
        String text = txtInvoiceIdSearch.getText().trim();
        if (text.isEmpty()) { loadInvoices(); return; }
        try {
            Long id = Long.parseLong(text);
            Invoice inv = apiService.getInvoiceById(id);
            updateTable(Collections.singletonList(inv));
            selectedInvoiceId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invoice ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Invoice> list) {
        tableModel.setRowCount(0);
        for (Invoice inv : list) {
            Student s = inv.getStudent();
            tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    s != null ? s.getId() : null,
                    s != null ? s.getFullName() : "",
                    inv.getTotalAmount(),
                    inv.getIssueDate(),
                    inv.getStatus()
            });
        }
    }

    private void deleteInvoice() {
        if (selectedInvoiceId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hóa đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            apiService.deleteInvoice(selectedInvoiceId);
            JOptionPane.showMessageDialog(this, "Đã xóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadInvoices();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
