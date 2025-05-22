package org.bupt.persosnalfinance.Front.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javafx.application.Platform;
import org.bupt.persosnalfinance.Front.ManualEntry.ManualEntryManager;
import org.bupt.persosnalfinance.Front.ManualEntry.TransactionListManager;
import org.bupt.persosnalfinance.Front.AIBudgetPlanner.FullBudgetPlannerManager;
import org.bupt.persosnalfinance.Front.ExportCsvPanel.ExportCsvPanel;
import org.bupt.persosnalfinance.Front.AlertFront.BudgetApp;
import org.bupt.persosnalfinance.Front.HomePage.HomePage;
import org.bupt.persosnalfinance.Front.Localization.CombinedUIManager;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class Dashboard extends JFrame {

    private Map<String, Double> monthlyExpenses = new HashMap<>();
    private double monthlyIncome = 0;
    private DefaultTableModel tableModel;

    public Dashboard() {
        setTitle("Personal Finance Dashboard");
        setSize(1200, 724);
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

        JPanel RightTitle = new JPanel(new GridLayout(1, 2, 5,5));
        JButton RefreshBtn = new JButton("Refresh Table");
        RefreshBtn.addActionListener(e -> refreshtable());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JLabel dateLabel = new JLabel("Date: " + dateFormat.format(new Date()), SwingConstants.RIGHT);
        RightTitle.add(RefreshBtn);
        RightTitle.add(dateLabel);
        headerPanel.add(RightTitle, BorderLayout.EAST);

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

        TransactionInformation.loadFromJSON("src/main/data/transactionInformation.json");
        String[] columnNames = {"Date", "Amount", "Type", "Object", "Remarks"};
        tableModel = new DefaultTableModel(columnNames, 0);
        refreshtable();

        JTable transactionTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));

        summaryPanel.add(scrollPane, BorderLayout.CENTER);

        return summaryPanel;
    }

    private void refreshtable() {
        tableModel.setRowCount(0);
        List<TransactionInformation> transactions = TransactionInformation.transactionList;

        // Sort the transaction information in chronological order.
        transactions.sort((t1, t2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                return sdf.parse(t2.getDate()).compareTo(sdf.parse(t1.getDate())); // Sort by date in chronological order.
            } catch (Exception e) {
                return 0;
            }
        });

        for (int i = 0; i < 29; i++) {
            TransactionInformation t = transactions.get(i);
            if (Objects.equals(t.getType(), "Income")) {
                tableModel.addRow(new Object[]{t.getDate(), String.format("+ %.2f", t.getAmount()), t.getType(), t.getObject(), t.getRemarks()});
            } else {
                tableModel.addRow(new Object[]{t.getDate(), String.format("- %.2f", t.getAmount()), t.getType(), t.getObject(), t.getRemarks()});
            }
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton entryBtn = new JButton("Entry of Transaction Information");
        entryBtn.addActionListener(e -> openTransactionEntry());

        JButton transactionListBtn = new JButton("Detailed Transaction Lists");
        transactionListBtn.addActionListener(e -> openTransactionList());

        JButton overspendBtn = new JButton("Overspend Reminder");
        overspendBtn.addActionListener(e -> openOverspendReminder());

        JButton ExportcsvBtn = new JButton("Save and Export as Csv");
        ExportcsvBtn.addActionListener(e -> openExportCsv());

        JButton aiBudgetBtn = new JButton("AI Personalized Budget Planning");
        aiBudgetBtn.addActionListener(e -> openAIBudgetPlanning());

        JButton reqLocBtn = new JButton("Requirement Localization");
        reqLocBtn.addActionListener(e -> openRequirementLocalization());

//        JButton aiAlertsBtn = new JButton("AI Budget Progress Alerts");
//        aiAlertsBtn.addActionListener(e -> openAIBudgetAlerts());

        buttonPanel.add(entryBtn);
        buttonPanel.add(transactionListBtn);
        buttonPanel.add(ExportcsvBtn);
        buttonPanel.add(overspendBtn);
        buttonPanel.add(aiBudgetBtn);
        buttonPanel.add(reqLocBtn);
//        buttonPanel.add(aiAlertsBtn);

        return buttonPanel;
    }

    private void openHomePage() {
        String[] options = {"Yes, Return Home", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Are you sure you have saved your account book? After returning to the home page, it will not be saved.",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
        );

        if (choice == 0) { // "Yes, Return Home" 被选中
            // 关闭所有可见的窗口（包括 Dashboard 和其他子窗口）
            closeAllWindows();

            // 重新打开 HomePage
            new HomePage().setVisible(true);
        }
        // 如果选择 "Cancel"，不做任何操作
    }


    private void closeAllWindows() {
        // 获取所有可见的 JFrame
        for (Frame frame : Frame.getFrames()) {
            if (frame.isVisible()) {
                frame.dispose();
            }
        }

        // 获取所有可见的 JDialog（如果有）
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window.isVisible()) {
                window.dispose();
            }
        }
    }

    private void openTransactionList() {
        TransactionListManager.showTransactionList();
    }

    private void openTransactionEntry() {
        ManualEntryManager.showManualEntry();
    }

    private void openRequirementLocalization() {
        CombinedUIManager.showCombinedUI();
    }

    //待对接同学调试完毕后完善
    private void openOverspendReminder() {
        new Thread(() -> {
            try {
                BudgetApp.launch(BudgetApp.class);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(null,
                        "Budget application window is already open",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to launch budget application: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void openExportCsv() {
        // Create a new dialog window
        JDialog exportDialog = new JDialog();
        exportDialog.setTitle("Export CSV");
        exportDialog.setModal(true); // Make it modal to block other windows
        exportDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Create and add the ExportCsvPanel to the dialog
        ExportCsvPanel exportPanel = new ExportCsvPanel();
        exportDialog.add(exportPanel);

        // Set preferred size and pack
        exportDialog.setPreferredSize(new Dimension(600, 400));
        exportDialog.pack();

        // Center the dialog relative to parent window
        exportDialog.setLocationRelativeTo(null);
        exportDialog.setVisible(true);
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