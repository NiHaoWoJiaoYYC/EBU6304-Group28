package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

import org.bupt.persosnalfinance.dto.UserInfo;
import org.bupt.persosnalfinance.Back.Service.BudgetAIService;
import org.bupt.persosnalfinance.dto.SpendingRecord;

import javax.swing.*;
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

        // 模拟用户信息输入
        UserInfo user = new UserInfo("student", 5000, "Beijing", 0, 0, false, false);

        Map<String, Double> aiBudget = BudgetAIService.generateBudget(user);
        Map<String, Double> actualSpending =
                BudgetAIService.getActualSpendingFromJson("src/main/data/transactionInformation.json");

        List<SpendingRecord> records =
                SpendingRecord.createFromMaps(actualSpending, aiBudget);

        SpendingTablePanel tablePanel = new SpendingTablePanel(records);
        frame.add(tablePanel);

        frame.setVisible(true);
    }
}
