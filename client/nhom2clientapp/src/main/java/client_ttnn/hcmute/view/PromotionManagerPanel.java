package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;

public class PromotionManagerPanel extends JPanel {

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
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadPromotions();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.add(new JLabel("Tìm theo Promotion ID:"));
        txtPromotionIdSearch = new JTextField(10);
        toolbarPanel.add(txtPromotionIdSearch);
        JButton btnSearchId = new JButton("Tìm");
        btnSearchId.addActionListener(e -> searchPromotionById());
        toolbarPanel.add(btnSearchId);
        Dimension refButtonSize = btnSearchId.getPreferredSize();

        toolbarPanel.add(new JLabel("Hoặc theo Code:"));
        txtCodeSearch = new JTextField(14);
        toolbarPanel.add(txtCodeSearch);
        JButton btnSearchCode = new JButton("Tìm Code");
        btnSearchCode.addActionListener(e -> searchPromotionByCode());
        toolbarPanel.add(btnSearchCode);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPromotions());
        toolbarPanel.add(btnRefresh);

        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Code", "Discount %", "Start Date", "End Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        promotionTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(promotionTable);
        
        promotionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        promotionTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateSelectionState();
        });

        JScrollPane scrollPane = new JScrollPane(promotionTable);
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
        btnDelete.addActionListener(e -> deletePromotion());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);
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
