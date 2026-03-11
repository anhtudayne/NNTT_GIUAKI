package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.service.RoomApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import client_ttnn.hcmute.util.TableCustomizer;
import client_ttnn.hcmute.util.ButtonStyles;

public class RoomManagerPanel extends JPanel {

    private final RoomApiService apiService;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField txtMinCapacity;
    private JComboBox<String> cmbFilterStatus;
    private JButton btnEdit;
    private JButton btnDelete;
    private Long selectedRoomId = null;

    public RoomManagerPanel() {
        apiService = new RoomApiService();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        initComponents();
        loadRoomList();
    }

    private void initComponents() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbarPanel.setBackground(Color.WHITE);
        
        toolbarPanel.add(new JLabel("Sức chứa tối thiểu:"));
        txtMinCapacity = new JTextField(8);
        toolbarPanel.add(txtMinCapacity);
        
        JButton btnFilterCapacity = ButtonStyles.createPrimaryButton("Lọc Capacity");
        btnFilterCapacity.addActionListener(e -> filterRoomsByCapacity());
        toolbarPanel.add(btnFilterCapacity);

        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(new JLabel("Trạng thái:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Available", "Maintenance", "Inactive"});
        cmbFilterStatus.addActionListener(e -> filterRoomsByStatus());
        toolbarPanel.add(cmbFilterStatus);

        JButton btnRefresh = ButtonStyles.createNeutralButton("Làm mới");
        btnRefresh.addActionListener(e -> {
            txtMinCapacity.setText("");
            cmbFilterStatus.setSelectedIndex(0);
            loadRoomList();
        });
        toolbarPanel.add(btnRefresh);

        add(toolbarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên Phòng", "Sức chứa", "Vị trí", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(roomTable);
        
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateSelectionState();
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottomPanel.setMinimumSize(new Dimension(400, 50));
        JButton btnAdd = ButtonStyles.createPrimaryButton("Thêm");
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = ButtonStyles.createNeutralButton("Sửa");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> openEditDialog());

        btnDelete = ButtonStyles.createDangerButton("Xóa");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteRoom());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateSelectionState() {
        int row = roomTable.getSelectedRow();
        if (row >= 0) {
            Object idVal = tableModel.getValueAt(row, 0);
            selectedRoomId = idVal != null ? ((Number) idVal).longValue() : null;
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            selectedRoomId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private void openAddDialog() {
        RoomFormDialog dlg = new RoomFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                false,
                null,
                this::loadRoomList
        );
        dlg.setVisible(true);
    }

    private void openEditDialog() {
        if (selectedRoomId == null) return;
        Room selected = getSelectedRoomFromTable();
        if (selected == null) return;
        RoomFormDialog dlg = new RoomFormDialog(
                (Window) SwingUtilities.getWindowAncestor(this),
                apiService,
                true,
                selected,
                this::loadRoomList
        );
        dlg.setVisible(true);
    }

    private Room getSelectedRoomFromTable() {
        int row = roomTable.getSelectedRow();
        if (row < 0) return null;
        Room r = new Room();
        r.setRoomId(selectedRoomId);
        r.setRoomName(str(tableModel.getValueAt(row, 1)));
        Object cap = tableModel.getValueAt(row, 2);
        r.setCapacity(cap != null ? ((Number) cap).intValue() : null);
        r.setLocation(str(tableModel.getValueAt(row, 3)));
        r.setStatus(str(tableModel.getValueAt(row, 4)));
        return r;
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private void loadRoomList() {
        try {
            List<Room> list = apiService.getAllRooms();
            updateTable(list);
            selectedRoomId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterRoomsByCapacity() {
        String capacityStr = txtMinCapacity.getText().trim();
        cmbFilterStatus.setSelectedIndex(0);
        if (capacityStr.isEmpty()) {
            loadRoomList();
            return;
        }
        try {
            int minCap = Integer.parseInt(capacityStr);
            List<Room> list = apiService.getRoomsByMinCapacity(minCap);
            updateTable(list);
            selectedRoomId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterRoomsByStatus() {
        String selectedStatus = (String) cmbFilterStatus.getSelectedItem();
        if ("Tất cả".equals(selectedStatus)) {
            loadRoomList();
            return;
        }
        try {
            List<Room> list = apiService.getRoomsByStatus(selectedStatus);
            updateTable(list);
            selectedRoomId = null;
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc trạng thái: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomName(),
                    r.getCapacity(),
                    r.getLocation(),
                    r.getStatus()
            });
        }
    }

    private void deleteRoom() {
        if (selectedRoomId == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng học này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (!apiService.deleteRoom(selectedRoomId)) {
                throw new Exception("Xóa thất bại.");
            }
            JOptionPane.showMessageDialog(this, "Đã xóa phòng học.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadRoomList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
