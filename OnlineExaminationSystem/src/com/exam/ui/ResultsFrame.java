package com.exam.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/** Shows all past results saved in data/results.csv (teacher's view). */
public class ResultsFrame extends JFrame {

    public ResultsFrame() {
        super("Past Results");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel head = new JLabel("  All Submitted Results (data/results.csv)", SwingConstants.LEFT);
        head.setFont(new Font("SansSerif", Font.BOLD, 14));
        head.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        add(head, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel();
        Path file = Paths.get("data", "results.csv");
        try {
            if (Files.exists(file)) {
                List<String> lines = Files.readAllLines(file);
                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",", -1);
                    if (i == 0) model.setColumnIdentifiers(parts);
                    else model.addRow(parts);
                }
            }
        } catch (IOException e) {
            // table stays empty
        }
        if (model.getColumnCount() == 0) {
            model.setColumnIdentifiers(new String[]{"No results found yet"});
        }
        JTable table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
