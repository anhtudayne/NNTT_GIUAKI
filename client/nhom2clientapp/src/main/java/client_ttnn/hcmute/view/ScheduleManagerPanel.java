package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Schedule;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.service.ScheduleApiService;
import client_ttnn.hcmute.util.CacheManager;
import client_ttnn.hcmute.util.TableCustomizer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import client_ttnn.hcmute.dto.BatchScheduleRequest;
import client_ttnn.hcmute.util.ButtonStyles;
public class ScheduleManagerPanel extends JPanel {
    private final ScheduleApiService apiService;
    private final CacheManager cacheManager;
    
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JComboBox<ClassItem> cmbClass;
    private JTextField txtDate; // YYYY-MM-DD (Hoặc StartDate)
    private JTextField txtEndDate; // YYYY-MM-DD
    private JComboBox<String> cmbStartTime;
    private JComboBox<String> cmbEndTime;
    
    // Batch Mode Components
    private JCheckBox chkBatchMode;
    private JCheckBox[] chkDays;
    private JPanel daysPanel;
    private JLabel lblDateLabel;
    private JLabel lblEndDate;
    private JComboBox<RoomItem> cmbRoom;
    private JLabel lblTeacherInfo;
    
    // Nút check stream api
    private JButton btnCheckAvailable;
    
    private Integer selectedScheduleId = null;

    public ScheduleManagerPanel() {
        apiService = new ScheduleApiService();
        cacheManager = CacheManager.getInstance();
        initComponents();
        loadDataAsync();
    }

