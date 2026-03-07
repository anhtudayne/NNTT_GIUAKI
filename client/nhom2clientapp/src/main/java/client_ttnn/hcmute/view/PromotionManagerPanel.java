package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Promotion;
import client_ttnn.hcmute.service.PromotionApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class PromotionManagerPanel extends JPanel {

    private final PromotionApiService apiService;
    private JTable tblPromotion;
    private DefaultTableModel tableModel;

    private JTextField txtPromotionIdSearch;
    private JTextField txtPromoCode;
    private JTextField txtDiscountPercent;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextArea txtDescription;

    private Long selectedPromotionId = null;

    public PromotionManagerPanel() {
        this.apiService = new PromotionApiService();
        initComponents();
        loadPromotions();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Tìm theo Promotion ID:"));
        txtPromotionIdSearch = new JTextField(12);
        searchPanel.add(txtPromotionIdSearch);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchPromotionById());
        searchPanel.add(btnSearch);

        searchPanel.add(new JLabel("Hoặc theo Code:"));
        JTextField txtCodeSearch = new JTextField(10);
        searchPanel.add(txtCodeSearch);
        JButton btnSearchCode = new JButton("Tìm Code");
        btnSearchCode.addActionListener(e -> searchPromotionByCode(txtCodeSearch.getText().trim()));
        searchPanel.add(btnSearchCode);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadPromotions());
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Code", "Discount (%)", "Start Date", "End Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPromotion = new JTable(tableModel);
        tblPromotion.setRowHeight(26);
        tblPromotion.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblPromotion.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPromotion.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPromotion.getSelectedRow() != -1) {
                loadSelectedPromotion();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPromotion);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10),
                UIManager.getBorder("ScrollPane.border")));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khuyến mãi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPromoCode = new JTextField(20);
        txtDiscountPercent = new JTextField(20);
        txtStartDate = new JTextField(20);
        txtEndDate = new JTextField(20);
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã khuyến mãi:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPromoCode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Giảm (%)"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDiscountPercent, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEndDate, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        JScrollPane descScroll = new JScrollPane(txtDescription);
        formPanel.add(descScroll, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createPromotion());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updatePromotion());
        JButton btnDelete = new JButton("Xoá");
        btnDelete.addActionListener(e -> deletePromotion());
        JButton btnClear = new JButton("Xoá trắng form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadPromotions() {
        try {
            updateTable(apiService.getAllPromotions());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách khuyến mãi: " + e.getMessage(),
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
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Promotion ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPromotionByCode(String code) {
        if (code.isEmpty()) {
            loadPromotions();
            return;
        }
        try {
            Promotion p = apiService.findByCode(code);
            updateTable(Collections.singletonList(p));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy promotion code: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Promotion> list) {
        tableModel.setRowCount(0);
        for (Promotion p : list) {
            Object[] row = {
                    p.getPromotionId(),
                    p.getPromoCode(),
                    p.getDiscountPercent(),
                    p.getStartDate(),
                    p.getEndDate(),
                    p.getDescription()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedPromotion() {
        int row = tblPromotion.getSelectedRow();
        if (row == -1) return;

        Object idVal = tableModel.getValueAt(row, 0);
        selectedPromotionId = parseLong(idVal);

        txtPromoCode.setText(valueOf(tableModel.getValueAt(row, 1)));
        txtDiscountPercent.setText(valueOf(tableModel.getValueAt(row, 2)));
        txtStartDate.setText(valueOf(tableModel.getValueAt(row, 3)));
        txtEndDate.setText(valueOf(tableModel.getValueAt(row, 4)));
        txtDescription.setText(valueOf(tableModel.getValueAt(row, 5)));
    }

    private void createPromotion() {
        if (!validateForm()) return;
        try {
            apiService.createPromotion(getPromotionFromForm());
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPromotions();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khuyến mãi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePromotion() {
        if (selectedPromotionId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần cập nhật!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        try {
            apiService.updatePromotion(selectedPromotionId, getPromotionFromForm());
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadPromotions();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khuyến mãi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePromotion() {
        if (selectedPromotionId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xoá!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xoá khuyến mãi này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deletePromotion(selectedPromotionId);
                JOptionPane.showMessageDialog(this, "Xoá khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadPromotions();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xoá khuyến mãi: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Promotion getPromotionFromForm() {
        Promotion p = new Promotion();
        p.setPromoCode(txtPromoCode.getText().trim());
        String discountText = txtDiscountPercent.getText().trim();
        p.setDiscountPercent(new BigDecimal(discountText));
        p.setStartDate(txtStartDate.getText().trim());
        p.setEndDate(txtEndDate.getText().trim());
        p.setDescription(txtDescription.getText().trim());
        return p;
    }

    private boolean validateForm() {
        if (txtPromoCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã khuyến mãi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDiscountPercent.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập phần trăm giảm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(txtDiscountPercent.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá trị phần trăm giảm không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedPromotionId = null;
        txtPromoCode.setText("");
        txtDiscountPercent.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        txtDescription.setText("");
        tblPromotion.clearSelection();
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

