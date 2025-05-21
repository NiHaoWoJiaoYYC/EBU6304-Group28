package aiBudgetPlanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

public class FullBudgetPlannerApp {
    private JFrame frame;
    private JTextArea suggestionArea;
    private JPanel centerPanel; // 表格/图表 切换容器

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FullBudgetPlannerApp().createAndShow());
    }

    private void createAndShow() {
        frame = new JFrame("AI Personalized Budget Planning");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout(5,5));

        // 左侧：用户输入 + 按钮
        frame.add(createInputPanel(), BorderLayout.WEST);

        // 中部：表格 + 图表 + 建议
        centerPanel = new JPanel(new BorderLayout());
        frame.add(centerPanel, BorderLayout.CENTER);

        suggestionArea = new JTextArea(3, 50);
        suggestionArea.setLineWrap(true);
        suggestionArea.setBorder(BorderFactory.createTitledBorder("AI Suggestion"));
        frame.add(suggestionArea, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder("User Information"));
        p.setLayout(new GridLayout(0,2,5,5));

        JTextField occ = new JTextField();
        JTextField inc = new JTextField();
        JTextField city = new JTextField();
        JTextField eld = new JTextField("0");
        JTextField chi = new JTextField("0");
        JCheckBox chPartner = new JCheckBox();
        JCheckBox chPets = new JCheckBox();

        p.add(new JLabel("Occupation:")); p.add(occ);
        p.add(new JLabel("Disposable Income:")); p.add(inc);
        p.add(new JLabel("City:")); p.add(city);
        p.add(new JLabel("Number of Elderly to Support:")); p.add(eld);
        p.add(new JLabel("Number of Children to Support:")); p.add(chi);
        p.add(new JLabel("Have a Partner:")); p.add(chPartner);
        p.add(new JLabel("Have Pets:")); p.add(chPets);

        JButton btn = new JButton("Generate Budget");
        btn.addActionListener((ActionEvent e) -> onGenerate(
                occ.getText(),
                inc.getText(),
                city.getText(),
                eld.getText(),
                chi.getText(),
                chPartner.isSelected(),
                chPets.isSelected()
        ));
        p.add(btn);
        return p;
    }

    private void onGenerate(String occ, String inc, String city, String eld, String chi, boolean ptn, boolean pts) {
        // 1. 构造 UserInfo
        UserInfo user = new UserInfo();
        user.setOccupation(occ);
        user.setDisposableIncome(Double.parseDouble(inc));
        user.setCity(city);
        user.setNumElderlyToSupport(Integer.parseInt(eld));
        user.setNumChildrenToSupport(Integer.parseInt(chi));
        user.setHasPartner(ptn);
        user.setHasPets(pts);

        // 2. AI 预算 + 本月实际支出
        Map<String, Double> aiMap = BudgetAIService.generateBudget(user);
        Map<String, Double> actualMap = BudgetAIService.getActualSpendingFromJson(
                "src/main/data/transactionInformation.json"
        );

        // 3. 表格面板
        List<SpendingRecord> recs = SpendingRecord.createFromMaps(actualMap, aiMap);
        SpendingTablePanel tablePanel = new SpendingTablePanel(recs);

        // 4. 柱状图面板
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(SpendingRecord r : recs) {
            dataset.addValue(r.getActualSpending(), "Actual", r.getCategory());
            dataset.addValue(r.getAiBudget(), "AI", r.getCategory());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Actual vs AI Budget",
                "Category",
                "Amount (¥)",
                dataset
        );
        ChartPanel chartP = new ChartPanel(chart);

        // 切换到 表格+图表
        centerPanel.removeAll();
        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tablePanel,
                chartP
        );
        split.setDividerLocation(200);
        centerPanel.add(split, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();

        // 5. 整体节省建议
        double totalActual = actualMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalAIBud  = aiMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double diff = totalAIBud - totalActual;
        suggestionArea.setText(String.format("本月预算结余 ¥%.2f，可%s！",
                diff,
                diff>=0 ? "考虑储蓄或投资" : "注意控制开支"
        ));
    }
}
