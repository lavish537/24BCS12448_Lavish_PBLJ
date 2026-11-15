package com.exam.ui;

import com.exam.data.QuestionBank;
import com.exam.model.ExamResult;
import com.exam.model.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main exam screen (professional layout).
 * - Top bar: candidate details + big countdown timer (auto-submit at 0)
 * - Left: question in a white card, options as large clickable rows,
 *         action buttons (Previous / Clear / Mark / Save & Next / Submit)
 * - Right: question palette with colour status + live counts
 * The paper is randomly picked from the subject's question bank.
 */
public class ExamFrame extends JFrame {

    private static final int NOT_VISITED = 0, NOT_ANSWERED = 1, ANSWERED = 2, MARKED = 3;

    private static final Color NAVY = new Color(25, 70, 130);
    private static final Color GREEN = new Color(34, 139, 60);
    private static final Color RED = new Color(200, 45, 45);
    private static final Color PURPLE = new Color(120, 70, 160);

    private final String studentName, rollNo, subject;
    private final List<Question> questions;
    private final int[] answers;      // -1 = no answer
    private final int[] status;
    private int current = 0;
    private int secondsLeft;

    private final JLabel timerLabel = new JLabel();
    private final JLabel questionTitle = new JLabel();
    private final JTextArea questionText = new JTextArea();
    private final JRadioButton[] optionBtns = new JRadioButton[4];
    private final JPanel[] optionPanels = new JPanel[4];
    private final ButtonGroup optionGroup = new ButtonGroup();
    private final JButton[] paletteBtns;
    private final JLabel countsLabel = new JLabel();
    private Timer timer;
    private boolean submitted = false;

    public ExamFrame(String studentName, String rollNo, String subject) {
        super("Online Examination - " + subject);
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.subject = subject;

        // random paper: shuffle the bank and pick the exam size
        List<Question> bank = new ArrayList<>(QuestionBank.getQuestions(subject));
        Collections.shuffle(bank);
        this.questions = new ArrayList<>(bank.subList(0,
                Math.min(LoginFrame.QUESTIONS_PER_EXAM, bank.size())));

        int n = questions.size();
        this.answers = new int[n];
        this.status = new int[n];
        java.util.Arrays.fill(answers, -1);
        this.secondsLeft = LoginFrame.EXAM_MINUTES * 60;
        this.paletteBtns = new JButton[n];

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildQuestionPanel(), BorderLayout.CENTER);
        add(buildPalettePanel(), BorderLayout.EAST);

