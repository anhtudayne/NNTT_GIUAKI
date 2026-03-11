package client_ttnn.hcmute.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class TableCustomizer {

    public static void applyModernStyle(JTable table) {
        // 1. Cải thiện Font và Khoảng cách dòng
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(240, 240, 240)); // Màu đường kẻ nhạt hơn, tươi hơn
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(50, 50, 50));
        table.setFillsViewportHeight(true); // Trải dài màu nền trắng xuống khoảng trống bên dưới
        
        // 2. Tinh chỉnh vùng chọn (Selection)
        table.setSelectionBackground(new Color(232, 244, 253)); // Xanh nhạt Flat
        table.setSelectionForeground(new Color(30, 136, 229)); // Xanh đậm hơn chút
        table.setFocusable(false); // Xoá viền giựt giựt khi focus vào cell
        
        // 3. Tinh chỉnh Header (Tiêu đề cột)
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(Color.WHITE); // Trắng tươi tắn
        header.setForeground(new Color(33, 37, 41)); // Chữ đậm rõ nét
        header.setPreferredSize(new Dimension(header.getWidth(), 45)); // Header cao hơn
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219))); // Viền dưới xanh nhạt (Blue) tạo cảm giác tươi mới
        
        // 4. Căn giữa dữ liệu (Tuỳ chọn - thường các cột ID hoặc Trạng thái nên căn giữa)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Áp dụng padding và xoá focus border cho tất cả các Cell Renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, column); // Ép hasFocus = false
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Thêm Padding trái phải
                return c;
            }
        });
    }
}
