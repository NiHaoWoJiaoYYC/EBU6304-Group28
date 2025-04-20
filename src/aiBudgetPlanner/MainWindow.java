package aiBudgetPlanner;

import javax.swing.*;
import java.util.*;
import static aiBudgetPlanner.CategoryConstants.ALL_CATEGORIES;

public class MainWindow {
    public static void main(String[] args) {
        // 模拟一个实际消费数据
        Map<String, Double> actualData = new HashMap<>();
        actualData.put("Food", 1500.0);
        actualData.put("Housing/Rent", 3000.0);
        actualData.put("Entertainment", 800.0);

        // 模拟用户信息（已去掉 familySituation）
        UserInfo user = new UserInfo(
                "Student", 4000.0, "Beijing",
                1, 0, true, false
        );

        // 调用 AI 接口生成预算数据
        Map<String, Double> budgetData = BudgetAIService.generateBudget(user);

        // 构造记录列表（将 AI 预算数据和实际数据一并放入表格）
        List<SpendingRecord> spendingList = new ArrayList<>();
        for (String category : ALL_CATEGORIES) {
            double actual = actualData.getOrDefault(category, 0.0);
            double budget = budgetData.getOrDefault(category, 0.0);
            spendingList.add(new SpendingRecord(category, actual, budget));
        }

        // 显示在窗口
        JFrame frame = new JFrame("Spending Table Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        SpendingTablePanel tablePanel = new SpendingTablePanel(spendingList);
        frame.add(tablePanel);
        frame.setVisible(true);
    }
}
