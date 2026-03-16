package client_ttnn.hcmute.view;

import client_ttnn.hcmute.model.DashboardSummary;
import client_ttnn.hcmute.model.RevenueByMonth;
import client_ttnn.hcmute.service.DashboardApiService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import client_ttnn.hcmute.util.ButtonStyles;

/**
 * Dashboard tổng quan theo phong cách doanh nghiệp:
 * - Header rõ ràng, cập nhật thời gian
 * - KPI cards chia nhóm (Hoạt động đào tạo / Tài chính), thiết kế thẻ chuyên nghiệp
 * - Biểu đồ cột doanh thu theo tháng + (biểu đồ tròn doanh thu theo quý + biểu đồ cột ngang quy mô hoạt động), bảng doanh thu theo tháng
 * - Điểm trung bình và nút làm mới
 */
public class DashboardPanel extends JPanel {

    private static final Color HEADER_BG = new Color(25, 32, 72);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(224, 224, 224);
    private static final Color ACCENT_OPERATIONS = new Color(30, 136, 229);
    private static final Color ACCENT_FINANCE = new Color(0, 150, 136);
    private static final Color TEXT_SECONDARY = new Color(97, 97, 97);
    private static final Color REVENUE_BAR = new Color(2, 119, 189);

    private final DashboardApiService apiService;
    private JLabel lblLastUpdated;
    private JLabel lblTotalStudents, lblTotalTeachers, lblTotalStaff, lblTotalClasses;
    private JLabel lblRevenueAllTime, lblRevenueYear, lblRevenueMonth;
    private JLabel lblCertificates;
    private JTable tblRevenue;
    private DefaultTableModel revenueTableModel;
    private JPanel revenueChartPanel;
    private JPanel rightChartsWrapper;
    private JPanel quarterlyRevenueChartPanel;
    private JPanel headcountBarChartPanel;
    private JTextField txtYearFilter;
    private JPanel centerContentPanel;

    public DashboardPanel() {
        this.apiService = new DashboardApiService();
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        initComponents();
        loadDashboardData(null);
    }

    private void initComponents() {
        // ----- HEADER -----
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(20, 28, 20, 28));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("TỔNG QUAN");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerLeft.add(lblTitle);
        headerPanel.add(headerLeft, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setOpaque(false);
        lblLastUpdated = new JLabel("Cập nhật: --");
        lblLastUpdated.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblLastUpdated.setForeground(new Color(200, 210, 230));
        headerRight.add(lblLastUpdated);
        txtYearFilter = new JTextField(5);
        txtYearFilter.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtYearFilter.setHorizontalAlignment(JTextField.CENTER);
        txtYearFilter.setToolTipText("Năm (để trống = năm hiện tại)");
        JLabel lblYear = new JLabel("Năm:");
        lblYear.setForeground(Color.WHITE);
        headerRight.add(lblYear);
        headerRight.add(txtYearFilter);
        JButton btnRefresh = ButtonStyles.createPrimaryButton("Làm mới");
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> {
            Integer year = null;
            String text = txtYearFilter.getText().trim();
            if (!text.isEmpty()) {
                try { year = Integer.parseInt(text); } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Năm không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            loadDashboardData(year);
        });
        headerRight.add(btnRefresh);
        headerPanel.add(headerRight, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ----- SCROLLABLE CONTENT -----
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setBackground(new Color(245, 247, 250));
        centerContentPanel.setBorder(new EmptyBorder(20, 28, 28, 28));

        // ----- KPI: Hoạt động đào tạo -----
        JLabel sectionOperations = new JLabel("Hoạt động đào tạo");
        sectionOperations.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        sectionOperations.setForeground(TEXT_SECONDARY);
        sectionOperations.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerContentPanel.add(sectionOperations);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel kpiOperationsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiOperationsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        kpiOperationsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        kpiOperationsPanel.setOpaque(false);

        lblTotalStudents = createKpiCard("Học viên đang học", ACCENT_OPERATIONS, "0");
        lblTotalTeachers = createKpiCard("Giảng viên", ACCENT_OPERATIONS, "0");
        lblTotalStaff = createKpiCard("Nhân sự", ACCENT_OPERATIONS, "0");
        lblTotalClasses = createKpiCard("Lớp đang học", ACCENT_OPERATIONS, "0");
        kpiOperationsPanel.add(lblTotalStudents.getParent());
        kpiOperationsPanel.add(lblTotalTeachers.getParent());
        kpiOperationsPanel.add(lblTotalStaff.getParent());
        kpiOperationsPanel.add(lblTotalClasses.getParent());

        centerContentPanel.add(kpiOperationsPanel);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // ----- KPI: Tài chính -----
        JLabel sectionFinance = new JLabel("Tài chính");
        sectionFinance.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        sectionFinance.setForeground(TEXT_SECONDARY);
        sectionFinance.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerContentPanel.add(sectionFinance);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel kpiFinancePanel = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiFinancePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        kpiFinancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        kpiFinancePanel.setOpaque(false);

        lblRevenueAllTime = createKpiCard("Doanh thu tổng", ACCENT_FINANCE, "0");
        lblRevenueYear = createKpiCard("Doanh thu năm", ACCENT_FINANCE, "0");
        lblRevenueMonth = createKpiCard("Doanh thu tháng", ACCENT_FINANCE, "0");
        lblCertificates = createKpiCard("Chứng chỉ đã cấp", ACCENT_FINANCE, "0");
        kpiFinancePanel.add(lblRevenueAllTime.getParent());
        kpiFinancePanel.add(lblRevenueYear.getParent());
        kpiFinancePanel.add(lblRevenueMonth.getParent());
        kpiFinancePanel.add(lblCertificates.getParent());

        centerContentPanel.add(kpiFinancePanel);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // ----- CHARTS ROW -----
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 640));
        chartsRow.setPreferredSize(new Dimension(900, 640));
        chartsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartsRow.setOpaque(false);

