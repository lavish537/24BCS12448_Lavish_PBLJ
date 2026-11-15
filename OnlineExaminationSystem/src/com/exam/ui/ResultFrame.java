package com.exam.ui;

import com.exam.model.ExamResult;
import com.exam.model.Question;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Result screen: summary card + full answer review table. */
public class ResultFrame extends JFrame {

    public ResultFrame(ExamResult result, List<Question> questions, int[] answers) {
        super("Exam Result");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---- Summary ----
        Theme.GradientPanel summary = new Theme.GradientPanel(Theme.NAVY_DARK, Theme.NAVY_LIGHT);
        summary.setLayout(new GridLayout(2, 1));
        summary.setBorder(BorderFactory.createEmptyBorder(12, 16, 10, 16));

        JLabel line1 = new JLabel((result.isPass() ? "🎉 " : "")
                + result.getStudentName() + "  (" + result.getRollNo() + ")   —   "
                + result.getSubject());
        line1.setForeground(Color.WHITE);
        line1.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel line2 = new JLabel("Score: " + result.getCorrect() + " / " + result.getTotalQuestions()
                + "   |   Percentage: " + String.format("%.1f%%", result.getPercentage())
                + "   |   Grade: " + result.getGrade()
                + "   |   Attempted: " + result.getAttempted()
                + "   |   Wrong: " + result.getWrong()
                + "   |   Time Used: " + ExamResult.formatTime(result.getSecondsUsed())
                + "   |   " + (result.isPass() ? "PASS ✓" : "FAIL ✗"));
        line2.setForeground(new Color(210, 228, 250));
        line2.setFont(new Font("SansSerif", Font.PLAIN, 13));

        summary.add(line1);
        summary.add(line2);
        add(summary, BorderLayout.NORTH);

        // ---- Answer review ----
        String[] cols = {"Q.No", "Question", "Your Answer", "Correct Answer", "Result"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String yours = answers[i] < 0 ? "(Not attempted)"
                    : (char) ('A' + answers[i]) + ". " + q.getOptions().get(answers[i]);
            String right = (char) ('A' + q.getCorrectOption()) + ". "
                    + q.getOptions().get(q.getCorrectOption());
            String res = answers[i] < 0 ? "Skipped" : (q.isCorrect(answers[i]) ? "✓ Correct" : "✗ Wrong");
            model.addRow(new Object[]{i + 1, q.getText(), yours, right, res});
        }
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(Theme.NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(28);
        table.setGridColor(new Color(215, 222, 232));
        table.setShowGrid(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(330);
        table.getColumnModel().getColumn(2).setPreferredWidth(190);
        table.getColumnModel().getColumn(3).setPreferredWidth(190);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- Bottom ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        JButton homeBtn = new JButton("Back to Home");
        JButton exitBtn = new JButton("Exit");
        homeBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        exitBtn.addActionListener(e -> System.exit(0));
        bottom.add(homeBtn);
        bottom.add(exitBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}
