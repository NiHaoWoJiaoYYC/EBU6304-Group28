

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BudgetPlannerUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetPlannerUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("AI Personalized Budget Planning");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // 模拟用户信息输入（你可以改成表单）
        UserInfo user = new UserInfo(
                "student",
                5000,
                "Beijing",
                0,
                0,
                false,
                false
        );

        // AI预算数据
        Map<String, Double> aiBudget = BudgetAIService.generateBudget(user);

        // 实际消费数据
        Map<String, Double> actualSpending = BudgetAIService.getActualSpendingFromJson("src/main/data/transactionInformation.json");

        // 构造表格数据
        List<SpendingRecord> records = SpendingRecord.createFromMaps(actualSpending, aiBudget);

        // 显示表格
        SpendingTablePanel tablePanel = new SpendingTablePanel(records);
        frame.add(tablePanel);

        frame.setVisible(true);
    }
}