        revenueChartPanel = new JPanel(new BorderLayout(8, 8));
        revenueChartPanel.setBackground(CARD_BG);
        revenueChartPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel chartRevenueTitle = new JLabel("Doanh thu theo tháng");
        chartRevenueTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        chartRevenueTitle.setForeground(TEXT_SECONDARY);
        revenueChartPanel.add(chartRevenueTitle, BorderLayout.NORTH);
        chartsRow.add(revenueChartPanel);

        // Cột phải: 2 biểu đồ (Doanh thu theo quý + Quy mô hoạt động)
        rightChartsWrapper = new JPanel();
        rightChartsWrapper.setLayout(new BoxLayout(rightChartsWrapper, BoxLayout.Y_AXIS));
        rightChartsWrapper.setBackground(new Color(245, 247, 250));
        rightChartsWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        quarterlyRevenueChartPanel = new JPanel(new BorderLayout(8, 8));
        quarterlyRevenueChartPanel.setBackground(CARD_BG);
        quarterlyRevenueChartPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel quarterlyTitle = new JLabel("Cơ cấu doanh thu theo quý");
        quarterlyTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        quarterlyTitle.setForeground(TEXT_SECONDARY);
        quarterlyRevenueChartPanel.add(quarterlyTitle, BorderLayout.NORTH);
        rightChartsWrapper.add(quarterlyRevenueChartPanel);

        rightChartsWrapper.add(Box.createRigidArea(new Dimension(0, 12)));

        headcountBarChartPanel = new JPanel(new BorderLayout(8, 8));
        headcountBarChartPanel.setBackground(CARD_BG);
        headcountBarChartPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel headcountBarTitle = new JLabel("Quy mô hoạt động");
        headcountBarTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        headcountBarTitle.setForeground(TEXT_SECONDARY);
        headcountBarChartPanel.add(headcountBarTitle, BorderLayout.NORTH);
        rightChartsWrapper.add(headcountBarChartPanel);

        chartsRow.add(rightChartsWrapper);

        centerContentPanel.add(chartsRow);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ----- TABLE ONLY (Removed AVERAGE SCORE) -----
        JPanel bottomRow = new JPanel(new BorderLayout(16, 0));
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setOpaque(false);

