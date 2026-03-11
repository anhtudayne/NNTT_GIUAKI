package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Promotion;
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
import client_ttnn.hcmute.util.TableCustomizer;

public class PromotionManagerPanel extends JPanel {
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color HEADER_BG = new Color(250, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(95, 99, 104);
    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color DANGER = new Color(211, 47, 47);

    private final PromotionApiService apiService;
    private JTable promotionTable;
    private DefaultTableModel tableModel;
    private JTextField txtPromotionIdSearch;
    private JTextField txtCodeSearch;
    private JButton btnEdit;
    private JButton btnDelete;
    private Long selectedPromotionId = null;

    public PromotionManagerPanel() {
        apiService = new PromotionApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadPromotions();
    }

    private void initComponents() {
        JPanel topWrap = new JPanel();
        topWrap.setOpaque(false);
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Khuyến mãi");
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

        JLabel lblId = new JLabel("Promotion ID:");
        lblId.setForeground(TEXT_SECONDARY);
        toolbarPanel.add(lblId);
        txtPromotionIdSearch = new JTextField(10);
        txtPromotionIdSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        toolbarPanel.add(txtPromotionIdSearch);
        JButton btnSearchId = createPrimaryButton("Tìm");
        btnSearchId.addActionListener(e -> searchPromotionById());
        toolbarPanel.add(btnSearchId);
        Dimension refButtonSize = btnSearchId.getPreferredSize();

        JLabel lblCode = new JLabel("Code:");
        lblCode.setForeground(TEXT_SECONDARY);
        toolbarPanel.add(lblCode);
        txtCodeSearch = new JTextField(14);
        txtCodeSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        toolbarPanel.add(txtCodeSearch);
        JButton btnSearchCode = createNeutralButton("Tìm code");
        btnSearchCode.addActionListener(e -> searchPromotionByCode());
        toolbarPanel.add(btnSearchCode);

        JButton btnRefresh = createNeutralButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPromotions());
        toolbarPanel.add(btnRefresh);

        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topWrap.add(toolbarPanel);
        add(topWrap, BorderLayout.NORTH);

        String[] columns = {"ID", "Code", "Discount %", "Start Date", "End Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        promotionTable = new JTable(tableModel);
        promotionTable.setRowHeight(34);
        promotionTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        promotionTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        promotionTable.getTableHeader().setBackground(HEADER_BG);
        promotionTable.getTableHeader().setForeground(new Color(33, 33, 33));
        promotionTable.getTableHeader().setReorderingAllowed(false);
        promotionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        promotionTable.setShowGrid(true);
        promotionTable.setGridColor(new Color(235, 238, 245));
        promotionTable.setSelectionBackground(new Color(227, 242, 253));
        promotionTable.setSelectionForeground(new Color(33, 33, 33));
        promotionTable.setDefaultRenderer(Object.class, zebraRenderer());

        promotionTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateSelectionState();
        });

        JScrollPane scrollPane = new JScrollPane(promotionTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(BG);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));

        JButton btnAdd = createPrimaryButton("Thêm khuyến mãi");
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());

        btnDelete = createDangerButton("Xóa");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deletePromotion());

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
        int row = promotionTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedPromotionId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedPromotionId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        PromotionFormDialog dlg = new PromotionFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                false,
                null,
                this::loadPromotions
        );
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedPromotionId == null) return;
        Promotion selected = getSelectedPromotionFromTable();
        if (selected == null) return;
        PromotionFormDialog dlg = new PromotionFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                true,
                selected,
                this::loadPromotions
        );
        dlg.setVisible(true);
    }

    private Promotion getSelectedPromotionFromTable() {
        int row = promotionTable.getSelectedRow();
        if (row < 0) return null;
        Promotion p = new Promotion();
        p.setPromotionId(selectedPromotionId);
        p.setPromoCode(str(tableModel.getValueAt(row, 1)));
        Object discount = tableModel.getValueAt(row, 2);
        if (discount != null) {
            try {
                p.setDiscountPercent(new java.math.BigDecimal(discount.toString()));
            } catch (NumberFormatException e) {
                p.setDiscountPercent(null);
            }
        }
        p.setStartDate(str(tableModel.getValueAt(row, 3)));
        p.setEndDate(str(tableModel.getValueAt(row, 4)));
        p.setDescription(str(tableModel.getValueAt(row, 5)));
        return p;
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private void loadPromotions() {
        try {
            List<Promotion> list = apiService.getAllPromotions();
            updateTable(list);
            selectedPromotionId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPromotionById() {
        String text = txtPromotionIdSearch.getText().trim();
        if (text.isEmpty()) {
            loadPromotions();
            return;
        }
        try {
            Long id = Long.parseLong(text);
            Promotion p = apiService.getPromotionById(id);
            updateTable(Collections.singletonList(p));
            selectedPromotionId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Promotion ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPromotionByCode() {
        String code = txtCodeSearch.getText().trim();
        if (code.isEmpty()) {
            loadPromotions();
            return;
        }
        try {
            Promotion p = apiService.findByCode(code);
            updateTable(Collections.singletonList(p));
            selectedPromotionId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy code: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Promotion> list) {
        tableModel.setRowCount(0);
        for (Promotion p : list) {
            tableModel.addRow(new Object[]{
                    p.getPromotionId(),
                    p.getPromoCode(),
                    p.getDiscountPercent(),
                    p.getStartDate(),
                    p.getEndDate(),
                    p.getDescription()
            });
        }
    }

    private void deletePromotion() {
        if (selectedPromotionId == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khuyến mãi này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            apiService.deletePromotion(selectedPromotionId);
            JOptionPane.showMessageDialog(this, "Đã xóa khuyến mãi.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPromotions();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
