package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.DashboardSummary;
import client_ttnn.hcmute.model.RevenueByMonth;
import client_ttnn.hcmute.service.DashboardApiService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final DashboardApiService apiService;

    // KPI labels
    private JLabel lblTotalStudents;
    private JLabel lblTotalTeachers;
    private JLabel lblTotalStaff;
    private JLabel lblTotalClasses;
    private JLabel lblRevenueAllTime;
    private JLabel lblRevenueYear;
    private JLabel lblRevenueMonth;
    private JLabel lblCertificates;
    private JLabel lblAvgScore;

    // Revenue: table + charts
    private JTable tblRevenue;
    private DefaultTableModel revenueTableModel;
    private JTabbedPane chartTabbedPane;
    private JPanel revenueChartPanel;
    private JPanel headcountChartPanel;
    private JTextField txtYearFilter;

    public DashboardPanel() {
        this.apiService = new DashboardApiService();
        initComponents();
        loadDashboardData(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top KPI area (grid of cards)
        JPanel kpiPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        lblTotalStudents = createKpiCard(kpiPanel, "HỌC VIÊN ACTIVE");
        lblTotalTeachers = createKpiCard(kpiPanel, "GIÁO VIÊN");
        lblTotalStaff = createKpiCard(kpiPanel, "NHÂN SỰ");
        lblTotalClasses = createKpiCard(kpiPanel, "LỚP ĐANG HỌC");

        lblRevenueAllTime = createKpiCard(kpiPanel, "DOANH THU TỔNG");
        lblRevenueYear = createKpiCard(kpiPanel, "DOANH THU NĂM");
        lblRevenueMonth = createKpiCard(kpiPanel, "DOANH THU THÁNG");
        lblCertificates = createKpiCard(kpiPanel, "CHỨNG CHỈ ĐÃ CẤP");

        add(kpiPanel, BorderLayout.NORTH);

        // Center: revenue by month (chart + table) + average score
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Revenue filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Năm:"));
        txtYearFilter = new JTextField(6);
        filterPanel.add(txtYearFilter);
        JButton btnReload = new JButton("Tải Dashboard");
        btnReload.addActionListener(e -> {
            Integer year = null;
            String text = txtYearFilter.getText().trim();
            if (!text.isEmpty()) {
                try {
                    year = Integer.parseInt(text);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Năm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            loadDashboardData(year);
        });
        filterPanel.add(btnReload);

        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Revenue & headcount charts + table
        chartTabbedPane = new JTabbedPane();
        revenueChartPanel = new JPanel(new BorderLayout());
        headcountChartPanel = new JPanel(new BorderLayout());

        chartTabbedPane.addTab("Doanh thu theo tháng", revenueChartPanel);
        chartTabbedPane.addTab("Cơ cấu nhân sự", headcountChartPanel);

        String[] columns = {"Tháng", "Doanh thu"};
        revenueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRevenue = new JTable(revenueTableModel);
        tblRevenue.setRowHeight(26);
        tblRevenue.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollRevenue = new JScrollPane(tblRevenue);
        scrollRevenue.setBorder(BorderFactory.createTitledBorder("Bảng doanh thu theo tháng"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartTabbedPane, scrollRevenue);
        splitPane.setResizeWeight(0.6);
        centerPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom: average score card
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel avgPanel = new JPanel();
        avgPanel.setBorder(BorderFactory.createTitledBorder("Điểm trung bình kết quả học tập"));
        lblAvgScore = new JLabel("--");
        lblAvgScore.setFont(new Font("Segoe UI", Font.BOLD, 20));
        avgPanel.add(lblAvgScore);
        bottomPanel.add(avgPanel, BorderLayout.CENTER);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createKpiCard(JPanel parent, String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblValue = new JLabel("--");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);

        parent.add(panel);
        return lblValue;
    }

    private void loadDashboardData(Integer year) {
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardSummary summary = apiService.getSummary();
                List<RevenueByMonth> revenues = apiService.getRevenueByMonth(year);

                updateSummary(summary);
                updateHeadcountChart(summary);
                updateRevenueTable(revenues);
                updateRevenueChart(revenues);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tải Dashboard: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateSummary(DashboardSummary s) {
        if (s == null) {
            return;
        }
        lblTotalStudents.setText(String.valueOf(s.getTotalActiveStudents()));
        lblTotalTeachers.setText(String.valueOf(s.getTotalTeachers()));
        lblTotalStaff.setText(String.valueOf(s.getTotalStaff()));
        lblTotalClasses.setText(String.valueOf(s.getTotalOngoingClasses()));

        lblRevenueAllTime.setText(formatMoney(s.getTotalRevenueAllTime()));
        lblRevenueYear.setText(formatMoney(s.getTotalRevenueCurrentYear()));
        lblRevenueMonth.setText(formatMoney(s.getTotalRevenueCurrentMonth()));

        lblCertificates.setText(String.valueOf(s.getTotalCertificatesIssued()));
        lblAvgScore.setText(String.format("%.2f", s.getAverageScoreAllResults()));
    }

    private void updateRevenueTable(List<RevenueByMonth> list) {
        revenueTableModel.setRowCount(0);
        if (list == null) {
            return;
        }
        for (RevenueByMonth r : list) {
            Object[] row = {
                    r.getMonth() + "/" + r.getYear(),
                    formatMoney(r.getTotalRevenue())
            };
            revenueTableModel.addRow(row);
        }
    }

    /**
     * Biểu đồ đường doanh thu theo tháng.
     */
    private void updateRevenueChart(List<RevenueByMonth> list) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (list != null) {
            for (RevenueByMonth r : list) {
                BigDecimal value = r.getTotalRevenue();
                if (value == null) {
                    value = BigDecimal.ZERO;
                }
                String monthLabel = String.valueOf(r.getMonth());
                dataset.addValue(value, "Doanh thu", monthLabel);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Doanh thu theo tháng",
                "Tháng",
                "Doanh thu",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setDefaultShapesVisible(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        revenueChartPanel.removeAll();
        revenueChartPanel.add(chartPanel, BorderLayout.CENTER);
        revenueChartPanel.revalidate();
        revenueChartPanel.repaint();
    }

    /**
     * Biểu đồ tròn cơ cấu nhân sự / lớp học.
     */
    private void updateHeadcountChart(DashboardSummary s) {
        if (s == null) {
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Học viên active", s.getTotalActiveStudents());
        dataset.setValue("Giáo viên", s.getTotalTeachers());
        dataset.setValue("Nhân sự", s.getTotalStaff());
        dataset.setValue("Lớp đang học", s.getTotalOngoingClasses());

        JFreeChart chart = ChartFactory.createPieChart(
                "Cơ cấu nhân sự & lớp đang học",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Học viên active", new Color(46, 204, 113));
        plot.setSectionPaint("Giáo viên", new Color(52, 152, 219));
        plot.setSectionPaint("Nhân sự", new Color(155, 89, 182));
        plot.setSectionPaint("Lớp đang học", new Color(241, 196, 15));

        ChartPanel chartPanel = new ChartPanel(chart);

        headcountChartPanel.removeAll();
        headcountChartPanel.add(chartPanel, BorderLayout.CENTER);
        headcountChartPanel.revalidate();
        headcountChartPanel.repaint();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return String.format("%,.0f", value);
    }
}

