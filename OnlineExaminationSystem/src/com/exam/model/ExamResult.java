package com.exam.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Final result of one submitted exam. */
public class ExamResult {
    private final String studentName;
    private final String rollNo;
    private final String subject;
    private final int totalQuestions;
    private final int attempted;
    private final int correct;
    private final int totalSeconds;
    private final int secondsUsed;

    public ExamResult(String studentName, String rollNo, String subject,
                      int totalQuestions, int attempted, int correct,
                      int totalSeconds, int secondsUsed) {
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.attempted = attempted;
        this.correct = correct;
        this.totalSeconds = totalSeconds;
        this.secondsUsed = secondsUsed;
    }

    public double getPercentage() {
        return totalQuestions == 0 ? 0 : (correct * 100.0 / totalQuestions);
    }

    public String getGrade() {
        double p = getPercentage();
        if (p >= 90) return "A+";
        if (p >= 80) return "A";
        if (p >= 70) return "B+";
        if (p >= 60) return "B";
        if (p >= 50) return "C";
        if (p >= 40) return "D";
        return "F (Fail)";
    }

    public boolean isPass() { return getPercentage() >= 40; }

    public String getStudentName() { return studentName; }
    public String getRollNo() { return rollNo; }
    public String getSubject() { return subject; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getAttempted() { return attempted; }
    public int getCorrect() { return correct; }
    public int getWrong() { return attempted - correct; }
    public int getSecondsUsed() { return secondsUsed; }

    public static String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    /** One line for results.csv */
    public String toCsv() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return String.join(",",
                ts, studentName, rollNo, subject,
                String.valueOf(correct), String.valueOf(totalQuestions),
                String.format("%.1f%%", getPercentage()), getGrade(),
                formatTime(secondsUsed));
    }
}
