package com.exam.ui;

import com.exam.data.QuestionBank;

import javax.swing.*;
import java.awt.*;

/**
 * First screen of the examination system.
 * Left side: a gradient branding panel of the system.
 * Right side: the student details form on a white card.
 * Exam pattern: 15 questions (randomly picked from a bank of 30
 * per subject), 15 minutes, no negative marking.
 */
public class LoginFrame extends JFrame {

    public static final int QUESTIONS_PER_EXAM = 15;
    public static final int EXAM_MINUTES = 15;

    private final JTextField nameField = Theme.textField();
    private final JTextField rollField = Theme.textField();
    private final JComboBox<String> subjectCombo =
            new JComboBox<>(QuestionBank.getSubjects().toArray(new String[0]));

    public LoginFrame() {
        super("Online Examination System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildBrandPanel(), BorderLayout.WEST);
        add(buildFormPanel(), BorderLayout.CENTER);
    }

    // ---------------- left branding panel ----------------
    private JPanel buildBrandPanel() {
        Theme.GradientPanel brand = new Theme.GradientPanel(Theme.NAVY_DARK, Theme.NAVY_LIGHT);
        brand.setLayout(new BorderLayout());
        brand.setPreferredSize(new Dimension(360, 0));
        brand.setBorder(BorderFactory.createEmptyBorder(38, 34, 30, 30));

        JPanel top = new JPanel(new GridLayout(3, 1, 0, 4));
        top.setOpaque(false);
        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 52));
        JLabel t1 = new JLabel("ONLINE");
        t1.setFont(new Font("SansSerif", Font.BOLD, 37));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("EXAMINATION SYSTEM");
        t2.setFont(new Font("SansSerif", Font.BOLD, 22));
        t2.setForeground(new Color(190, 215, 245));
        top.add(icon);
        top.add(t1);
        top.add(t2);
        brand.add(top, BorderLayout.NORTH);

        JPanel feats = new JPanel(new GridLayout(5, 1, 0, 10));
        feats.setOpaque(false);
        String[] points = {
                "✓  3 Subjects - Java, Aptitude, GK",
                "✓  Random paper for every student",
                "✓  Live timer with auto-submit",
                "✓  Question palette like real CBT",
                "✓  Instant result + answer review",
        };
        for (String s : points) {
            JLabel l = new JLabel(s);
            l.setFont(new Font("SansSerif", Font.PLAIN, 15));
            l.setForeground(new Color(225, 236, 250));
            feats.add(l);
        }
        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(feats, BorderLayout.SOUTH);
        brand.add(mid, BorderLayout.CENTER);

        JLabel foot = new JLabel("<html>Chandigarh University<br>"
                + "<font size='3'>Developed by Rishi Kant</font></html>");
        foot.setFont(new Font("SansSerif", Font.BOLD, 14));
        foot.setForeground(new Color(170, 200, 235));
        brand.add(foot, BorderLayout.SOUTH);
        return brand;
    }

    // ---------------- right form panel ----------------
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.PAGE_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(46, 52, 30, 52));

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(30, 36, 28, 36)));

        JPanel head = new JPanel(new GridLayout(2, 1, 0, 3));
        head.setBackground(Color.WHITE);
        JLabel welcome = new JLabel("Student Login");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcome.setForeground(Theme.NAVY);
        JLabel sub = new JLabel("Enter your details to start the examination");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(110, 120, 135));
        head.add(welcome);
        head.add(sub);
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 1, 0, 14));
        form.setBackground(Color.WHITE);
        form.add(fieldRow("STUDENT NAME", nameField));
        form.add(fieldRow("ROLL NUMBER", rollField));

        JPanel subjRow = new JPanel(new BorderLayout(0, 5));
        subjRow.setBackground(Color.WHITE);
        JLabel sl = smallLabel("SELECT SUBJECT");
        subjectCombo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subjectCombo.setBackground(Color.WHITE);
        subjectCombo.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        subjectCombo.setPreferredSize(new Dimension(0, 42));
        subjRow.add(sl, BorderLayout.NORTH);
        subjRow.add(subjectCombo, BorderLayout.CENTER);
        form.add(subjRow);
        card.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setBackground(Color.WHITE);
        JLabel info = new JLabel("Exam: " + QUESTIONS_PER_EXAM + " questions  •  Time: "
                + EXAM_MINUTES + " minutes  •  No negative marking", SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.ITALIC, 12));
        info.setForeground(new Color(110, 120, 135));
        bottom.add(info, BorderLayout.NORTH);

        JPanel btns = new JPanel(new GridLayout(1, 2, 12, 0));
        btns.setBackground(Color.WHITE);
        JButton startBtn = Theme.button("Start Exam  →", Theme.GREEN);
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        JButton resultsBtn = Theme.outlineButton("View Past Results", Theme.NAVY);
        btns.add(startBtn);
        btns.add(resultsBtn);
        bottom.add(btns, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        outer.add(card, BorderLayout.CENTER);

        startBtn.addActionListener(e -> startExam());
        resultsBtn.addActionListener(e -> new ResultsFrame().setVisible(true));
        return outer;
    }

    private JPanel fieldRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setBackground(Color.WHITE);
        row.add(smallLabel(labelText), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(new Color(110, 120, 135));
        return l;
    }

    private void startExam() {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();
        if (name.isEmpty() || roll.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your name and roll number.",
                    "Details missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String subject = (String) subjectCombo.getSelectedItem();

        String instructions = """
                INSTRUCTIONS - Please read carefully:
                
                1. The exam has %d questions of subject: %s
                2. Total time is %d minutes. The exam submits itself
                   automatically when the time is over.
                3. Use the question palette on the right to jump to
                   any question.
                4. Green = answered, Red = seen but not answered,
                   Purple = marked for review.
                5. There is no negative marking.
                
                All the best, %s!
                """.formatted(QUESTIONS_PER_EXAM, subject, EXAM_MINUTES, name);

        int ok = JOptionPane.showConfirmDialog(this, instructions,
                "Exam Instructions", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        dispose();
        SwingUtilities.invokeLater(() ->
                new ExamFrame(name, roll, subject).setVisible(true));
    }
}
