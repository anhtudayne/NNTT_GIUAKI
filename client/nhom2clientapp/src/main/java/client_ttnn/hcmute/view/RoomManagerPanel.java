package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.service.RoomApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomManagerPanel extends JPanel {
    private final RoomApiService apiService;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtRoomName;
    private JTextField txtCapacity;
    private JTextField txtLocation;
    private JComboBox<String> cmbStatus;
    
    // Lọc theo sức chứa (Stream API)
    private JTextField txtMinCapacity;
    
    // Lọc theo trạng thái (Stream API)
    private JComboBox<String> cmbFilterStatus;
    
    private Long selectedRoomId = null;

    public RoomManagerPanel() {
        apiService = new RoomApiService();
        initComponents();
        loadRoomList();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel Lọc tìm kiếm dùng Stream (Top)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        searchPanel.add(new JLabel("Sức chứa tối thiểu:"));
        txtMinCapacity = new JTextField(5);
        searchPanel.add(txtMinCapacity);
        JButton btnFilterCapacity = new JButton("Lọc (Capacity)");
        btnFilterCapacity.addActionListener(e -> filterRoomsByCapacity());
        searchPanel.add(btnFilterCapacity);
        
        searchPanel.add(new JLabel("  |  Trạng thái:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Available", "Maintenance", "Inactive"});
        cmbFilterStatus.addActionListener(e -> filterRoomsByStatus());
        searchPanel.add(cmbFilterStatus);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            txtMinCapacity.setText("");
            cmbFilterStatus.setSelectedIndex(0);
            loadRoomList();
        });
        searchPanel.add(btnRefresh);

        add(searchPanel, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columns = {"ID", "Tên Phòng", "Sức chứa", "Vị trí", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(30);
        roomTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roomTable.getSelectedRow() != -1) {
                loadSelectedRoom();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Form điền thông tin bên phải màn hình
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Phòng học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtRoomName = new JTextField(20);
        txtCapacity = new JTextField(20);
        txtLocation = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"Available", "Maintenance", "Inactive"});

        int rowCount = 0;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Tên phòng:"), gbc);
        gbc.gridx = 1; formPanel.add(txtRoomName, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Sức chứa (Số ghế):"), gbc);
        gbc.gridx = 1; formPanel.add(txtCapacity, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Vị trí (VD: Tầng 1):"), gbc);
        gbc.gridx = 1; formPanel.add(txtLocation, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; formPanel.add(cmbStatus, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> createRoom());
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateRoom());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteRoom());
        JButton btnClear = new JButton("Xóa Form");
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    private void loadRoomList() {
        try {
            updateTable(apiService.getAllRooms());
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải DS Phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterRoomsByStatus() {
        String selectedStatus = (String) cmbFilterStatus.getSelectedItem();
        txtMinCapacity.setText(""); // Reset filter capacity
        if ("Tất cả".equals(selectedStatus)) {
            loadRoomList();
            return;
        }
        try {
            updateTable(apiService.getRoomsByStatus(selectedStatus));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc Trạng thái: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterRoomsByCapacity() {
        String capacityStr = txtMinCapacity.getText().trim();
        cmbFilterStatus.setSelectedIndex(0); // Reset filter status
        if (capacityStr.isEmpty()) {
            loadRoomList();
            return;
        }
        try {
            int minCap = Integer.parseInt(capacityStr);
            updateTable(apiService.getRoomsByMinCapacity(minCap));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMinCapacity.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm bằng Sức chứa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room r : rooms) {
            Object[] row = {
                r.getRoomId(),
                r.getRoomName(),
                r.getCapacity(),
                r.getLocation(),
                r.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow != -1) {
            // Check based on data type from model mapping. We map standard Long from App model
            Object idObj = tableModel.getValueAt(selectedRow, 0);
            if (idObj instanceof Integer) {
                selectedRoomId = ((Integer) idObj).longValue();
            } else if (idObj instanceof Double) {
                selectedRoomId = ((Double) idObj).longValue();
            } else if (idObj instanceof Long) {
                selectedRoomId = (Long) idObj;
            }
            
            txtRoomName.setText(tableModel.getValueAt(selectedRow, 1) != null ? String.valueOf(tableModel.getValueAt(selectedRow, 1)) : "");
            
            Object capObj = tableModel.getValueAt(selectedRow, 2);
            txtCapacity.setText(capObj != null ? String.valueOf(capObj) : "");

            txtLocation.setText(tableModel.getValueAt(selectedRow, 3) != null ? String.valueOf(tableModel.getValueAt(selectedRow, 3)) : "");
            Object statusObj = tableModel.getValueAt(selectedRow, 4);
            if (statusObj != null) cmbStatus.setSelectedItem(String.valueOf(statusObj));
        }
    }

    private void createRoom() {
        if (!validateForm()) return;
        try {
            apiService.createRoom(getRoomFromForm());
            JOptionPane.showMessageDialog(this, "Thêm Phòng học thành công!");
            loadRoomList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        if (selectedRoomId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Phòng học.");
            return;
        }
        if (!validateForm()) return;
        try {
            Room r = getRoomFromForm();
            r.setRoomId(selectedRoomId);
            apiService.updateRoom(r);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadRoomList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        if (selectedRoomId == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn Phòng học cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa Phòng học này sẽ không thể khôi phục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteRoom(selectedRoomId);
                JOptionPane.showMessageDialog(this, "Xóa thành công.");
                loadRoomList();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Room getRoomFromForm() {
        Room r = new Room();
        r.setRoomName(txtRoomName.getText().trim());
        try {
            r.setCapacity(Integer.parseInt(txtCapacity.getText().trim()));
        } catch (Exception e) {
            r.setCapacity(0);
        }
        r.setLocation(txtLocation.getText().trim());
        r.setStatus((String) cmbStatus.getSelectedItem());
        return r;
    }

    private boolean validateForm() {
        if (txtRoomName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cần nhập Tên phòng!");
            return false;
        }
        try {
            Integer.parseInt(txtCapacity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là một số nguyên hợp lệ!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedRoomId = null;
        txtRoomName.setText("");
        txtCapacity.setText("");
        txtLocation.setText("");
        cmbStatus.setSelectedIndex(0);
        roomTable.clearSelection();
    }
}
