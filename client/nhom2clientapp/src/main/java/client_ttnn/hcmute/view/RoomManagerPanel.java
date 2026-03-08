package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.service.RoomApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
        toolbarPanel.setBackground(new Color(236, 240, 241));
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(22, 160, 133)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblCapacity = new JLabel("🏢 Sức chứa tối thiểu:");
        lblCapacity.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        toolbarPanel.add(lblCapacity);
        txtMinCapacity = new JTextField(8);
        txtMinCapacity.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtMinCapacity.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        toolbarPanel.add(txtMinCapacity);
        
        JButton btnFilterCapacity = new JButton("Lọc Capacity");
        btnFilterCapacity.setBackground(new Color(22, 160, 133));
        btnFilterCapacity.setForeground(Color.WHITE);
        btnFilterCapacity.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnFilterCapacity.setFocusPainted(false);
        btnFilterCapacity.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnFilterCapacity.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilterCapacity.addActionListener(e -> filterRoomsByCapacity());
        toolbarPanel.add(btnFilterCapacity);
        Dimension refButtonSize = btnFilterCapacity.getPreferredSize();

        toolbarPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        toolbarPanel.add(lblStatus);
        cmbFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Available", "Maintenance", "Inactive"});
        cmbFilterStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cmbFilterStatus.addActionListener(e -> filterRoomsByStatus());
        toolbarPanel.add(cmbFilterStatus);

        JButton btnRefresh = new JButton("⟳ Làm mới");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        roomTable.setRowHeight(36);
        roomTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        roomTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        roomTable.getTableHeader().setBackground(new Color(52, 73, 94));
        roomTable.getTableHeader().setForeground(Color.WHITE);
        roomTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.setSelectionBackground(new Color(130, 224, 170));
        roomTable.setSelectionForeground(new Color(44, 62, 80));
        roomTable.setShowGrid(true);
        roomTable.setGridColor(new Color(220, 220, 220));
        roomTable.setIntercellSpacing(new Dimension(1, 1));

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

        Dimension btnSize = new Dimension(130, 40);
        JButton btnAdd = new JButton("➕ Thêm");
        btnAdd.setPreferredSize(btnSize);
        btnAdd.setMinimumSize(btnSize);
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openAddDialog());

        btnEdit = new JButton("✏️ Xem/Sửa");
        btnEdit.setPreferredSize(btnSize);
        btnEdit.setMinimumSize(btnSize);
        btnEdit.setEnabled(false);
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorderPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> openEditDialog());

        btnDelete = new JButton("🗑️ Xóa");
        btnDelete.setPreferredSize(btnSize);
        btnDelete.setMinimumSize(btnSize);
        btnDelete.setEnabled(false);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
