package aiBudgetPlanner;

import javax.swing.*;
import java.util.*;

public class MainWindow {
    public static void main(String[] args) {
        // 模拟一个实际消费数据
        Map<String, Double> actualData = new HashMap<>();
        actualData.put("Food", 1500.0);
        actualData.put("Housing/Rent", 3000.0);
        actualData.put("Entertainment", 800.0);
        // 生成记录
        List<SpendingRecord> spendingList = SpendingListGenerator.buildSpendingList(actualData);

        // 显示在窗口
        JFrame frame = new JFrame("Spending Table Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        SpendingTablePanel tablePanel = new SpendingTablePanel(spendingList);
        frame.add(tablePanel);
        frame.setVisible(true);
    }
}
