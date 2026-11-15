package com.exam;

import com.exam.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Online Examination System
 * A desktop exam application made with Java AWT/Swing.
 * Student login -> instructions -> timed exam with question
 * palette -> instant result with answer review.
 *
 * Developed by Rishi Kant, Chandigarh University
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        // bigger, clear default fonts everywhere (better readability)
        java.awt.Font base = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14);
        java.awt.Font baseBold = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14);
        UIManager.put("Label.font", base);
        UIManager.put("Button.font", baseBold);
        UIManager.put("TextField.font", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));
        UIManager.put("ComboBox.font", base);
        UIManager.put("RadioButton.font", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));
        UIManager.put("Table.font", base);
        UIManager.put("TableHeader.font", baseBold);
        UIManager.put("TextArea.font", base);
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