    private void loadDataAsync() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Classes> classes;
            private List<Schedule> schedules;
            private Exception error;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    classes = cacheManager.getClasses();
                    schedules = apiService.getAllSchedules();
                } catch (Exception e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                if (error != null) {
                    JOptionPane.showMessageDialog(ScheduleManagerPanel.this, "Lỗi tải dữ liệu: " + error.getMessage());
                    return;
                }
                
                // Update Classes Combo
                cmbClass.removeAllItems();
                if (classes != null) {
                    for (Classes c : classes) {
                        cmbClass.addItem(new ClassItem(c));
                    }
                }
                
                // Update Schedule Table
                tableModel.setRowCount(0);
                if (schedules != null) {
                    for (Schedule s : schedules) {
                        String className = s.getClassEntity() != null ? s.getClassEntity().getClassName() : "N/A";
                        String teacherName = (s.getClassEntity() != null && s.getClassEntity().getTeacher() != null) 
                                                ? s.getClassEntity().getTeacher().getFullName() : "N/A";
                        String roomName = s.getRoom() != null ? s.getRoom().getRoomName() : "N/A";
                        
                        Object[] row = {
                            s.getScheduleId(),
                            className,
                            teacherName,
                            roomName,
                            s.getDate(),
                            s.getStartTime(),
                            s.getEndTime()
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        };
        worker.execute();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE); // Add this to ensure base panel is white

        // Bảng dữ liệu chính
        String[] columns = {"ID", "Lớp Học", "Giảng viên", "Phòng", "Ngày", "Bắt đầu", "Kết thúc"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(scheduleTable);
        
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && scheduleTable.getSelectedRow() != -1) {
                loadSelectedSchedule();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));
                
        add(scrollPane, BorderLayout.CENTER);

        // Bên trái là form Xếp lịch với logic khó
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE); // Loại bỏ màu nền xám mặc định
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Xếp Lịch Học Mới"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Thêm Padding đẹp hơn
        ));
        formPanel.setPreferredSize(new Dimension(450, 0)); // Ép độ rộng Form tối thiểu để không bị bảng lấn

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Tăng khoảng cách các dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        cmbClass = new JComboBox<>();
        
        lblDateLabel = new JLabel("Ngày học (YYYY-MM-DD):");
        txtDate = new JTextField(10);
        txtDate.setToolTipText("YYYY-MM-DD");
        txtDate.setText(LocalDate.now().toString());
        
        lblEndDate = new JLabel("Đến ngày (YYYY-MM-DD):");
        txtEndDate = new JTextField(10);
        txtEndDate.setToolTipText("YYYY-MM-DD");
        txtEndDate.setText(LocalDate.now().plusMonths(1).toString());
        
        // --- COMBOBOX THỜI GIAN ---
        String[] timeOptions = {
            "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", 
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", 
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", 
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30"
        };
        cmbStartTime = new JComboBox<>(timeOptions);
        cmbStartTime.setSelectedItem("15:30");
        cmbEndTime = new JComboBox<>(timeOptions);
        cmbEndTime.setSelectedItem("18:00");
        
        // --- BATCH MODE UI ---
        chkBatchMode = new JCheckBox("Tạo lịch hàng loạt");
        chkBatchMode.setBackground(Color.WHITE);
        chkBatchMode.addActionListener(e -> toggleBatchMode());
        
        daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        daysPanel.setBackground(Color.WHITE);
        String[] dayNames = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        chkDays = new JCheckBox[7];
        for (int i = 0; i < 7; i++) {
            chkDays[i] = new JCheckBox(dayNames[i]);
            chkDays[i].setBackground(Color.WHITE);
            daysPanel.add(chkDays[i]);
        }
        
        cmbRoom = new JComboBox<>();
        cmbRoom.setEnabled(false); // Disable ban đầu, đợi bấm nút Check
        
        lblTeacherInfo = new JLabel("Lưu ý: Lớp học đã gắn cứng Giáo viên từ lúc tạo. Trạng thái GV: Chưa rõ");
        lblTeacherInfo.setForeground(Color.RED);

        int rowCount = 0;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Chọn Lớp học:"), gbc);
        gbc.gridx = 1; formPanel.add(cmbClass, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(lblTeacherInfo, gbc);
        rowCount++;
        
        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(chkBatchMode, gbc);
        rowCount++;
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(lblDateLabel, gbc);
        gbc.gridx = 1; formPanel.add(txtDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(lblEndDate, gbc);
        gbc.gridx = 1; formPanel.add(txtEndDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(daysPanel, gbc);
        rowCount++;

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Giờ Bắt đầu:"), gbc);
        gbc.gridx = 1; formPanel.add(cmbStartTime, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Giờ Kết thúc:"), gbc);
        gbc.gridx = 1; formPanel.add(cmbEndTime, gbc);

        // NÚT CHECK DÙNG STREAM API
        btnCheckAvailable = ButtonStyles.createPrimaryButton("1. Kiểm tra Phòng trống / GV Rảnh");
        btnCheckAvailable.addActionListener(e -> checkAvailability());
        
        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(btnCheckAvailable, gbc);
        rowCount++;
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Phòng học khả dụng:"), gbc);
        gbc.gridx = 1; formPanel.add(cmbRoom, gbc);

        // Buttons hành động
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = ButtonStyles.createPrimaryButton("2. Thêm Lịch");
        btnAdd.addActionListener(e -> createSchedule());
        JButton btnDelete = ButtonStyles.createDangerButton("Xóa lầm");
        btnDelete.addActionListener(e -> deleteSchedule());
        JButton btnClear = ButtonStyles.createNeutralButton("Refresh");
        btnClear.addActionListener(e -> {
            loadDataAsync();
            clearForm();
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Khoảng trắng đẩy form lên trên cùng cho đẹp, không dồn cục giữa màn hình
        gbc.gridx = 0; gbc.gridy = rowCount++;
        gbc.weighty = 1.0; // Push everything up
        formPanel.add(Box.createVerticalGlue(), gbc);

        add(formPanel, BorderLayout.WEST);
        
        // Thiết lập UI ban đầu
        toggleBatchMode();
    }
    
    private void toggleBatchMode() {
        boolean isBatch = chkBatchMode.isSelected();
        lblDateLabel.setText(isBatch ? "Từ ngày (YYYY-MM-DD):" : "Ngày học (YYYY-MM-DD):");
        lblEndDate.setVisible(isBatch);
        txtEndDate.setVisible(isBatch);
        daysPanel.setVisible(isBatch);
    }

    // Các hàm loadClasses() và loadSchedules() đồng bộ cũ đã được gộp chung vào loadDataAsync() dùng SwingWorker phía trên.
    
    /**
     * LOGIC KIỂM TRA PHÒNG/GV BẰNG STREAM API TỪ BACKEND
     */
    private void checkAvailability() {
        String date = txtDate.getText().trim();
        String start = cmbStartTime.getSelectedItem().toString() + ":00";
        String end = cmbEndTime.getSelectedItem().toString() + ":00";
        ClassItem selectedClassItem = (ClassItem) cmbClass.getSelectedItem();
        
        if (date.isEmpty() || selectedClassItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ Lớp và Ngày!");
            return;
        }
        
        try {
            // 1. Check giáo viên của lớp này có rảnh không
            Integer teacherIdOfClass = null;
            if (selectedClassItem.getCls().getTeacher() != null) {
                teacherIdOfClass = selectedClassItem.getCls().getTeacher().getTeacherId();
            } else {
                lblTeacherInfo.setText("Lớp này chưa được gán Giáo viên!");
                lblTeacherInfo.setForeground(Color.RED);
                return;
            }
            
            List<Teacher> availableTeachers = apiService.getAvailableTeachers(date, start, end);
            boolean isTeacherAvailable = availableTeachers.stream().anyMatch(t -> t.getTeacherId().equals(selectedClassItem.getCls().getTeacher().getTeacherId()));
            
            if (!isTeacherAvailable) {
                lblTeacherInfo.setText("Giảng viên của lớp đang KIẾN LỊCH / BẬN vào giờ này!");
                lblTeacherInfo.setForeground(Color.RED);
                cmbRoom.removeAllItems();
                cmbRoom.setEnabled(false);
                return;
            } else {
                lblTeacherInfo.setText("GV " + selectedClassItem.getCls().getTeacher().getFullName() + " RẢNH. Vẫn ok!");
                lblTeacherInfo.setForeground(new Color(39, 174, 96));
            }
            
            // 2. Tải list phòng trống từ Stream API Backend
            List<Room> availableRooms = apiService.getAvailableRooms(date, start, end);
            cmbRoom.removeAllItems();
            if (availableRooms == null || availableRooms.isEmpty()) {
                JOptionPane.showMessageDialog(this, "KHÔNG CÓ PHÒNG TRỐNG nào trong khung giờ này!");
                cmbRoom.setEnabled(false);
            } else {
                for (Room r : availableRooms) {
                    cmbRoom.addItem(new RoomItem(r));
                }
                cmbRoom.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Có " + availableRooms.size() + " phòng trống khả dụng. Vui lòng chọn.");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra. Cần khung giờ đúng định dạng (HH:MM). Chi tiết: " + e.getMessage());
        }
    }

    private void createSchedule() {
        if (!cmbRoom.isEnabled() || cmbRoom.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bạn cần qua bước (1. Kiểm tra phòng trống) trước khi Thêm lịch!");
            return;
        }
        
        if (chkBatchMode.isSelected()) {
            createBatchScheduleLogic();
        } else {
            createSingleScheduleLogic();
        }
    }
    
    private void createBatchScheduleLogic() {
        try {
            BatchScheduleRequest req = new BatchScheduleRequest();
            ClassItem clsItem = (ClassItem) cmbClass.getSelectedItem();
            req.setClassId(Long.valueOf(clsItem.getCls().getClassId()));
            
            RoomItem roomItem = (RoomItem) cmbRoom.getSelectedItem();
            req.setRoomId(Long.valueOf(roomItem.getRoom().getRoomId()));
            
            req.setStartDate(txtDate.getText().trim());
            req.setEndDate(txtEndDate.getText().trim());
            req.setStartTime(cmbStartTime.getSelectedItem().toString() + ":00");
            req.setEndTime(cmbEndTime.getSelectedItem().toString() + ":00");
            
            List<Integer> selectedDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (chkDays[i].isSelected()) {
                    selectedDays.add(i + 1); // 1=Mon, 7=Sun
                }
            }
            if (selectedDays.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 Thứ trong tuần!");
                return;
            }
            req.setDaysOfWeek(selectedDays);
            
            int count = apiService.createBatchSchedules(req);
            if(count > 0) {
                JOptionPane.showMessageDialog(this, "Tạo hàng loạt thành công Sinh ra " + count + " buổi học. Các ngày đụng lịch đã bị bỏ qua an toàn.");
                clearForm();
                loadDataAsync();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại hoặc không có ngày nào trống để xếp. Vui lòng kiểm tra lại khung giờ!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xử lý hàng loạt: " + e.getMessage());
        }
    }

    private void createSingleScheduleLogic() {
        if (!cmbRoom.isEnabled() || cmbRoom.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bạn cần qua bước (1. Kiểm tra phòng trống) trước khi Thêm lịch!");
            return;
        }
        
        try {
            Schedule schedule = new Schedule();
            ClassItem clsItem = (ClassItem) cmbClass.getSelectedItem();
            // Khởi tạo đối tượng Classes chỉ chứa ID thay vì gửi nguyên cục Class full data bị fail Json parse
            Classes c = new Classes();
            c.setClassId(clsItem.getCls().getClassId());
            schedule.setClassEntity(c);
            
            RoomItem roomItem = (RoomItem) cmbRoom.getSelectedItem();
            // Khởi tạo đối tượng Room chỉ chứa ID tránh lỗi vòng lặp Gson / Spring
            Room r = new Room();
            r.setRoomId(roomItem.getRoom().getRoomId());
            schedule.setRoom(r);
            
            schedule.setDate(txtDate.getText().trim());
            schedule.setStartTime(cmbStartTime.getSelectedItem().toString() + ":00");
            schedule.setEndTime(cmbEndTime.getSelectedItem().toString() + ":00");
            
            boolean created = apiService.createSchedule(schedule) != null;
            if(created) {
                JOptionPane.showMessageDialog(this, "Xếp lịch thành công!");
                clearForm();
                loadDataAsync();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại do lỗi Backend. Kiểm tra console log.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xử lý: " + e.getMessage());
        }
    }

    private void loadSelectedSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row != -1) {
            selectedScheduleId = (Integer) tableModel.getValueAt(row, 0);
        }
    }

    private void deleteSchedule() {
        if (selectedScheduleId == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng lịch để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa lịch này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiService.deleteSchedule(selectedScheduleId);
                loadDataAsync();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void clearForm() {
        selectedScheduleId = null;
        cmbRoom.removeAllItems();
        cmbRoom.setEnabled(false);
        lblTeacherInfo.setText("Lưu ý: Lớp học đã gắn cứng Giáo viên từ lúc tạo.");
        lblTeacherInfo.setForeground(Color.RED);
    }

    // Lớp bọc Combobox Lớp học
    static class ClassItem {
        private Classes cls;
        public ClassItem(Classes cls) { this.cls = cls; }
        public Classes getCls() { return cls; }
        @Override
        public String toString() { return "Lớp: " + cls.getClassName(); }
    }
    
    // Lớp bọc Combobox Phòng trống
    static class RoomItem {
        private Room room;
        public RoomItem(Room room) { this.room = room; }
        public Room getRoom() { return room; }
        @Override
        public String toString() { return "Phòng: " + room.getRoomName() + " (Sức chứa: " + room.getCapacity() + ")"; }
    }
}
