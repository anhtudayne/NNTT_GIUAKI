package client_ttnn.hcmute.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;

/**
 * Centralized button styling utilities so all action buttons
 * look consistent across the application.
 *
 * Styles are based on the placement test manager panel buttons.
 */
public final class ButtonStyles {

    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color DANGER = new Color(211, 47, 47);
    private static final Color NEUTRAL_BG = new Color(236, 239, 241);
    private static final Color NEUTRAL_FG = new Color(33, 33, 33);

    private ButtonStyles() {
        // no instances
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        applyPrimary(button);
        return button;
    }

    public static JButton createNeutralButton(String text) {
        JButton button = new JButton(text);
        applyNeutral(button);
        return button;
    }

    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        applyDanger(button);
        return button;
    }

    public static void applyPrimary(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setMargin(new Insets(10, 18, 10, 18));
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void applyNeutral(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(NEUTRAL_BG);
        button.setForeground(NEUTRAL_FG);
        button.setMargin(new Insets(10, 18, 10, 18));
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void applyDanger(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setMargin(new Insets(10, 18, 10, 18));
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

