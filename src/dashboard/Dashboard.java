import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class Dashboard extends JFrame {

    // Sample data for demonstration
    private Map<String, Double> monthlyExpenses = new HashMap<>();
    private double monthlyIncome = 0;
    private Object[][] recentTransactions = {
            {"2025-03-19", "Food", "family dinner", -212.37},
            {"2025-03-18", "Transportation", "Taxi", -72.91},
            {"2025-03-18", "Food", "buy groceries", -34.20}
    };

    public Dashboard() {
        setTitle("Personal Finance Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize sample data
        initializeSampleData();

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add header panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Add center panel with pie chart and summary
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);

        // Add button panel at the bottom
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void initializeSampleData() {
        // Sample expense data
        monthlyExpenses.put("Food", 1352.44);
        monthlyExpenses.put("Transportation", 910.00);
        monthlyExpenses.put("Entertainment", 676.50);
        monthlyExpenses.put("Shopping", 509.00);
        monthlyExpenses.put("Healthcare", 120.00);

        // Sample income
        monthlyIncome = 10216.79;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Left: Detailed Transaction Lists button
        JButton transactionListBtn = new JButton("Detailed Transaction Lists");
        transactionListBtn.addActionListener(e -> openTransactionList());
        headerPanel.add(transactionListBtn, BorderLayout.WEST);

        // Right: Current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JLabel dateLabel = new JLabel("Date: " + dateFormat.format(new Date()), SwingConstants.RIGHT);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Left: Pie chart
        centerPanel.add(createPieChartPanel());

        // Right: Summary panel
        centerPanel.add(createSummaryPanel());

        return centerPanel;
    }

    private JPanel createPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Add expense categories to the pie chart
        for (Map.Entry<String, Double> entry : monthlyExpenses.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Monthly Expenses by Category",
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createTitledBorder("Expense Distribution"));

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));

        // Top: Income and Expenditure summary
        JPanel topSummary = new JPanel(new GridLayout(1, 2, 10, 10));

        // Monthly Expenditure
        JPanel expenditurePanel = new JPanel(new BorderLayout());
        expenditurePanel.add(new JLabel("Monthly Expenditure", SwingConstants.CENTER), BorderLayout.NORTH);

        double totalExpenses = monthlyExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
        JLabel expenditureAmount = new JLabel(String.format("-$%.2f", totalExpenses), SwingConstants.CENTER);
        expenditureAmount.setForeground(Color.RED);
        expenditureAmount.setFont(new Font("Arial", Font.BOLD, 20));
        expenditurePanel.add(expenditureAmount, BorderLayout.CENTER);

        // Monthly Income
        JPanel incomePanel = new JPanel(new BorderLayout());
        incomePanel.add(new JLabel("Monthly Income", SwingConstants.CENTER), BorderLayout.NORTH);

        JLabel incomeAmount = new JLabel(String.format("$%.2f", monthlyIncome), SwingConstants.CENTER);
        incomeAmount.setForeground(Color.GREEN);
        incomeAmount.setFont(new Font("Arial", Font.BOLD, 20));
        incomePanel.add(incomeAmount, BorderLayout.CENTER);

        topSummary.add(expenditurePanel);
        topSummary.add(incomePanel);

        summaryPanel.add(topSummary, BorderLayout.NORTH);

        // Bottom: Recent transactions table
        String[] columnNames = {"Date", "Category", "Description", "Amount"};
        JTable transactionTable = new JTable(recentTransactions, columnNames);
        transactionTable.setEnabled(false); // Make it non-editable

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));

        summaryPanel.add(scrollPane, BorderLayout.CENTER);

        return summaryPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        // First row of buttons
        JButton entryBtn = new JButton("Entry of Transaction Information");
        entryBtn.addActionListener(e -> openTransactionEntry());

        JButton reqLocBtn = new JButton("Requirement Localization");
        reqLocBtn.addActionListener(e -> openRequirementLocalization());

        JButton overspendBtn = new JButton("Overspend Reminder");
        overspendBtn.addActionListener(e -> openOverspendReminder());

        // Second row of buttons
        JButton aiClassifyBtn = new JButton("AI Transaction Classify");
        aiClassifyBtn.addActionListener(e -> openAITransactionClassify());

        JButton aiBudgetBtn = new JButton("AI Personalized Budget Planning");
        aiBudgetBtn.addActionListener(e -> openAIBudgetPlanning());

        JButton aiAlertsBtn = new JButton("AI Budget Progress Alerts");
        aiAlertsBtn.addActionListener(e -> openAIBudgetAlerts());

        // Add buttons to panel
        buttonPanel.add(entryBtn);
        buttonPanel.add(reqLocBtn);
        buttonPanel.add(overspendBtn);
        buttonPanel.add(aiClassifyBtn);
        buttonPanel.add(aiBudgetBtn);
        buttonPanel.add(aiAlertsBtn);

        return buttonPanel;
    }

    // Methods to open other panels (stubs - would be implemented to open the actual panels)
    private void openTransactionList() {
        // Implementation would open DetailedTransactionLists.java
        JOptionPane.showMessageDialog(this, "Opening Detailed Transaction Lists");
    }

    private void openTransactionEntry() {
        // Implementation would open EntryOfTransactionInformation.java
        JOptionPane.showMessageDialog(this, "Opening Entry of Transaction Information");
    }

    private void openRequirementLocalization() {
        // Implementation would open RequirementLocalization.java
        JOptionPane.showMessageDialog(this, "Opening Requirement Localization");
    }

    private void openOverspendReminder() {
        // Implementation would open OverspendReminder.java
        JOptionPane.showMessageDialog(this, "Opening Overspend Reminder");
    }

    private void openAITransactionClassify() {
        // Implementation would open AITransactionClassify.java
        JOptionPane.showMessageDialog(this, "Opening AI Transaction Classify");
    }

    private void openAIBudgetPlanning() {
        // Implementation would open AIPersonalizedBudgetPlanning.java
        JOptionPane.showMessageDialog(this, "Opening AI Personalized Budget Planning");
    }

    private void openAIBudgetAlerts() {
        // Implementation would open AIBudgetProgressAlerts.java
        JOptionPane.showMessageDialog(this, "Opening AI Budget Progress Alerts");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}
