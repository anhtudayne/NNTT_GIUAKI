package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Attendance;
import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.AttendanceApiService;
import client_ttnn.hcmute.service.ClassesApiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManagerPanel extends JPanel {
    private AttendanceApiService attendanceApiService;
    private ClassesApiService classApiService;

    private JComboBox<Classes> cbClass;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnLoadStudents, btnSaveAttendance;

    public AttendanceManagerPanel() {
        attendanceApiService = new AttendanceApiService();
        classApiService = new ClassesApiService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("ĐIỂM DANH HỌC VIÊN");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(41, 128, 185)); // Xanh dương đậm
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // ==== VÙNG LỌC TÌM KIẾM THEO LỚP ====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(new Color(245, 245, 245));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)));

        filterPanel.add(new JLabel("Chọn Lớp Học:"));
        cbClass = new JComboBox<>();
        cbClass.setPreferredSize(new Dimension(300, 35));
        
        // Load danh sách Lớp vào ComboBox
        loadClassesToComboBox();
        filterPanel.add(cbClass);

        btnLoadStudents = new JButton("Tải Danh Sách Lớp");
        btnLoadStudents.setBackground(new Color(52, 152, 219));
        btnLoadStudents.setForeground(Color.WHITE);
        btnLoadStudents.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnLoadStudents.addActionListener(e -> loadStudentsToTable());
        filterPanel.add(btnLoadStudents);

        // ==== BẢNG HIỂN THỊ ĐIỂM DANH JTABLE ====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)), "Danh sách Học viên Lớp",
            TitledBorder.LEFT, TitledBorder.TOP, new Font(Font.SANS_SERIF, Font.BOLD, 14), new Color(52, 73, 94)));
        
        String[] columns = {"Mã HV", "Họ Tên HV", "Email", "SĐT", "Trạng Thái Điểm Danh"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép Edit cái cột cuối cùng "Trạng Thái Điểm Danh" (cột 4)
                return column == 4;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        
        // TRỌNG TÂM YÊU CẦU ĐỒ ÁN: Render cột Trạng thái thành JComboBox
        setUpStatusColumnEditor();

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(filterPanel, BorderLayout.NORTH); // Lọc ở trên
        tablePanel.add(scrollPane, BorderLayout.CENTER);   // Lưới ở giữa

        add(tablePanel, BorderLayout.CENTER);

        // ==== NÚT LƯU ĐIỂM DANH ====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnSaveAttendance = new JButton("LƯU ĐIỂM DANH HÀNG LOẠT");
        btnSaveAttendance.setPreferredSize(new Dimension(250, 45));
        btnSaveAttendance.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        btnSaveAttendance.setBackground(new Color(46, 204, 113)); // Xanh Emerald báo thành công
        btnSaveAttendance.setForeground(Color.WHITE);
        btnSaveAttendance.addActionListener(e -> saveBatchAttendances());
        bottomPanel.add(btnSaveAttendance);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Thiết lập JComboBox chìm vào trong JTable column Trạng Thái
    private void setUpStatusColumnEditor() {
        TableColumn statusColumn = table.getColumnModel().getColumn(4);
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Present");
        comboBox.addItem("Absent");
        comboBox.addItem("Late");
        statusColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void loadClassesToComboBox() {
        try {
            List<Classes> classes = classApiService.getAllClasses();
            cbClass.removeAllItems();
            
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

            for (Classes c : classes) {
                cbClass.addItem(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi load danh sách lớp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentsToTable() {
        Classes selectedClass = (Classes) cbClass.getSelectedItem();
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Lớp học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Student> students = attendanceApiService.getStudentsByClassId(selectedClass.getClassId().intValue());
            tableModel.setRowCount(0); // clear cũ
            
            for (Student s : students) {
                // Mặc định nạp lên cho các em "Present" (Có mặt) hết
                Object[] row = {
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
        String todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long studentId = (Long) tableModel.getValueAt(i, 0);
            String status = (String) tableModel.getValueAt(i, 4);

            // Gói vào Object
            Student student = new Student();
            student.setId(studentId);

            Attendance att = new Attendance();
            att.setClassEntity(selectedClass);
            att.setStudent(student);
            att.setDate(todayStr); // Lưu Điểm danh theo ngày hôm nay
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
