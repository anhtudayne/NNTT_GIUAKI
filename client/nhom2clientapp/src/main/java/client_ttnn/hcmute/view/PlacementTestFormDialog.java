package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.PlacementTest;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PlacementTestApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlacementTestFormDialog extends JDialog {
    private final PlacementTestApiService apiService;
    private final boolean isEditMode;
    private final PlacementTest initial;
    private final Runnable onSuccess;

    private JTextField txtStudentId, txtTestDate, txtScore, txtRecommendedLevel;

    public PlacementTestFormDialog(Window owner, PlacementTestApiService apiService,
                                  boolean isEditMode, PlacementTest initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật Placement Test" : "Thêm Placement Test mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initial = initial;
        this.onSuccess = onSuccess;
        setSize(820, 580);
        setMinimumSize(new Dimension(560, 420));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initial != null) fillForm(initial);
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setBackground(Color.WHITE);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.insets = new Insets(8, 10, 8, 10);
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.anchor = GridBagConstraints.WEST;
        gbcField.weightx = 1.0;

        int cols = 42;
        txtStudentId = new JTextField(cols);
        txtTestDate = new JTextField(cols);
        txtTestDate.setToolTipText("yyyy-MM-dd");
        txtScore = new JTextField(cols);
        txtRecommendedLevel = new JTextField(cols);

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Student ID:", txtStudentId);
        addRow(formPanel, gbc, gbcField, row++, "Ngày test (yyyy-MM-dd):", txtTestDate);
        addRow(formPanel, gbc, gbcField, row++, "Score:", txtScore);
        addRow(formPanel, gbc, gbcField, row++, "Recommended Level:", txtRecommendedLevel);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(Color.WHITE);
        Dimension refBtn = new JButton("Tìm kiếm").getPreferredSize();
        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(refBtn);
        btnSave.setMinimumSize(refBtn);
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(refBtn);
        btnCancel.setMinimumSize(refBtn);
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        content.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, GridBagConstraints gbcField, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        p.add(field, gbcField);
    }

    private void fillForm(PlacementTest p) {
        if (p == null) return;
        if (p.getStudent() != null && p.getStudent().getId() != null) txtStudentId.setText(String.valueOf(p.getStudent().getId()));
        txtTestDate.setText(str(p.getTestDate()));
        if (p.getScore() != null) txtScore.setText(String.valueOf(p.getScore()));
        txtRecommendedLevel.setText(str(p.getRecommendedLevel()));
    }

    private PlacementTest getFromForm() {
        PlacementTest p = new PlacementTest();
        Student s = new Student();
        s.setId(Long.parseLong(txtStudentId.getText().trim()));
        p.setStudent(s);
        p.setTestDate(txtTestDate.getText().trim());
        try { p.setScore(Double.parseDouble(txtScore.getText().trim())); } catch (NumberFormatException e) { p.setScore(null); }
        p.setRecommendedLevel(txtRecommendedLevel.getText().trim());
        return p;
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập Student ID.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        if (txtTestDate.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày test.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        try { Long.parseLong(txtStudentId.getText().trim()); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Student ID phải là số.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return false; }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        try {
            PlacementTest p = getFromForm();
            if (isEditMode && initial != null) {
                apiService.updatePlacementTest(initial.getPlacementTestId(), p);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                apiService.createPlacementTest(p);
                JOptionPane.showMessageDialog(this, "Thêm placement test thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
