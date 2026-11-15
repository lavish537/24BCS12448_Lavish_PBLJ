package com.exam.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Shared look of the application: colours, gradient panels
 * and styled buttons - so that every screen looks alike.
 */
public class Theme {

    public static final Color NAVY_DARK = new Color(13, 42, 74);
    public static final Color NAVY = new Color(25, 70, 130);
    public static final Color NAVY_LIGHT = new Color(52, 110, 180);
    public static final Color GREEN = new Color(34, 139, 60);
    public static final Color GREEN_DARK = new Color(26, 110, 48);
    public static final Color RED = new Color(200, 45, 45);
    public static final Color PURPLE = new Color(120, 70, 160);
    public static final Color SLATE = new Color(90, 100, 115);
    public static final Color PAGE_BG = new Color(238, 242, 246);
    public static final Color BORDER = new Color(185, 198, 214);
    public static final Color TEXT = new Color(25, 30, 38);

    /** A panel painted with a left-to-right blue gradient. */
    public static class GradientPanel extends JPanel {
        private final Color from, to;

        public GradientPanel(Color from, Color to) {
            this.from = from;
            this.to = to;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, from, getWidth(), getHeight(), to));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A coloured button whose colour shows on every platform
     * (BasicButtonUI - the Windows look-and-feel otherwise
     * paints all buttons white).
     */
    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** A white button with a coloured border and coloured text. */
    public static JButton outlineButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setBackground(Color.WHITE);
        b.setForeground(color);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** A text field with comfortable height and padding. */
    public static JTextField textField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tf.setForeground(TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return tf;
    }

    public static JLabel label(String text, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(TEXT);
        return l;
    }
}
