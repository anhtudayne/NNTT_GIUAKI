package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.service.RoomApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import client_ttnn.hcmute.util.ButtonStyles;

public class RoomFormDialog extends JDialog {

    private final RoomApiService apiService;
    private final boolean isEditMode;
    private final Room initialRoom;
    private final Runnable onSuccess;

    private JTextField txtRoomName;
    private JTextField txtCapacity;
    private JTextField txtLocation;
    private JComboBox<String> cmbStatus;

    public RoomFormDialog(Window owner, RoomApiService apiService,
                          boolean isEditMode, Room initialRoom, Runnable onSuccess) {
        super(owner, isEditMode ? "Cập nhật phòng học" : "Thêm phòng học mới", ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.isEditMode = isEditMode;
        this.initialRoom = initialRoom;
        this.onSuccess = onSuccess;

        setSize(820, 680);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        if (initialRoom != null) {
            fillForm(initialRoom);
        }
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setBackground(new Color(245, 247, 250));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel(isEditMode ? "Cập nhật phòng học" : "Thêm phòng học mới");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        content.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 10, 15, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thông tin phòng học",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                new Color(52, 73, 94)
            )
        ));
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

        int fieldCols = 42;
        txtRoomName = new JTextField(fieldCols);
        txtRoomName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        txtCapacity = new JTextField(fieldCols);
        txtCapacity.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        txtLocation = new JTextField(fieldCols);
        txtLocation.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        cmbStatus = new JComboBox<>(new String[]{"Available", "Maintenance", "Inactive"});
        cmbStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cmbStatus.setPreferredSize(new Dimension(txtRoomName.getPreferredSize().width, 30));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblName = new JLabel("Tên phòng:");
        lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblName, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtRoomName, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblCapacity = new JLabel("Sức chứa:");
        lblCapacity.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblCapacity, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtCapacity, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblLocation = new JLabel("Vị trí:");
        lblLocation.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblLocation, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(txtLocation, gbcField);
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);
        gbcField.gridx = 1; gbcField.gridy = row;
        formPanel.add(cmbStatus, gbcField);

        content.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton btnSave = ButtonStyles.createPrimaryButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        
        JButton btnCancel = ButtonStyles.createNeutralButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);
        getRootPane().setDefaultButton(btnSave);
    }

    private void fillForm(Room r) {
        if (r == null) return;
        txtRoomName.setText(r.getRoomName() != null ? r.getRoomName() : "");
        txtCapacity.setText(r.getCapacity() != null ? r.getCapacity().toString() : "");
        txtLocation.setText(r.getLocation() != null ? r.getLocation() : "");
        if (r.getStatus() != null) cmbStatus.setSelectedItem(r.getStatus());
    }

    private Room getRoomFromForm() {
        Room r = new Room();
        r.setRoomName(txtRoomName.getText().trim());
        try {
            r.setCapacity(Integer.parseInt(txtCapacity.getText().trim()));
        } catch (NumberFormatException e) {
            r.setCapacity(0);
        }
        r.setLocation(txtLocation.getText().trim());
        r.setStatus((String) cmbStatus.getSelectedItem());
        return r;
    }

    private boolean validateForm() {
        if (txtRoomName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtRoomName.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(txtCapacity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtCapacity.requestFocus();
            return false;
        }
        return true;
    }

    private void save() {
        if (!validateForm()) return;
        Room room = getRoomFromForm();
        try {
            if (isEditMode && initialRoom != null) {
                room.setRoomId(initialRoom.getRoomId());
                if (!apiService.updateRoom(room)) {
                    throw new Exception("Cập nhật thất bại.");
                }
                JOptionPane.showMessageDialog(this, "Cập nhật phòng học thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (apiService.createRoom(room) == null) {
                    throw new Exception("Thêm phòng học thất bại.");
                }
                JOptionPane.showMessageDialog(this, "Thêm phòng học thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
