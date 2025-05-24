package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bupt.persosnalfinance.Back.Service.BudgetAIService;
import org.bupt.persosnalfinance.dto.UserInfo;
import org.bupt.persosnalfinance.dto.SpendingRecord;
import org.bupt.persosnalfinance.Front.visualization.CurrentMonthBudgetPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;
import java.util.Map;

public class FullBudgetPlannerApp {
    private static FullBudgetPlannerApp instance;
    private JFrame frame;
    private JTextArea suggestionArea;
    private JPanel centerPanel;
    private JFrame budgetFrame;
    private static final String BUDGET_JSON = "src/main/data/current_budget.json";

    public static synchronized void showBudgetPlanner() {
        if (instance == null || instance.frame == null || !instance.frame.isDisplayable()) {
            SwingUtilities.invokeLater(() -> {
                instance = new FullBudgetPlannerApp();
                instance.createAndShow();
            });
        } else {
            instance.frame.toFront();
        }
    }

    public static void main(String[] args) {
        showBudgetPlanner();
    }

    private void createAndShow() {
        frame = new JFrame("AI Personalized Budget Planning");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout(5, 5));

        frame.add(createInputPanel(), BorderLayout.WEST);

        centerPanel = new JPanel(new BorderLayout());
        frame.add(centerPanel, BorderLayout.CENTER);

        suggestionArea = new JTextArea(3, 50);
        suggestionArea.setLineWrap(true);
        suggestionArea.setBorder(BorderFactory.createTitledBorder("AI Suggestion"));
        frame.add(suggestionArea, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder("User Information"));

        JTextField occ = new JTextField();
        JTextField inc = new JTextField();
        JTextField city = new JTextField();
        JTextField eld = new JTextField("0");
        JTextField chi = new JTextField("0");
        JCheckBox chPartner = new JCheckBox();
        JCheckBox chPets = new JCheckBox();

        p.add(new JLabel("Occupation:"));                 p.add(occ);
        p.add(new JLabel("Disposable Income:"));          p.add(inc);
        p.add(new JLabel("City:"));                       p.add(city);
        p.add(new JLabel("Number of Elderly to Support:")); p.add(eld);
        p.add(new JLabel("Number of Children to Support:")); p.add(chi);
        p.add(new JLabel("Have a Partner:"));             p.add(chPartner);
        p.add(new JLabel("Have Pets:"));                  p.add(chPets);

        JButton genBtn = new JButton("Generate Budget");
        genBtn.addActionListener((ActionEvent e) -> onGenerate(
                occ.getText(), inc.getText(), city.getText(),
                eld.getText(), chi.getText(),
                chPartner.isSelected(), chPets.isSelected()
        ));
        p.add(genBtn);

        JButton viewBtn = new JButton("Monthly Budget");
        viewBtn.addActionListener(e -> openOrRefreshBudgetFrame());
        p.add(viewBtn);

        return p;
    }

    private void onGenerate(
            String occ, String inc, String city,
            String eld, String chi,
            boolean ptn, boolean pts) {

        // 1) 解析可支配收入
        double disposableIncome;
        try {
            disposableIncome = Double.parseDouble(inc);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number for Disposable Income.");
            return;
        }

        // 2) 构造 UserInfo
        UserInfo user = new UserInfo();
        user.setOccupation(occ);
        user.setDisposableIncome(disposableIncome);
        user.setCity(city);
        user.setNumElderlyToSupport(Integer.parseInt(eld));
        user.setNumChildrenToSupport(Integer.parseInt(chi));
        user.setHasPartner(ptn);
        user.setHasPets(pts);

        // 3) AI 生成预算 + 本月实际支出
        Map<String, Double> aiMap     = BudgetAIService.generateBudget(user);
        Map<String, Double> actualMap = BudgetAIService.getActualSpendingFromJson(
                "src/main/data/transactionInformation.json"
        );

        // 4) 保存预算到 JSON
        saveBudgetToJson(disposableIncome, aiMap);

        // 5) 渲染表格+图表
        displayTableAndChart(aiMap, actualMap);

        // 6) 调用 AI 生成文字建议
        String advice = BudgetAIService.generateSuggestion(user, actualMap);
        suggestionArea.setText(advice);
    }

    private void saveBudgetToJson(double income, Map<String, Double> budgets) {
        try (Writer writer = new FileWriter(BUDGET_JSON)) {
            new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(Map.of(
                            "disposableIncome", income,
                            "budgets", budgets
                    ), writer);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save budget JSON.");
        }
    }

    private void displayTableAndChart(
            Map<String, Double> budgets,
            Map<String, Double> actuals) {

        centerPanel.removeAll();

        List<SpendingRecord> recs = SpendingRecord.createFromMaps(actuals, budgets);
        SpendingTablePanel tablePanel = new SpendingTablePanel(recs);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SpendingRecord r : recs) {
            dataset.addValue(r.getActualSpending(), "Actual", r.getCategory());
            dataset.addValue(r.getAiBudget(),      "AI",     r.getCategory());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Actual vs AI Budget", "Category", "Amount (¥)", dataset
        );
        ChartPanel chartP = new ChartPanel(chart);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, tablePanel, chartP
        );
        split.setDividerLocation(200);
        centerPanel.add(split, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void openOrRefreshBudgetFrame() {
        if (budgetFrame == null) {
            budgetFrame = new JFrame("Monthly Budget");
            budgetFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else {
            budgetFrame.getContentPane().removeAll();
        }

        CurrentMonthBudgetPanel panel =
                new CurrentMonthBudgetPanel(BUDGET_JSON, this::refreshMainDisplay);
        budgetFrame.getContentPane().add(panel);
        budgetFrame.pack();
        budgetFrame.setLocationRelativeTo(frame);
        budgetFrame.setVisible(true);
    }

    private void refreshMainDisplay() {
        try (Reader reader = new FileReader(BUDGET_JSON)) {
            BudgetData data = new Gson().fromJson(reader, BudgetData.class);
            displayTableAndChart(
                    data.budgets,
                    BudgetAIService.getActualSpendingFromJson("src/main/data/transactionInformation.json")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class BudgetData {
        double disposableIncome;
        Map<String, Double> budgets;
    }
}
