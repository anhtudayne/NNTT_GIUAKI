package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.Classes;
import client_ttnn.hcmute.model.Student;
import client_ttnn.hcmute.service.StudentApiService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import client_ttnn.hcmute.util.ButtonStyles;

/**
 * Dialog hiển thị chi tiết thông tin học viên và danh sách các lớp học đã/đang tham gia
 */
public class StudentDetailDialog extends JDialog {

    private final StudentApiService apiService;
    private final Student student;

    private JTable tblClasses;
    private DefaultTableModel tableModel;
    private ChartPanel chartPanel;

    public StudentDetailDialog(Window owner, StudentApiService apiService, Student student) {
        super(owner, "Chi tiết học viên - " + student.getFullName(), ModalityType.APPLICATION_MODAL);
        this.apiService = apiService;
        this.student = student;

        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(owner);
        setResizable(true);
        initComponents();
        loadStudentClasses();
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(new Color(245, 247, 250));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("📊 Chi tiết học viên");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        content.add(titlePanel, BorderLayout.NORTH);

        // Student Info Panel
        JPanel infoPanel = createStudentInfoPanel();
        content.add(infoPanel, BorderLayout.WEST);

        // Center Panel (Classes Table + Chart)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(245, 247, 250));

        // Classes Table
        JPanel tablePanel = createClassesTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Pie Chart Panel
        JPanel chartWrapPanel = new JPanel(new BorderLayout());
        chartWrapPanel.setBackground(Color.WHITE);
        chartWrapPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thống kê trạng thái lớp học",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                new Color(52, 73, 94)
            )
        ));
        
        // Initialize empty chart
        chartPanel = new ChartPanel(createEmptyChart());
        chartPanel.setPreferredSize(new Dimension(350, 300));
        chartWrapPanel.add(chartPanel, BorderLayout.CENTER);
        
        centerPanel.add(chartWrapPanel, BorderLayout.EAST);

        content.add(centerPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));
        
        JButton btnClose = ButtonStyles.createNeutralButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private JPanel createStudentInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Thông tin cá nhân",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                new Color(52, 73, 94)
            )
        ));
        panel.setPreferredSize(new Dimension(280, 0));

        addInfoRow(panel, "👤 Họ và tên:", student.getFullName());
        addInfoRow(panel, "📅 Ngày sinh:", student.getDateOfBirth());
        addInfoRow(panel, "⚥ Giới tính:", student.getGender());
        addInfoRow(panel, "📞 Điện thoại:", student.getPhone());
        addInfoRow(panel, "📧 Email:", student.getEmail());
        addInfoRow(panel, "🏠 Địa chỉ:", student.getAddress());
        addInfoRow(panel, "📌 Trạng thái:", student.getStatus());

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(5, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblTitle.setForeground(new Color(52, 73, 94));
        
        JLabel lblValue = new JLabel(value != null ? value : "N/A");
        lblValue.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblValue.setForeground(new Color(44, 62, 80));

        rowPanel.add(lblTitle, BorderLayout.NORTH);
        rowPanel.add(lblValue, BorderLayout.CENTER);

        panel.add(rowPanel);
    }

    private JPanel createClassesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "Danh sách lớp học",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                new Color(52, 73, 94)
            )
        ));

        String[] columns = {"ID", "Tên lớp", "Khóa học", "Giảng viên", "Phòng học", 
                           "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblClasses = new JTable(tableModel);
        tblClasses.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblClasses.setRowHeight(28);
        tblClasses.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tblClasses.getTableHeader().setBackground(new Color(52, 73, 94));
        tblClasses.getTableHeader().setForeground(Color.WHITE);
        tblClasses.setSelectionBackground(new Color(52, 152, 219));
        tblClasses.setSelectionForeground(Color.WHITE);
        tblClasses.setGridColor(new Color(189, 195, 199));

        // Set column widths
        tblClasses.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblClasses.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblClasses.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblClasses.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblClasses.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblClasses.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblClasses.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblClasses.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblClasses);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JFreeChart createEmptyChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Chưa có dữ liệu", 1);
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Thống kê trạng thái",
            dataset,
            true,
            true,
            false
        );
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Chưa có dữ liệu", new Color(189, 195, 199));
        
        return chart;
    }

    private void loadStudentClasses() {
        try {
            List<Classes> classes = apiService.getStudentClasses(student.getId());
            updateTable(classes);
            updatePieChart(classes);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách lớp học: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Classes> classes) {
        tableModel.setRowCount(0);
        for (Classes cls : classes) {
            Object[] row = new Object[]{
                cls.getClassId(),
                cls.getClassName(),
                cls.getCourse() != null ? cls.getCourse().getCourseName() : "N/A",
                cls.getTeacher() != null ? cls.getTeacher().getFullName() : "N/A",
                cls.getRoom() != null ? cls.getRoom().getRoomName() : "N/A",
                cls.getStartDate(),
                cls.getEndDate(),
                cls.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void updatePieChart(List<Classes> classes) {
        // Count classes by status
        Map<String, Integer> statusCount = new HashMap<>();
        statusCount.put("Pending", 0);
        statusCount.put("Ongoing", 0);
        statusCount.put("Completed", 0);
        statusCount.put("Canceled", 0);

        for (Classes cls : classes) {
            String status = cls.getStatus();
            if (status != null && statusCount.containsKey(status)) {
                statusCount.put(status, statusCount.get(status) + 1);
            }
        }

        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
            if (entry.getValue() > 0) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        // If no data, show message
        if (dataset.getItemCount() == 0) {
            dataset.setValue("Chưa có lớp học", 1);
        }

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
            null,
            dataset,
            true,
            true,
            false
        );

        // Customize plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        // Set colors for each status
        plot.setSectionPaint("Pending", new Color(241, 196, 15));      // Yellow
        plot.setSectionPaint("Ongoing", new Color(52, 152, 219));      // Blue
        plot.setSectionPaint("Completed", new Color(46, 204, 113));    // Green
        plot.setSectionPaint("Canceled", new Color(231, 76, 60));      // Red
        plot.setSectionPaint("Chưa có lớp học", new Color(189, 195, 199));

        // Update chart panel
        chartPanel.setChart(chart);
    }
}