        status[0] = NOT_ANSWERED;
        showQuestion(0);
        startTimer();
    }

    // ---------------- top bar ----------------
    private JPanel buildTopBar() {
        Theme.GradientPanel top = new Theme.GradientPanel(Theme.NAVY_DARK, Theme.NAVY_LIGHT);
        top.setLayout(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel("ONLINE EXAMINATION  —  " + subject.toUpperCase());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        JLabel cand = new JLabel("Candidate: " + studentName + "    |    Roll No: " + rollNo
                + "    |    Total Questions: " + questions.size() + "    |    Max Marks: " + questions.size());
        cand.setForeground(new Color(200, 220, 245));
        cand.setFont(new Font("SansSerif", Font.PLAIN, 13));
        left.add(title);
        left.add(cand);
        top.add(left, BorderLayout.WEST);

        JPanel timerBox = new JPanel(new GridLayout(2, 1));
        timerBox.setBackground(new Color(13, 42, 74));
        timerBox.setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 18));
        JLabel cap = new JLabel("TIME LEFT", SwingConstants.CENTER);
        cap.setForeground(new Color(170, 200, 235));
        cap.setFont(new Font("SansSerif", Font.BOLD, 11));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        timerBox.add(cap);
        timerBox.add(timerLabel);
        top.add(timerBox, BorderLayout.EAST);
        updateTimerLabel();
        return top;
    }

    // ---------------- question area ----------------
    private JPanel buildQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 18, 12, 12));
        panel.setBackground(new Color(240, 243, 247));

        questionTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        questionTitle.setForeground(NAVY);
        panel.add(questionTitle, BorderLayout.NORTH);

        // white card holding question + options
        JPanel card = new JPanel(new BorderLayout(10, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 200, 215)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        questionText.setFont(new Font("SansSerif", Font.BOLD, 19));
        questionText.setForeground(new Color(20, 20, 20));
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setEditable(false);
        questionText.setBackground(Color.WHITE);
        questionText.setRows(2);
        card.add(questionText, BorderLayout.NORTH);

        JPanel options = new JPanel(new GridLayout(4, 1, 6, 10));
        options.setBackground(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optionBtns[i] = new JRadioButton();
            optionBtns[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionBtns[i].setForeground(new Color(20, 20, 20));
            optionBtns[i].setBackground(Color.WHITE);
            optionBtns[i].setFocusPainted(false);
            optionGroup.add(optionBtns[i]);

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(190, 200, 215)),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)));
            row.add(optionBtns[i], BorderLayout.CENTER);
            row.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    optionBtns[idx].setSelected(true);
                    highlightOptions();
                }
            });
            optionBtns[i].addActionListener(e -> highlightOptions());
            optionPanels[i] = row;
            options.add(row);
        }
        card.add(options, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);

        // action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.setBackground(new Color(240, 243, 247));
        JButton prevBtn = makeBtn("<< Previous", new Color(90, 100, 115), Color.WHITE);
        JButton clearBtn = makeBtn("Clear Response", new Color(90, 100, 115), Color.WHITE);
        JButton markBtn = makeBtn("Mark for Review", PURPLE, Color.WHITE);
        JButton nextBtn = makeBtn("Save & Next >>", GREEN, Color.WHITE);
        JButton submitBtn = makeBtn("SUBMIT EXAM", RED, Color.WHITE);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        prevBtn.addActionListener(e -> { saveCurrent(); if (current > 0) showQuestion(current - 1); });
        nextBtn.addActionListener(e -> { saveCurrent(); if (current < questions.size() - 1) showQuestion(current + 1); });
        clearBtn.addActionListener(e -> { optionGroup.clearSelection(); answers[current] = -1; setStatus(NOT_ANSWERED); highlightOptions(); });
        markBtn.addActionListener(e -> { saveCurrent(); setStatus(MARKED); if (current < questions.size() - 1) showQuestion(current + 1); });
        submitBtn.addActionListener(e -> confirmSubmit());

        actions.add(prevBtn);
        actions.add(clearBtn);
        actions.add(markBtn);
        actions.add(nextBtn);
        actions.add(submitBtn);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        // Theme.button paints the colour on every platform
        JButton b = Theme.button(text, bg);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return b;
    }

    // ---------------- palette ----------------
    private JPanel buildPalettePanel() {
        JPanel palette = new JPanel(new BorderLayout(6, 10));
        palette.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(14, 8, 14, 14),
                BorderFactory.createTitledBorder("Question Palette")));
        palette.setPreferredSize(new Dimension(228, 0));
        palette.setBackground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(0, 4, 7, 7));
        grid.setBackground(Color.WHITE);
        for (int i = 0; i < questions.size(); i++) {
            JButton b = new JButton(String.valueOf(i + 1));
            b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            b.setOpaque(true);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setMargin(new Insets(6, 2, 6, 2));
            b.setFocusPainted(false);
            final int idx = i;
            b.addActionListener(e -> { saveCurrent(); showQuestion(idx); });
            paletteBtns[i] = b;
            grid.add(b);
        }
        JPanel gridWrap = new JPanel(new BorderLayout());
        gridWrap.setBackground(Color.WHITE);
        gridWrap.add(grid, BorderLayout.NORTH);
        palette.add(gridWrap, BorderLayout.CENTER);

        JPanel legend = new JPanel(new GridLayout(6, 1, 2, 4));
        legend.setBackground(Color.WHITE);
        legend.add(legendItem(GREEN, "Answered"));
        legend.add(legendItem(RED, "Not Answered"));
        legend.add(legendItem(PURPLE, "Marked for Review"));
        legend.add(legendItem(new Color(150, 150, 150), "Not Visited"));
        countsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        legend.add(countsLabel);
        palette.add(legend, BorderLayout.SOUTH);
        return palette;
    }

    private JLabel legendItem(Color col, String text) {
        JLabel l = new JLabel("  ■  " + text);
        l.setForeground(col.darker());
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    // ---------------- behaviour ----------------
    private void showQuestion(int idx) {
        current = idx;
        if (status[idx] == NOT_VISITED) status[idx] = NOT_ANSWERED;
        Question q = questions.get(idx);
        questionTitle.setText("Question " + (idx + 1) + " of " + questions.size()
                + "        |        Marks: 1        |        Negative Marks: 0");
        questionText.setText(q.getText());
        for (int i = 0; i < 4; i++) {
            optionBtns[i].setText((char) ('A' + i) + ".   " + q.getOptions().get(i));
        }
        optionGroup.clearSelection();
        if (answers[idx] >= 0) optionBtns[answers[idx]].setSelected(true);
        highlightOptions();
        refreshPalette();
    }

    private void highlightOptions() {
        for (int i = 0; i < 4; i++) {
            boolean sel = optionBtns[i].isSelected();
            optionPanels[i].setBackground(sel ? new Color(225, 240, 255) : Color.WHITE);
            optionBtns[i].setBackground(sel ? new Color(225, 240, 255) : Color.WHITE);
            optionPanels[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(sel ? NAVY : new Color(190, 200, 215), sel ? 2 : 1),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        }
    }

    private void saveCurrent() {
        for (int i = 0; i < 4; i++) {
            if (optionBtns[i].isSelected()) {
                answers[current] = i;
                if (status[current] != MARKED) status[current] = ANSWERED;
                return;
            }
        }
    }

    private void setStatus(int st) {
        status[current] = st;
        refreshPalette();
    }

    private void refreshPalette() {
        int answered = 0, marked = 0, notAnswered = 0, notVisited = 0;
        for (int i = 0; i < questions.size(); i++) {
            JButton b = paletteBtns[i];
            switch (status[i]) {
                case ANSWERED -> { b.setBackground(GREEN); b.setForeground(Color.WHITE); answered++; }
                case MARKED -> { b.setBackground(PURPLE); b.setForeground(Color.WHITE); marked++; if (answers[i] >= 0) answered++; }
                case NOT_ANSWERED -> { b.setBackground(RED); b.setForeground(Color.WHITE); notAnswered++; }
                default -> { b.setBackground(new Color(228, 228, 228)); b.setForeground(Color.BLACK); notVisited++; }
            }
            if (i == current) b.setBorder(BorderFactory.createLineBorder(NAVY, 2));
            else b.setBorder(UIManager.getBorder("Button.border"));
        }
        countsLabel.setText("<html>Answered: " + answered + " &nbsp;|&nbsp; Marked: " + marked
                + "<br>Not Answered: " + notAnswered + " &nbsp;|&nbsp; Not Visited: " + notVisited + "</html>");
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            secondsLeft--;
            updateTimerLabel();
            if (secondsLeft == 60) {
                JOptionPane.showMessageDialog(this, "Only 1 minute left!",
                        "Time Warning", JOptionPane.WARNING_MESSAGE);
            }
            if (secondsLeft <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Time is over! Your exam is being submitted.",
                        "Time Up", JOptionPane.INFORMATION_MESSAGE);
                submitExam();
            }
        });
        timer.start();
    }

    private void updateTimerLabel() {
        timerLabel.setText(ExamResult.formatTime(Math.max(secondsLeft, 0)));
        if (secondsLeft <= 60) timerLabel.setForeground(new Color(255, 110, 110));
    }

    private void confirmSubmit() {
        int unanswered = 0;
        for (int a : answers) if (a < 0) unanswered++;
        String msg = unanswered == 0
                ? "You have answered all questions.\nSubmit the exam?"
                : "You still have " + unanswered + " unanswered question(s).\nSubmit the exam anyway?";
        int ok = JOptionPane.showConfirmDialog(this, msg, "Confirm Submit",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) submitExam();
    }

    private void submitExam() {
        if (submitted) return;
        submitted = true;
        if (timer != null) timer.stop();
        saveCurrent();

        int correct = 0, attempted = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers[i] >= 0) {
                attempted++;
                if (questions.get(i).isCorrect(answers[i])) correct++;
            }
        }
        int used = LoginFrame.EXAM_MINUTES * 60 - Math.max(secondsLeft, 0);
        ExamResult result = new ExamResult(studentName, rollNo, subject,
                questions.size(), attempted, correct,
                LoginFrame.EXAM_MINUTES * 60, used);
        saveResult(result);

        dispose();
        SwingUtilities.invokeLater(() ->
                new ResultFrame(result, questions, answers).setVisible(true));
    }

    private void saveResult(ExamResult result) {
        try {
            Path dir = Paths.get("data");
            Files.createDirectories(dir);
            Path file = dir.resolve("results.csv");
            boolean newFile = !Files.exists(file);
            try (BufferedWriter bw = Files.newBufferedWriter(file,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                if (newFile) {
                    bw.write("DateTime,Student,RollNo,Subject,Correct,Total,Percentage,Grade,TimeUsed");
                    bw.newLine();
                }
                bw.write(result.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: result could not be saved: " + e.getMessage());
        }
    }
}
