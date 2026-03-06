package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Schedule;
import client_ttnn.hcmute.service.ScheduleApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimetableViewPanel extends JPanel {
    private final ScheduleApiService apiService;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    
    // Header controls
    private JLabel lblCurrentWeek;
    
    // Ngày bắt đầu của tuần đang xem (Mặc định là Thứ 2 tuần này)
    private LocalDate startOfWeek;

    // Danh sách 7 ngày trong tuần
    private final String[] daysOfWeek = {"Thời Gian", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
    private final String[] timeSlots = {"Ca 1\n(07:00 - 09:30)", "Ca 2\n(09:30 - 12:00)", "Ca 3\n(13:00 - 15:30)", "Ca 4\n(15:30 - 18:00)", "Ca 5\n(18:00 - 20:30)"};

    public TimetableViewPanel() {
        apiService = new ScheduleApiService();
        // Lấy ngày thứ 2 của tuần hiện tại
        LocalDate today = LocalDate.now();
        startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        
        initComponents();
        loadTimetableData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ----- HEADER LƯỚT TUẦN -----
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnPrevWeek = new JButton("<< Tuần Trước");
        btnPrevWeek.addActionListener(e -> {
            startOfWeek = startOfWeek.minusWeeks(1);
            updateWeekLabel();
            loadTimetableData();
        });

        lblCurrentWeek = new JLabel();
        lblCurrentWeek.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        updateWeekLabel();

        JButton btnNextWeek = new JButton("Tuần Sau >>");
        btnNextWeek.addActionListener(e -> {
            startOfWeek = startOfWeek.plusWeeks(1);
            updateWeekLabel();
            loadTimetableData();
        });
        
        JButton btnRefresh = new JButton("Làm mới Lịch");
        btnRefresh.addActionListener(e -> loadTimetableData());

        headerPanel.add(btnPrevWeek);
        headerPanel.add(lblCurrentWeek);
        headerPanel.add(btnNextWeek);
        headerPanel.add(btnRefresh);

        add(headerPanel, BorderLayout.NORTH);

        // ----- BẢNG THỜI KHÓA BIỂU MA TRẬN -----
        tableModel = new DefaultTableModel(timeSlots.length, daysOfWeek.length) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Gắn tên Cột (Thứ)
        tableModel.setColumnIdentifiers(daysOfWeek);

        // Gắn tên Dòng (Ca học)
        for (int i = 0; i < timeSlots.length; i++) {
            tableModel.setValueAt(timeSlots[i], i, 0);
        }

        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(80); // Cell cực rộng để chứa thông tin Lớp - Phòng - GV
        timetableTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        // Căn giữa văn bản & Hỗ trợ hiển thị \n nhiều dòng
        MultiLineTableCellRenderer renderer = new MultiLineTableCellRenderer();
        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
            if (i == 0) {
                timetableTable.getColumnModel().getColumn(i).setPreferredWidth(120); // Cột thời gian to hơn xíu
            } else {
                timetableTable.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
        }
        timetableTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        timetableTable.getTableHeader().setPreferredSize(new Dimension(100, 40));

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), 
                UIManager.getBorder("ScrollPane.border")));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateWeekLabel() {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblCurrentWeek.setText("Tuần: " + startOfWeek.format(formatter) + "  -  " + endOfWeek.format(formatter));
    }

    private void loadTimetableData() {
        // 1. Reset lưới về khoảng trắng (trừ cột Đầu tiên ghi Ca Học)
        for (int r = 0; r < timeSlots.length; r++) {
            for (int c = 1; c < daysOfWeek.length; c++) {
                tableModel.setValueAt("", r, c);
            }
        }

        // 2. Fetch toàn bộ schedule từ Backend
        List<Schedule> allSchedules = apiService.getAllSchedules();
        if (allSchedules == null || allSchedules.isEmpty()) return;

        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // 3. Sử dụng STREAM API của FRONTEND để Lọc lịch học trong tuần đang xem
        List<Schedule> weeklySchedules = allSchedules.stream()
                .filter(s -> {
                    if (s.getDate() == null || s.getDate().isEmpty()) return false;
                    try {
                        LocalDate d = LocalDate.parse(s.getDate());
                        return (d.isEqual(startOfWeek) || d.isAfter(startOfWeek)) && 
                               (d.isEqual(endOfWeek) || d.isBefore(endOfWeek));
                    } catch (Exception e) {
                        return false; // Bỏ qua nếu lỗi format Date
                    }
                })
                .toList();

        // 4. Đổ dữ liệu vào Ma trận (Grid)
        for (Schedule s : weeklySchedules) {
            try {
                LocalDate schDate = LocalDate.parse(s.getDate());
                int dayColumnIndex = schDate.getDayOfWeek().getValue(); // 1(Mon) -> 7(Sun) Tương ứng cột 1-7.

                String start = s.getStartTime();
                int rowCaHoc = -1;
                if (start.startsWith("07:")) rowCaHoc = 0;      // Ca 1
                else if (start.startsWith("09:")) rowCaHoc = 1; // Ca 2
                else if (start.startsWith("13:")) rowCaHoc = 2; // Ca 3
                else if (start.startsWith("15:")) rowCaHoc = 3; // Ca 4
                else if (start.startsWith("18:")) rowCaHoc = 4; // Ca 5

                if (rowCaHoc != -1 && dayColumnIndex >= 1 && dayColumnIndex <= 7) {
                    // Cấu trúc Data của 1 Cell Lịch:
                    String className = s.getClassEntity() != null ? s.getClassEntity().getClassName() : "N/A";
                    String roomName = s.getRoom() != null ? s.getRoom().getRoomName() : "N/A";
                    String teacherName = (s.getClassEntity() != null && s.getClassEntity().getTeacher() != null) 
                                                ? s.getClassEntity().getTeacher().getFullName() : "N/A";

                    String cellContent = String.format("Lớp: %s\nPhòng: %s\nGV: %s", className, roomName, teacherName);
                    
                    // Nếu ô đã có lịch rồi thì Append thêm (trùng giờ nhưng khác phòng)
                    String currentVal = (String) tableModel.getValueAt(rowCaHoc, dayColumnIndex);
                    if (currentVal.isEmpty()) {
                        tableModel.setValueAt(cellContent, rowCaHoc, dayColumnIndex);
                    } else {
                        tableModel.setValueAt(currentVal + "\n--------\n" + cellContent, rowCaHoc, dayColumnIndex);
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors for individual schedules
            }
        }
    }

    /**
     * Tự tạo Renderer để JTable hiển thị text có nhiều dòng (\n)
     */
    class MultiLineTableCellRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        public MultiLineTableCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setMargin(new Insets(5, 5, 5, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText(value != null ? value.toString() : "");
            
            // Trang trí màu sắc
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                if (column == 0) {
                    setBackground(new Color(236, 240, 241)); // Cột thời gian màu xám nhạt
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                } else if (value != null && !value.toString().trim().isEmpty()) {
                    setBackground(new Color(212, 230, 241)); // Ô có lịch học màu xanh nhạt
                    setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                } else {
                    setBackground(table.getBackground()); // Trống
                }
                setForeground(table.getForeground());
            }
            return this;
        }
    }
}
