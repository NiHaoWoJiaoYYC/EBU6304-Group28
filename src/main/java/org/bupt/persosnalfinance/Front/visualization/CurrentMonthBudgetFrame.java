// 文件路径：src/main/java/org/bupt/persosnalfinance/Front/visualization/CurrentMonthBudgetFrame.java
package org.bupt.persosnalfinance.Front.visualization;

import javax.swing.*;

public class CurrentMonthBudgetFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("本月预算");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 这里给 CurrentMonthBudgetPanel 传入两个参数：JSON 路径 + 一个空的 onSave 回调
            CurrentMonthBudgetPanel panel = new CurrentMonthBudgetPanel(
                    "src/main/data/current_budget.json",
                    () -> { /* no-op */ }
            );

            frame.getContentPane().add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
