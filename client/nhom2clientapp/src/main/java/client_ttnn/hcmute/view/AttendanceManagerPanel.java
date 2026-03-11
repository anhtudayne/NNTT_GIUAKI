package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Attendance;
import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.model.UserAccount;
import client_ttnn.hcmute.service.AttendanceApiService;
import client_ttnn.hcmute.service.ClassesApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import client_ttnn.hcmute.util.TableCustomizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManagerPanel extends JPanel {
    private AttendanceApiService attendanceApiService;
    private ClassesApiService classApiService;
    private UserAccount currentUser;

    private JComboBox<Classes> cbClass;
    private JTextField txtDate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnLoadStudents, btnSaveAttendance;

    public AttendanceManagerPanel(UserAccount currentUser) {
        this.currentUser = currentUser;
        attendanceApiService = new AttendanceApiService();
        classApiService = new ClassesApiService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ==== VÙNG LỌC TÌM KIẾM THEO LỚP ====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Chọn Lớp Học:"));
        cbClass = new JComboBox<>();
        cbClass.setPreferredSize(new Dimension(300, 25));
        
        // Load danh sách Lớp vào ComboBox sử dụng SwingWorker
        loadClassesToComboBoxAsync();
        filterPanel.add(cbClass);

        filterPanel.add(new JLabel("Ngày Điểm Danh:"));
        txtDate = new JTextField(15);
        txtDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); // Mặc định hôm nay
        txtDate.setToolTipText("yyyy-MM-dd");
        filterPanel.add(txtDate);

        btnLoadStudents = new JButton("Tải Danh Sách Lớp");
        btnLoadStudents.addActionListener(e -> loadStudentsToTable());
        filterPanel.add(btnLoadStudents);

        // ==== BẢNG HIỂN THỊ ĐIỂM DANH JTABLE ====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        String[] columns = {"ID ĐD", "Mã HV", "Họ Tên HV", "Email", "SĐT", "Trạng Thái Điểm Danh"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép Edit cái cột cuối cùng "Trạng Thái Điểm Danh" (cột 5)
                return column == 5;
            }
        };
        table = new JTable(tableModel);
        
        // Cải thiện phong cách hiển thị JTable bằng Helper Class
        TableCustomizer.applyModernStyle(table);
        
        // Cột ID ĐD sẽ ẩn đi hoặc để độ rộng cực nhỏ chỉ để chứa Data
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); 
        table.getColumnModel().getColumn(0).setWidth(0);

        // TRỌNG TÂM YÊU CẦU ĐỒ ÁN: Render cột Trạng thái thành JComboBox
        setUpStatusColumnEditor();

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(filterPanel, BorderLayout.NORTH); // Lọc ở trên
        tablePanel.add(scrollPane, BorderLayout.CENTER);   // Lưới ở giữa

        add(tablePanel, BorderLayout.CENTER);

        // ==== NÚT LƯU ĐIỂM DANH ====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnSaveAttendance = new JButton("Lưu Điểm Danh");
        btnSaveAttendance.setPreferredSize(new Dimension(150, 40));
        btnSaveAttendance.addActionListener(e -> saveBatchAttendances());
        bottomPanel.add(btnSaveAttendance);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Thiết lập JComboBox chìm vào trong JTable column Trạng Thái
    private void setUpStatusColumnEditor() {
        TableColumn statusColumn = table.getColumnModel().getColumn(5);
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Present");
        comboBox.addItem("Absent");
        comboBox.addItem("Late");
        statusColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void loadClassesToComboBoxAsync() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cbClass.setEnabled(false);
        
        // Customize cách hiển thị Tên Lớp lên Combobox
        cbClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Classes) {
                    Classes c = (Classes) value;
                    setText(c.getClassName() + " (Mã: " + c.getClassId() + ")");
                }
                return this;
            }
        });

        SwingWorker<List<Classes>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Classes> doInBackground() throws Exception {
                if (currentUser != null && "Teacher".equals(currentUser.getRole())) {
                    return classApiService.getClassesByTeacherId(Long.valueOf(currentUser.getRelatedId()));
                } else {
                    return client_ttnn.hcmute.util.CacheManager.getInstance().getClasses();
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                cbClass.setEnabled(true);
                try {
                    List<Classes> classes = get();
                    cbClass.removeAllItems();
                    if (classes != null) {
                        for (Classes c : classes) {
                            cbClass.addItem(c);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AttendanceManagerPanel.this, "Lỗi load danh sách lớp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadStudentsToTable() {
        Classes selectedClass = (Classes) cbClass.getSelectedItem();
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Lớp học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String dateStr = txtDate.getText().trim();
            if(dateStr.isEmpty()){
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày (yyyy-MM-dd)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi API kiểm tra Data cũ
            List<Attendance> oldData = attendanceApiService.getAttendancesByClassIdAndDate(selectedClass.getClassId().intValue(), dateStr);
            tableModel.setRowCount(0); // clear cũ

            if (oldData != null && !oldData.isEmpty()) {
                // Đã có data -> Chế độ Xem / Cập nhật
                for (Attendance a : oldData) {
                    Student s = a.getStudent();
                    Object[] row = {
                        a.getAttendanceId(),
                        s.getId(),
                        s.getFullName(),
                        s.getEmail(),
                        s.getPhone(),
                        a.getStatus() 
                    };
                    tableModel.addRow(row);
                }
            } else {
                // Trống -> Chế độ Tạo mới (Lấy List Học Sinh Đổ vào, Set Present)
                List<Student> students = attendanceApiService.getStudentsByClassId(selectedClass.getClassId().intValue());
                for (Student s : students) {
                    Object[] row = {
                        null, // Insert data mới Id = null
                        s.getId(),
                        s.getFullName(),
                        s.getEmail(),
                        s.getPhone(),
                        "Present" 
                    };
                    tableModel.addRow(row);
                }
                
                if(students.isEmpty()){
                     JOptionPane.showMessageDialog(this, "Lớp này hiện chưa có học viên nào Ghi Danh hoặc Đang học!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách HS: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBatchAttendances() {
        Classes selectedClass = (Classes) cbClass.getSelectedItem();
        if (selectedClass == null || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu điểm danh để lưu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Quét từng dòng JTable đóng gói thành DTO Attendance
        List<Attendance> batchList = new ArrayList<>();
        String dateStr = txtDate.getText().trim();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Integer attendanceId = (Integer) tableModel.getValueAt(i, 0); // Có thể null 
            Long studentId = (Long) tableModel.getValueAt(i, 1);
            String status = (String) tableModel.getValueAt(i, 5);

            // Gói vào Object
            Student student = new Student();
            student.setId(studentId);

            Attendance att = new Attendance();
            att.setAttendanceId(attendanceId); // Điểm quyết định Update hay Create
            att.setClassEntity(selectedClass);
            att.setStudent(student);
            att.setDate(dateStr); // Lưu Điểm danh theo ngày trên txt textbox
            att.setStatus(status);

            batchList.add(att);
        }

        // GỌI POST GỬI JSON HÀNG LOẠT
        try {
            boolean success = attendanceApiService.saveBatchAttendances(batchList);
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu danh sách Điểm danh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Lưu Điểm Danh: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
