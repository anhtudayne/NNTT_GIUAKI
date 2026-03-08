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

/**
 * Dashboard tổng quan theo phong cách doanh nghiệp:
 * - Header rõ ràng, cập nhật thời gian
 * - KPI cards chia nhóm (Hoạt động đào tạo / Tài chính), thiết kế thẻ chuyên nghiệp
 * - Biểu đồ cột doanh thu + biểu đồ tròn cơ cấu, bảng doanh thu theo tháng
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
    private JLabel lblCertificates, lblAvgScore;
    private JTable tblRevenue;
    private DefaultTableModel revenueTableModel;
    private JPanel revenueChartPanel;
    private JPanel headcountChartPanel;
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
        JButton btnRefresh = new JButton("Làm mới");
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
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        chartsRow.setPreferredSize(new Dimension(900, 320));
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

        headcountChartPanel = new JPanel(new BorderLayout(8, 8));
        headcountChartPanel.setBackground(CARD_BG);
        headcountChartPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel chartHeadcountTitle = new JLabel("Cơ cấu nhân sự & lớp học");
        chartHeadcountTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        chartHeadcountTitle.setForeground(TEXT_SECONDARY);
        headcountChartPanel.add(chartHeadcountTitle, BorderLayout.NORTH);
        chartsRow.add(headcountChartPanel);

        centerContentPanel.add(chartsRow);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ----- TABLE + AVERAGE SCORE -----
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
        bottomRow.add(tableWrapper, BorderLayout.CENTER);

        JPanel avgCard = new JPanel(new BorderLayout(12, 12));
        avgCard.setPreferredSize(new Dimension(220, 0));
        avgCard.setBackground(CARD_BG);
        avgCard.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, CARD_BORDER),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel avgTitle = new JLabel("Điểm trung bình kết quả học tập");
        avgTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        avgTitle.setForeground(TEXT_SECONDARY);
        lblAvgScore = new JLabel("--");
        lblAvgScore.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        lblAvgScore.setForeground(ACCENT_OPERATIONS);
        lblAvgScore.setHorizontalAlignment(SwingConstants.CENTER);
        avgCard.add(avgTitle, BorderLayout.NORTH);
        avgCard.add(lblAvgScore, BorderLayout.CENTER);
        bottomRow.add(avgCard, BorderLayout.EAST);

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
                updateHeadcountChart(summary);
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
        lblAvgScore.setText(String.format("%.2f", s.getAverageScoreAllResults()));
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

    private void updateHeadcountChart(DashboardSummary s) {
        if (s == null) return;
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Học viên", s.getTotalActiveStudents());
        dataset.setValue("Giảng viên", s.getTotalTeachers());
        dataset.setValue("Nhân sự", s.getTotalStaff());
        dataset.setValue("Lớp học", s.getTotalOngoingClasses());

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        chart.setBackgroundPaint(CARD_BG);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(CARD_BG);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Học viên", new Color(46, 204, 113));
        plot.setSectionPaint("Giảng viên", new Color(52, 152, 219));
        plot.setSectionPaint("Nhân sự", new Color(155, 89, 182));
        plot.setSectionPaint("Lớp học", new Color(241, 196, 15));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",
                new DecimalFormat("#,##0"),
                new DecimalFormat("0%")));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(CARD_BG);
        chartPanel.setPreferredSize(new Dimension(420, 280));
        chartPanel.setMaximumSize(new Dimension(500, 300));

        Component[] comps = headcountChartPanel.getComponents();
        headcountChartPanel.removeAll();
        for (Component c : comps) if (c instanceof JLabel) headcountChartPanel.add(c, BorderLayout.NORTH);
        headcountChartPanel.add(chartPanel, BorderLayout.CENTER);
        headcountChartPanel.revalidate();
        headcountChartPanel.repaint();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return String.format("%,.0f", value);
    }
}
