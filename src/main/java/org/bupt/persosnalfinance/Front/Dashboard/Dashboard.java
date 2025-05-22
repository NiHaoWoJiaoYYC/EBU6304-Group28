package org.bupt.persosnalfinance.Front.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import org.bupt.persosnalfinance.Front.ManualEntry.ManualEntryManager;
import org.bupt.persosnalfinance.Front.ManualEntry.TransactionListManager;
import org.bupt.persosnalfinance.Front.AIBudgetPlanner.FullBudgetPlannerManager;

public class Dashboard extends JFrame {

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

        initializeSampleData();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void initializeSampleData() {
        monthlyExpenses.put("Food", 1352.44);
        monthlyExpenses.put("Housing/Rent", 910.00);
        monthlyExpenses.put("Daily Necessities", 676.50);
        monthlyExpenses.put("Transportation", 509.00);
        monthlyExpenses.put("Entertainment", 120.00);
        monthlyExpenses.put("Shopping", 910.00);
        monthlyExpenses.put("Healthcare", 676.50);
        monthlyExpenses.put("Education", 509.00);
        monthlyExpenses.put("Childcare", 120.00);
        monthlyExpenses.put("Gifts", 910.00);
        monthlyExpenses.put("Savings", 676.50);
        monthlyExpenses.put("Others", 509.00);

        monthlyIncome = 10216.79;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JButton HomePageBtn = new JButton("Home Page");
        HomePageBtn.addActionListener(e -> openHomePage());
        headerPanel.add(HomePageBtn, BorderLayout.WEST);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JLabel dateLabel = new JLabel("Date: " + dateFormat.format(new Date()), SwingConstants.RIGHT);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(createPieChartPanel());
        centerPanel.add(createSummaryPanel());
        return centerPanel;
    }

    private JPanel createPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
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

        JPanel topSummary = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel expenditurePanel = new JPanel(new BorderLayout());
        expenditurePanel.add(new JLabel("Monthly Expenditure", SwingConstants.CENTER), BorderLayout.NORTH);
        double totalExpenses = monthlyExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
        JLabel expenditureAmount = new JLabel(String.format("-$%.2f", totalExpenses), SwingConstants.CENTER);
        expenditureAmount.setForeground(Color.RED);
        expenditureAmount.setFont(new Font("Arial", Font.BOLD, 20));
        expenditurePanel.add(expenditureAmount, BorderLayout.CENTER);

        JPanel incomePanel = new JPanel(new BorderLayout());
        incomePanel.add(new JLabel("Monthly Income", SwingConstants.CENTER), BorderLayout.NORTH);
        JLabel incomeAmount = new JLabel(String.format("$%.2f", monthlyIncome), SwingConstants.CENTER);
        incomeAmount.setForeground(Color.GREEN);
        incomeAmount.setFont(new Font("Arial", Font.BOLD, 20));
        incomePanel.add(incomeAmount, BorderLayout.CENTER);

        topSummary.add(expenditurePanel);
        topSummary.add(incomePanel);
        summaryPanel.add(topSummary, BorderLayout.NORTH);

        String[] columnNames = {"Date", "Category", "Description", "Amount"};
        JTable transactionTable = new JTable(recentTransactions, columnNames);
        transactionTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));

        summaryPanel.add(scrollPane, BorderLayout.CENTER);

        return summaryPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton entryBtn = new JButton("Entry of Transaction Information");
        entryBtn.addActionListener(e -> openTransactionEntry());

        JButton transactionListBtn = new JButton("Detailed Transaction Lists");
        transactionListBtn.addActionListener(e -> openTransactionList());

        JButton overspendBtn = new JButton("Overspend Reminder");
        overspendBtn.addActionListener(e -> openOverspendReminder());

        JButton aiClassifyBtn = new JButton("AI Transaction Classify");
        aiClassifyBtn.addActionListener(e -> openAITransactionClassify());

        JButton aiBudgetBtn = new JButton("AI Personalized Budget Planning");
        aiBudgetBtn.addActionListener(e -> openAIBudgetPlanning());

        JButton reqLocBtn = new JButton("Requirement Localization");
        reqLocBtn.addActionListener(e -> openRequirementLocalization());

//        JButton aiAlertsBtn = new JButton("AI Budget Progress Alerts");
//        aiAlertsBtn.addActionListener(e -> openAIBudgetAlerts());

        buttonPanel.add(entryBtn);
        buttonPanel.add(transactionListBtn);
        buttonPanel.add(overspendBtn);
        buttonPanel.add(aiClassifyBtn);
        buttonPanel.add(aiBudgetBtn);
        buttonPanel.add(reqLocBtn);
//        buttonPanel.add(aiAlertsBtn);

        return buttonPanel;
    }

    private void openHomePage() {
        JOptionPane.showMessageDialog(this, "Are you sure you have saved your account book? After returning to the home page, it will not be saved.");
    }

    private void openTransactionList() {
        TransactionListManager.showTransactionList();
    }

    private void openTransactionEntry() {
        ManualEntryManager.showManualEntry();
    }

    private void openRequirementLocalization() {
        JOptionPane.showMessageDialog(this, "Opening Requirement Localization");
    }

    private void openOverspendReminder() {
        JOptionPane.showMessageDialog(this, "Opening Overspend Reminder");
    }

    private void openAITransactionClassify() {
        JOptionPane.showMessageDialog(this, "Opening AI Transaction Classify");
    }

    private void openAIBudgetPlanning() {
        FullBudgetPlannerManager.showBudgetPlanner();
    }

//    private void openAIBudgetAlerts() {
//        JOptionPane.showMessageDialog(this, "Opening AI Budget Progress Alerts");
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}