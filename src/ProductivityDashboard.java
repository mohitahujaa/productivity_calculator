package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.util.Properties;
import config.ConfigLoader;

public class ProductivityDashboard extends JFrame {
    private String username;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel totalHoursLabel;
    private JLabel productiveHoursLabel;
    private JLabel productivityLabel;
    
    
    public ProductivityDashboard(String username) {
        this.username = username;
        Properties uiProps = ConfigLoader.load("config/ui.properties");
        String fontName = uiProps.getProperty("ui.font.name", "SansSerif");
        int fontSize = Integer.parseInt(uiProps.getProperty("ui.font.size", "12"));
        Font uiFont_for_stats = new Font(fontName, Font.BOLD, fontSize);
        fontName = uiProps.getProperty("ui.font.tdata.name");
        fontSize = Integer.parseInt(uiProps.getProperty("ui.font.tdata.size", "12"));

        Font uiFont_for_tdata = new Font(fontName, Font.PLAIN, fontSize);

        fontSize = Integer.parseInt(uiProps.getProperty("ui.tablheader", "12"));
        Font uiFont_for_theader = new Font(fontName, Font.BOLD, fontSize);

        setTitle("Productivity Dashboard - " + username);
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Task", "Category", "Hours"}, 0);
        table = new JTable(tableModel);
        loadTasks();

        table.setFont(uiFont_for_tdata);
        table.setRowHeight(Integer.parseInt(uiProps.getProperty("ui.tablerowHeight")));
        JScrollPane scrollPane = new JScrollPane(table);

        JTableHeader theader = table.getTableHeader();
        theader.setFont(uiFont_for_theader);

        JPanel tableWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 60, 40)); 
        tableWrapper.add(scrollPane);
        add(tableWrapper, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Task");
        JButton deleteBtn = new JButton("Delete Task");
        JButton saveBtn = new JButton("Save");

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Top stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        totalHoursLabel = new JLabel("Total Hours: 0");
        totalHoursLabel.setFont(uiFont_for_stats);
        productiveHoursLabel = new JLabel("Productive Hours: 0");
        productiveHoursLabel.setFont(uiFont_for_stats);
        productivityLabel = new JLabel("Productivity: 0%");
        productivityLabel.setFont(uiFont_for_stats);
        
        statsPanel.add(totalHoursLabel);
        statsPanel.add(productiveHoursLabel);
        statsPanel.add(productivityLabel);
        add(statsPanel, BorderLayout.NORTH);

        // Button listeners
        addBtn.addActionListener(e -> addTask());
        deleteBtn.addActionListener(e -> deleteTask());
        saveBtn.addActionListener(e -> saveTasks());

        updateSummary();
        setVisible(true);
    }

    private void loadTasks() {
        try (BufferedReader br = new BufferedReader(new FileReader("user_data/productivity_calculator.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.strip().split(",");
                if (parts.length != 4) continue;
                if (!parts[0].equals(username)) continue;

                tableModel.addRow(new Object[]{parts[1], parts[2], parts[3]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void saveTasks() {
        try (BufferedReader br = new BufferedReader(new FileReader("productivity_calculator.txt"));
             BufferedWriter bw = new BufferedWriter(new FileWriter("temp.txt"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.strip().split(",");
                if (parts.length != 4) continue;

                if (!parts[0].equals(username)) {
                    bw.write(line);
                    bw.newLine();
                }
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String task = tableModel.getValueAt(i, 0).toString();
                String category = tableModel.getValueAt(i, 1).toString().toLowerCase();
                String hours = tableModel.getValueAt(i, 2).toString();

                bw.write(username + "," + task + "," + category + "," + hours);
                bw.newLine();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            return;
        }

        // Replace original file
        new File("productivity_calculator.txt").delete();
        new File("temp.txt").renameTo(new File("productivity_calculator.txt"));

        updateSummary();
        JOptionPane.showMessageDialog(this, "Tasks saved successfully.");
    }

    private void addTask() {
        tableModel.addRow(new Object[]{"", "", ""});
        updateSummary();
    }

    private void deleteTask() {
        int selected = table.getSelectedRow();
        if (selected >= 0) tableModel.removeRow(selected);
        updateSummary();
    }

    private void updateSummary() {
        double totalHours = 0;
        double productiveHours = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String category = tableModel.getValueAt(i, 1).toString().toLowerCase();
            double hours;

            try {
                hours = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
            } catch (NumberFormatException e) {
                continue;
            }

            totalHours += hours;
            if (category.equals("productive")) {
                productiveHours += hours;
            }
        }

        double productivity = (totalHours > 0) ? (productiveHours / totalHours) * 100 : 0;

        totalHoursLabel.setText("Total Hours: " + totalHours);
        productiveHoursLabel.setText("Productive Hours: " + productiveHours);
        productivityLabel.setText(String.format("Productivity: %.2f%%", productivity));
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username != null && !username.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> new ProductivityDashboard(username));
        }
    }
}

