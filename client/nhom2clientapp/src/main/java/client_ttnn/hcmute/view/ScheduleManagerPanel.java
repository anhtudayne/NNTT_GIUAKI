package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Room;
import client_ttnn.hcmute.model.Schedule;
import client_ttnn.hcmute.model.Teacher;
import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.service.ScheduleApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleManagerPanel extends JPanel {
    private final ScheduleApiService apiService;
    
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JComboBox<ClassItem> cmbClass;
    private JTextField txtDate; // YYYY-MM-DD
    private JTextField txtStartTime; // HH:MM
    private JTextField txtEndTime; // HH:MM
    private JComboBox<RoomItem> cmbRoom;
    private JLabel lblTeacherInfo;
    
    // Nút check stream api
    private JButton btnCheckAvailable;
    
    private Integer selectedScheduleId = null;

    public ScheduleManagerPanel() {
        apiService = new ScheduleApiService();
        initComponents();
        loadClasses();
        loadSchedules();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Bảng dữ liệu chính
        String[] columns = {"ID", "Lớp Học", "Giảng viên", "Phòng", "Ngày", "Bắt đầu", "Kết thúc"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Xếp Lịch Học Mới (Stream API check trùng)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbClass = new JComboBox<>();
        txtDate = new JTextField(15);
        txtDate.setToolTipText("YYYY-MM-DD");
        txtDate.setText(LocalDate.now().toString());
        txtStartTime = new JTextField(15);
        txtStartTime.setToolTipText("HH:MM");
        txtEndTime = new JTextField(15);
        txtEndTime.setToolTipText("HH:MM");
        
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
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Ngày học (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; formPanel.add(txtDate, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Giờ Bắt đầu (HH:mm):"), gbc);
        gbc.gridx = 1; formPanel.add(txtStartTime, gbc);

        gbc.gridx = 0; gbc.gridy = rowCount++;
        formPanel.add(new JLabel("Giờ Kết thúc (HH:mm):"), gbc);
        gbc.gridx = 1; formPanel.add(txtEndTime, gbc);

        // NÚT CHECK DÙNG STREAM API
        btnCheckAvailable = new JButton("1. Kiểm tra Phòng trống / GV Rảnh");
        btnCheckAvailable.setBackground(new Color(41, 128, 185));
        btnCheckAvailable.setForeground(Color.WHITE);
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
        JButton btnAdd = new JButton("2. Thêm Lịch");
        btnAdd.addActionListener(e -> createSchedule());
        JButton btnDelete = new JButton("Xóa lầm");
        btnDelete.addActionListener(e -> deleteSchedule());
        JButton btnClear = new JButton("Refresh");
        btnClear.addActionListener(e -> {
            loadSchedules();
            clearForm();
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = rowCount;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.WEST);
    }

    private void loadClasses() {
        try {
            // Fake Data để test Module Schedule vì TV1 phụ trách View chưa làm màn hình Quản lý Lớp API
            List<Classes> classes = List.of(
                new Classes("Lớp Mock IELTS 6.5", null, new Teacher(2, "Nguyen Van B", "091","y@gmail.com", "IELTS", "2023", "Active"), null, "2026-03-01", "2026-06-01", 15, "Ongoing")
            );
            cmbClass.removeAllItems();
            if (classes != null) {
                for (Classes c : classes) {
                    cmbClass.addItem(new ClassItem(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSchedules() {
        try {
            List<Schedule> list = apiService.getAllSchedules();
            tableModel.setRowCount(0);
            for (Schedule s : list) {
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải Lịch: " + e.getMessage());
        }
    }
    
    /**
     * LOGIC KIỂM TRA PHÒNG/GV BẰNG STREAM API TỪ BACKEND
     */
    private void checkAvailability() {
        String date = txtDate.getText().trim();
        String start = txtStartTime.getText().trim() + ":00"; // Thêm giây cho đùng format Time
        String end = txtEndTime.getText().trim() + ":00";
        ClassItem selectedClassItem = (ClassItem) cmbClass.getSelectedItem();
        
        if (date.isEmpty() || txtStartTime.getText().isEmpty() || txtEndTime.getText().isEmpty() || selectedClassItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ Lớp, Ngày, Giờ Bắt đầu và Giờ Kết thúc!");
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
            schedule.setStartTime(txtStartTime.getText().trim() + ":00");
            schedule.setEndTime(txtEndTime.getText().trim() + ":00");
            
            Schedule created = apiService.createSchedule(schedule);
            if(created != null) {
                JOptionPane.showMessageDialog(this, "Xếp lịch thành công!");
                clearForm();
                loadSchedules();
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
                loadSchedules();
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