        JPanel tableWrapper = new JPanel(new BorderLayout(8, 8));
        tableWrapper.setBackground(CARD_BG);
        tableWrapper.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        String[] columns = {"Tháng", "Doanh thu (VNĐ)"};
        revenueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRevenue = new JTable(revenueTableModel);
        tblRevenue.setRowHeight(32);
        tblRevenue.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tblRevenue.setShowGrid(false);
        JTableHeader th = tblRevenue.getTableHeader();
        th.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        th.setBackground(new Color(250, 250, 250));
        th.setPreferredSize(new Dimension(0, 40));
        JScrollPane tableScroll = new JScrollPane(tblRevenue);
        tableScroll.setBorder(null);
        JLabel tableTitle = new JLabel("Chi tiết doanh thu theo tháng");
        tableTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tableTitle.setForeground(TEXT_SECONDARY);
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(tableScroll, BorderLayout.CENTER);
        
        // Gán nguyên chiều rộng cho tableWrapper
        bottomRow.add(tableWrapper, BorderLayout.CENTER);

        centerContentPanel.add(bottomRow);

        scrollPane.setViewportView(centerContentPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createKpiCard(String title, Color accentColor, String initialValue) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 4, 0, 0, accentColor),
                        new MatteBorder(1, 1, 1, 1, CARD_BORDER)
                ),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblTitle.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(initialValue);
        lblValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        lblValue.setForeground(new Color(33, 33, 33));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return lblValue;
    }

    private void loadDashboardData(Integer year) {
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardSummary summary = apiService.getSummary();
                List<RevenueByMonth> revenues = apiService.getRevenueByMonth(year);

                updateSummary(summary);
                updateQuarterlyRevenueChart(revenues);
                updateHeadcountBarChart(summary);
                updateRevenueTable(revenues);
                updateRevenueChart(revenues);
                lblLastUpdated.setText("Cập nhật: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải Dashboard: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateSummary(DashboardSummary s) {
        if (s == null) return;
        lblTotalStudents.setText(String.valueOf(s.getTotalActiveStudents()));
        lblTotalTeachers.setText(String.valueOf(s.getTotalTeachers()));
        lblTotalStaff.setText(String.valueOf(s.getTotalStaff()));
        lblTotalClasses.setText(String.valueOf(s.getTotalOngoingClasses()));
        lblRevenueAllTime.setText(formatMoney(s.getTotalRevenueAllTime()));
        lblRevenueYear.setText(formatMoney(s.getTotalRevenueCurrentYear()));
        lblRevenueMonth.setText(formatMoney(s.getTotalRevenueCurrentMonth()));
        lblCertificates.setText(String.valueOf(s.getTotalCertificatesIssued()));
    }

    private void updateRevenueTable(List<RevenueByMonth> list) {
        revenueTableModel.setRowCount(0);
        if (list == null) return;
        for (RevenueByMonth r : list) {
            revenueTableModel.addRow(new Object[]{
                    r.getMonth() + "/" + r.getYear(),
                    formatMoney(r.getTotalRevenue())
            });
        }
    }

    private void updateRevenueChart(List<RevenueByMonth> list) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (list != null) {
            for (RevenueByMonth r : list) {
                BigDecimal value = r.getTotalRevenue() != null ? r.getTotalRevenue() : BigDecimal.ZERO;
                dataset.addValue(value.doubleValue(), "Doanh thu", String.valueOf(r.getMonth()));
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Tháng",
                "Doanh thu (VNĐ)",
                dataset
        );
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, REVENUE_BAR);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#,##0")));
        chart.setBackgroundPaint(CARD_BG);
        plot.setBackgroundPaint(CARD_BG);
        plot.setOutlineVisible(false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(CARD_BG);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(420, 280));
        chartPanel.setMaximumSize(new Dimension(500, 300));

        // Keep title label, replace chart only
        Component[] comps = revenueChartPanel.getComponents();
        revenueChartPanel.removeAll();
        for (Component c : comps) if (c instanceof JLabel) revenueChartPanel.add(c, BorderLayout.NORTH);
        revenueChartPanel.add(chartPanel, BorderLayout.CENTER);
        revenueChartPanel.revalidate();
        revenueChartPanel.repaint();
    }

    /** Biểu đồ tròn: cơ cấu doanh thu theo quý (Q1–Q4) từ dữ liệu doanh thu theo tháng. */
    private void updateQuarterlyRevenueChart(List<RevenueByMonth> list) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        if (list != null && !list.isEmpty()) {
            BigDecimal q1 = BigDecimal.ZERO, q2 = BigDecimal.ZERO, q3 = BigDecimal.ZERO, q4 = BigDecimal.ZERO;
            for (RevenueByMonth r : list) {
                BigDecimal v = r.getTotalRevenue() != null ? r.getTotalRevenue() : BigDecimal.ZERO;
                int m = r.getMonth();
                if (m >= 1 && m <= 3) q1 = q1.add(v);
                else if (m >= 4 && m <= 6) q2 = q2.add(v);
                else if (m >= 7 && m <= 9) q3 = q3.add(v);
                else if (m >= 10 && m <= 12) q4 = q4.add(v);
            }
            if (q1.compareTo(BigDecimal.ZERO) != 0) dataset.setValue("Q1 (T1–T3)", q1.doubleValue());
            if (q2.compareTo(BigDecimal.ZERO) != 0) dataset.setValue("Q2 (T4–T6)", q2.doubleValue());
            if (q3.compareTo(BigDecimal.ZERO) != 0) dataset.setValue("Q3 (T7–T9)", q3.doubleValue());
            if (q4.compareTo(BigDecimal.ZERO) != 0) dataset.setValue("Q4 (T10–T12)", q4.doubleValue());
        }
        if (dataset.getItemCount() == 0) dataset.setValue("Chưa có dữ liệu", 1);

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        chart.setBackgroundPaint(CARD_BG);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(CARD_BG);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Q1 (T1–T3)", new Color(46, 204, 113));
        plot.setSectionPaint("Q2 (T4–T6)", new Color(52, 152, 219));
        plot.setSectionPaint("Q3 (T7–T9)", new Color(155, 89, 182));
        plot.setSectionPaint("Q4 (T10–T12)", new Color(241, 196, 15));
        plot.setSectionPaint("Chưa có dữ liệu", new Color(189, 195, 199));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",
                new DecimalFormat("#,##0"),
                new DecimalFormat("0%")));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(CARD_BG);
        chartPanel.setPreferredSize(new Dimension(420, 260));
        chartPanel.setMaximumSize(new Dimension(500, 280));

        Component[] comps = quarterlyRevenueChartPanel.getComponents();
        quarterlyRevenueChartPanel.removeAll();
        for (Component c : comps) if (c instanceof JLabel) quarterlyRevenueChartPanel.add(c, BorderLayout.NORTH);
        quarterlyRevenueChartPanel.add(chartPanel, BorderLayout.CENTER);
        quarterlyRevenueChartPanel.revalidate();
        quarterlyRevenueChartPanel.repaint();
    }

    /** Biểu đồ cột ngang: so sánh quy mô Học viên, Giảng viên, Nhân sự, Lớp học. */
    private void updateHeadcountBarChart(DashboardSummary s) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (s != null) {
            dataset.addValue(s.getTotalActiveStudents(), "Số lượng", "Học viên");
            dataset.addValue(s.getTotalTeachers(), "Số lượng", "Giảng viên");
            dataset.addValue(s.getTotalStaff(), "Số lượng", "Nhân sự");
            dataset.addValue(s.getTotalOngoingClasses(), "Số lượng", "Lớp học");
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "",
                "",
                "Số lượng",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                false,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, ACCENT_OPERATIONS);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#,##0")));
        chart.setBackgroundPaint(CARD_BG);
        plot.setBackgroundPaint(CARD_BG);
        plot.setOutlineVisible(false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(CARD_BG);
        chartPanel.setPreferredSize(new Dimension(420, 260));
        chartPanel.setMaximumSize(new Dimension(500, 280));

        Component[] comps = headcountBarChartPanel.getComponents();
        headcountBarChartPanel.removeAll();
        for (Component c : comps) if (c instanceof JLabel) headcountBarChartPanel.add(c, BorderLayout.NORTH);
        headcountBarChartPanel.add(chartPanel, BorderLayout.CENTER);
        headcountBarChartPanel.revalidate();
        headcountBarChartPanel.repaint();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return String.format("%,.0f", value);
    }
}
