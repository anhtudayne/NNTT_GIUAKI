package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.PlacementTest;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.PlacementTestApiService;
import client_ttnn.hcmute.service.StudentApiService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlacementTestFormDialog extends JDialog {
    private final PlacementTestApiService apiService;
    private final boolean isEditMode;
    private final PlacementTest initial;
    private final Runnable onSuccess;

    private final StudentApiService studentApiService;
    private JComboBox<Student> cmbStudent;
    private JDateChooser dcTestDate;
    private JTextField txtScore;
    private JComboBox<String> cmbRecommendedLevel;
    private List<Student> allStudents = new ArrayList<>();
    private boolean updatingStudentCombo = false;

    public PlacementTestFormDialog(Window owner, PlacementTestApiService apiService,
                                  boolean isEditMode, PlacementTest initial, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật Placement Test" : "Thêm Placement Test mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initial = initial;
        this.onSuccess = onSuccess;
        this.studentApiService = new StudentApiService();
        setSize(720, 520);
        setMinimumSize(new Dimension(560, 420));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        loadStudents();
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

        int cols = 40;
        cmbStudent = new JComboBox<>();
        cmbStudent.setEditable(true);
        cmbStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student s) {
                    String id = s.getId() != null ? String.valueOf(s.getId()) : "--";
                    String name = s.getFullName() != null ? s.getFullName() : "";
                    setText(id + " - " + name);
                }
                return this;
            }
        });

        dcTestDate = new JDateChooser();
        dcTestDate.setDateFormatString("yyyy-MM-dd");
        txtScore = new JTextField(cols);
        cmbRecommendedLevel = new JComboBox<>(new String[]{"", "Beginner", "Intermediate", "Advanced"});

        int row = 0;
        addRow(formPanel, gbc, gbcField, row++, "Học viên:", withClearButton(cmbStudent));
        addRow(formPanel, gbc, gbcField, row++, "Ngày test:", dcTestDate);
        addRow(formPanel, gbc, gbcField, row++, "Điểm (0 - 10):", txtScore);
        addRow(formPanel, gbc, gbcField, row++, "Trình độ khuyến nghị:", cmbRecommendedLevel);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        content.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);

        hookStudentFilter();
        hookScoreToLevel();
    }

    private void addRow(JPanel p, GridBagConstraints gbc, GridBagConstraints gbcField, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        p.add(field, gbcField);
    }

    private JComponent withClearButton(JComboBox<?> combo) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 0));
        wrapper.setOpaque(false);
        JButton btnClear = new JButton("Xoa");
        btnClear.setMargin(new Insets(2, 8, 2, 8));
        btnClear.setToolTipText("Xoa lua chon");
        btnClear.addActionListener(e -> {
            combo.setSelectedItem(null);
            Object editorComp = combo.getEditor() != null ? combo.getEditor().getEditorComponent() : null;
            if (editorComp instanceof JTextField editor) {
                editor.setText("");
            }
            combo.hidePopup();
        });
        wrapper.add(combo, BorderLayout.CENTER);
        wrapper.add(btnClear, BorderLayout.EAST);
        return wrapper;
    }

    private void loadStudents() {
        try {
            allStudents = studentApiService.getAllStudents();
            refreshStudentCombo(allStudents);
        } catch (Exception e) {
            // ignore
        }
    }

    private void refreshStudentCombo(List<Student> students) {
        updatingStudentCombo = true;
        Student selected = (Student) cmbStudent.getSelectedItem();
        DefaultComboBoxModel<Student> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        if (students != null) {
            for (Student s : students) model.addElement(s);
        }
        cmbStudent.setModel(model);
        if (selected != null) cmbStudent.setSelectedItem(selected);
        updatingStudentCombo = false;
    }

    private void hookStudentFilter() {
        Component editorComp = cmbStudent.getEditor().getEditorComponent();
        if (!(editorComp instanceof JTextField editor)) return;
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                if (updatingStudentCombo) return;
                String q = editor.getText() != null ? editor.getText().trim().toLowerCase() : "";
                SwingUtilities.invokeLater(() -> {
                    if (q.isEmpty()) {
                        refreshStudentCombo(allStudents);
                        cmbStudent.setSelectedItem(null);
                        cmbStudent.hidePopup();
                        return;
                    }
                    List<Student> filtered = new ArrayList<>();
                    for (Student s : allStudents) {
                        String id = s.getId() != null ? String.valueOf(s.getId()) : "";
                        String name = s.getFullName() != null ? s.getFullName().toLowerCase() : "";
                        if (id.contains(q) || name.contains(q)) filtered.add(s);
                    }
                    refreshStudentCombo(filtered);
                    cmbStudent.showPopup();
                });
            }
        });
    }

    /**
     * Tự động gán RecommendedLevel khi người dùng nhập điểm.
     * Quy tắc khớp backend:
     *  - < 4.0  -> Beginner
     *  - < 6.0  -> Intermediate
     *  - >= 6.0 -> Advanced
     */
    private void hookScoreToLevel() {
        txtScore.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateLevel(); }
            @Override public void removeUpdate(DocumentEvent e) { updateLevel(); }
            @Override public void changedUpdate(DocumentEvent e) { updateLevel(); }
            private void updateLevel() {
                String text = txtScore.getText().trim();
                if (text.isEmpty()) {
                    cmbRecommendedLevel.setSelectedItem("");
                    return;
                }
                try {
                    double s = Double.parseDouble(text);
                    String level;
                    if (s < 4.0) level = "Beginner";
                    else if (s < 6.5) level = "Intermediate";
                    else level = "Advanced";
                    cmbRecommendedLevel.setSelectedItem(level);
                } catch (NumberFormatException ex) {
                    // không đổi level nếu score không parse được
                }
            }
        });
    }

    private void fillForm(PlacementTest p) {
        if (p == null) return;
        if (p.getStudent() != null) cmbStudent.setSelectedItem(p.getStudent());
        dcTestDate.setDate(parseDate(p.getTestDate()));
        if (p.getScore() != null) txtScore.setText(String.valueOf(p.getScore()));
        if (p.getRecommendedLevel() != null) cmbRecommendedLevel.setSelectedItem(p.getRecommendedLevel());
    }

    private PlacementTest getFromForm() {
        PlacementTest p = new PlacementTest();
        Student selected = (Student) cmbStudent.getSelectedItem();
        if (selected != null && selected.getId() != null) {
            Student s = new Student();
            s.setId(selected.getId());
            p.setStudent(s);
        }
        p.setTestDate(formatDate(dcTestDate.getDate()));
        try { p.setScore(Double.parseDouble(txtScore.getText().trim())); } catch (NumberFormatException e) { p.setScore(null); }
        String level = (String) cmbRecommendedLevel.getSelectedItem();
        if (level != null && !level.isBlank()) {
            p.setRecommendedLevel(level);
        }
        return p;
    }

    private boolean validateForm() {
        Student selected = (Student) cmbStudent.getSelectedItem();
        if (selected == null || selected.getId() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (dcTestDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày test.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!txtScore.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(txtScore.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
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

    private static String formatDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    private static Date parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
