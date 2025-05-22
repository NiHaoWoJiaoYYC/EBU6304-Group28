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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class FullBudgetPlannerApp {
    private static FullBudgetPlannerApp instance;
    private JFrame frame;
    private JTextArea suggestionArea;
    private JPanel centerPanel;
    private JFrame budgetFrame;  // 月预算窗口
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
        frame.setLayout(new BorderLayout(5,5));

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
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.setBorder(BorderFactory.createTitledBorder("User Information"));

        JTextField occ = new JTextField();
        JTextField inc = new JTextField();
        JTextField city = new JTextField();
        JTextField eld = new JTextField("0");
        JTextField chi = new JTextField("0");
        JCheckBox chPartner = new JCheckBox();
        JCheckBox chPets = new JCheckBox();

        p.add(new JLabel("Occupation:"));             p.add(occ);
        p.add(new JLabel("Disposable Income:"));     p.add(inc);
        p.add(new JLabel("City:"));                  p.add(city);
        p.add(new JLabel("Number of Elderly to Support:")); p.add(eld);
        p.add(new JLabel("Number of Children to Support:")); p.add(chi);
        p.add(new JLabel("Have a Partner:"));        p.add(chPartner);
        p.add(new JLabel("Have Pets:"));             p.add(chPets);

        JButton genBtn = new JButton("Generate Budget");
        genBtn.addActionListener((ActionEvent e) -> onGenerate(
                occ.getText(), inc.getText(), city.getText(),
                eld.getText(), chi.getText(),
                chPartner.isSelected(), chPets.isSelected()
        ));
        p.add(genBtn);

        JButton viewBtn = new JButton("本月预算");
        viewBtn.addActionListener(e -> openOrRefreshBudgetFrame());
        p.add(viewBtn);

        return p;
    }

    private void onGenerate(String occ, String inc, String city,
                            String eld, String chi,
                            boolean ptn, boolean pts) {
        // 构造用户信息
        UserInfo user = new UserInfo();
        user.setOccupation(occ);
        user.setDisposableIncome(Double.parseDouble(inc));
        user.setCity(city);
        user.setNumElderlyToSupport(Integer.parseInt(eld));
        user.setNumChildrenToSupport(Integer.parseInt(chi));
        user.setHasPartner(ptn);
        user.setHasPets(pts);

        // AI 预算 + 实际支出
        Map<String, Double> aiMap = BudgetAIService.generateBudget(user);
        Map<String, Double> actualMap =
                BudgetAIService.getActualSpendingFromJson("src/main/data/transactionInformation.json");

        // 保存 JSON
        saveBudgetToJson(user.getDisposableIncome(), aiMap);

        // 主界面表格+图表展示
        displayTableAndChart(aiMap, actualMap);

        // 建议
        double totalAct = actualMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalAI  = aiMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double diff = totalAI - totalAct;
        suggestionArea.setText(String.format(
                "本月预算结余 ¥%.2f，可%s！",
                diff, diff >= 0 ? "考虑储蓄或投资" : "注意控制开支"
        ));
    }

    private void saveBudgetToJson(double income, Map<String, Double> budgets) {
        try (Writer writer = new FileWriter(BUDGET_JSON)) {
            new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(Map.of(
                            "disposableIncome", income,
                            "budgets",           budgets
                    ), writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayTableAndChart(Map<String, Double> budgets,
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

    /** 打开或刷新独立的“本月预算”窗口 **/
    private void openOrRefreshBudgetFrame() {
        if (budgetFrame == null) {
            budgetFrame = new JFrame("本月预算");
            budgetFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else {
            budgetFrame.getContentPane().removeAll();
        }
        // 每次都重新创建面板并传入刷新回调
        CurrentMonthBudgetPanel panel =
                new CurrentMonthBudgetPanel(BUDGET_JSON, this::refreshMainDisplay);
        budgetFrame.getContentPane().add(panel);
        budgetFrame.pack();
        budgetFrame.setLocationRelativeTo(frame);
        budgetFrame.setVisible(true);
    }

    /** 月预算保存后回调，刷新主界面内容 **/
    private void refreshMainDisplay() {
        try (Reader reader = new FileReader(BUDGET_JSON)) {
            BudgetData data = new Gson().fromJson(reader, BudgetData.class);
            Map<String, Double> budgets = data.budgets;
            Map<String, Double> actuals = BudgetAIService.getActualSpendingFromJson(
                    "src/main/data/transactionInformation.json"
            );
            displayTableAndChart(budgets, actuals);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 内部类，用于解析 JSON **/
    private static class BudgetData {
        double disposableIncome;
        Map<String, Double> budgets;
    }
}
