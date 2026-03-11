package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Enrollment;
import client_ttnn.hcmute.model.Payment;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PaymentApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PaymentManagerPanel extends JPanel {
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color HEADER_BG = new Color(250, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(95, 99, 104);
    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color DANGER = new Color(211, 47, 47);

    private final PaymentApiService apiService;
    private JTable tblPayment;
    private DefaultTableModel tableModel;
    private JTextField txtPaymentIdSearch;
    private JButton btnEdit, btnDelete;
    private Long selectedPaymentId = null;

    public PaymentManagerPanel() {
        apiService = new PaymentApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadPayments();
    }

    private void initComponents() {
        JPanel topWrap = new JPanel();
        topWrap.setOpaque(false);
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Thanh toán");
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
        JLabel lblSearch = new JLabel("Payment ID:");
        lblSearch.setForeground(TEXT_SECONDARY);
        toolbarPanel.add(lblSearch);
        txtPaymentIdSearch = new JTextField(20);
        txtPaymentIdSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        toolbarPanel.add(txtPaymentIdSearch);
        JButton btnSearch = createPrimaryButton("Tìm");
        btnSearch.addActionListener(e -> searchPaymentById());
        toolbarPanel.add(btnSearch);
        Dimension refButtonSize = btnSearch.getPreferredSize();
        JButton btnRefresh = createNeutralButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPayments());
        toolbarPanel.add(btnRefresh);
        toolbarPanel.add(Box.createHorizontalStrut(8));
        JLabel hint = new JLabel("Khi thêm mới: chọn hóa đơn (Unpaid/Partial) để gắn thanh toán.");
        hint.setForeground(TEXT_SECONDARY);
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        toolbarPanel.add(hint);

        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(toolbarPanel);
        add(topWrap, BorderLayout.NORTH);

        String[] columns = {"ID", "Student ID", "Student Name", "Enrollment ID", "Amount", "Payment Date", "Method", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPayment = new JTable(tableModel);
        tblPayment.setRowHeight(34);
        tblPayment.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblPayment.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tblPayment.getTableHeader().setBackground(HEADER_BG);
        tblPayment.getTableHeader().setForeground(new Color(33, 33, 33));
        tblPayment.getTableHeader().setReorderingAllowed(false);
        tblPayment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPayment.setShowGrid(true);
        tblPayment.setGridColor(new Color(235, 238, 245));
        tblPayment.setSelectionBackground(new Color(227, 242, 253));
        tblPayment.setSelectionForeground(new Color(33, 33, 33));
        tblPayment.setDefaultRenderer(Object.class, zebraRenderer());
        tblPayment.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectionState();
        });
        JScrollPane scrollPane = new JScrollPane(tblPayment);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(BG);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
        JButton btnAdd = createPrimaryButton("Thêm thanh toán");
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit = createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete = createDangerButton("Xóa");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deletePayment());
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
                c.setForeground(new Color(33, 33, 33));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
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
