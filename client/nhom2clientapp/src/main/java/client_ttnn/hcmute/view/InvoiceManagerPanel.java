package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Invoice;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.InvoiceApiService;
import client_ttnn.hcmute.service.PaymentApiService;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class InvoiceManagerPanel extends JPanel {
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color HEADER_BG = new Color(250, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(95, 99, 104);
    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color DANGER = new Color(211, 47, 47);

    private final InvoiceApiService apiService;
    private final PaymentApiService paymentApiService;
    private final PromotionApiService promotionService;
    private JTable tblInvoice;
    private DefaultTableModel tableModel;
    private JTextField txtInvoiceIdSearch;
    private JButton btnEdit, btnDelete;
    private Long selectedInvoiceId = null;

    public InvoiceManagerPanel() {
        apiService = new InvoiceApiService();
        paymentApiService = new PaymentApiService();
        promotionService = new PromotionApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        JPanel topWrap = new JPanel();
        topWrap.setOpaque(false);
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Hóa đơn");
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
        JLabel lblSearch = new JLabel("Invoice ID:");
        lblSearch.setForeground(TEXT_SECONDARY);
        toolbarPanel.add(lblSearch);
        txtInvoiceIdSearch = new JTextField(18);
        txtInvoiceIdSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        toolbarPanel.add(txtInvoiceIdSearch);
        JButton btnSearch = createPrimaryButton("Tìm");
        btnSearch.addActionListener(e -> searchInvoiceById());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = createNeutralButton("Làm mới");
        btnRefresh.addActionListener(e -> loadInvoices());
        toolbarPanel.add(btnRefresh);
        toolbarPanel.add(Box.createHorizontalStrut(8));
        JLabel hint = new JLabel("Mẹo: bấm “👁 Xem” để xem/ghi nhận thanh toán.");
        hint.setForeground(TEXT_SECONDARY);
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        toolbarPanel.add(hint);

        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(toolbarPanel);
        add(topWrap, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Total Amount", "Issue Date", "Status", "Chi tiết"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblInvoice = new JTable(tableModel);
        tblInvoice.setRowHeight(34);
        tblInvoice.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblInvoice.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tblInvoice.getTableHeader().setBackground(HEADER_BG);
        tblInvoice.getTableHeader().setForeground(new Color(33, 33, 33));
        tblInvoice.getTableHeader().setReorderingAllowed(false);
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInvoice.setShowGrid(true);
        tblInvoice.setGridColor(new Color(235, 238, 245));
        tblInvoice.setSelectionBackground(new Color(227, 242, 253));
        tblInvoice.setSelectionForeground(new Color(33, 33, 33));
        tblInvoice.setDefaultRenderer(Object.class, zebraRenderer());
        tblInvoice.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        tblInvoice.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblInvoice.columnAtPoint(e.getPoint());
                int row = tblInvoice.rowAtPoint(e.getPoint());
                if (row >= 0 && col == 6) openDetailDialog(row);
            }
        });
        JScrollPane scrollPane = new JScrollPane(tblInvoice);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                new EmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(BG);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
        JButton btnAdd = createPrimaryButton("Thêm hóa đơn");
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit = createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete = createDangerButton("Xóa");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteInvoice());
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setMargin(new Insets(10, 18, 10, 18));
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createNeutralButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(236, 239, 241));
        b.setForeground(new Color(33, 33, 33));
        b.setMargin(new Insets(10, 18, 10, 18));
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createDangerButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        b.setMargin(new Insets(10, 18, 10, 18));
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        b.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private DefaultTableCellRenderer zebraRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 251, 253));
                }
                if (column == 6) {
                    c.setForeground(PRIMARY);
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    c.setForeground(new Color(33, 33, 33));
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
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

    private void openDetailDialog(int row) {
        Object idVal = tableModel.getValueAt(row, 0);
        if (idVal == null) return;
        Long id = ((Number) idVal).longValue();
        InvoiceDetailDialog dlg = new InvoiceDetailDialog(
                (Window) SwingUtilities.getWindowAncestor(this), id,
                apiService, paymentApiService, this::loadInvoices);
        dlg.setVisible(true);
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
                    inv.getStatus(),
                    "👁 Xem"
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
