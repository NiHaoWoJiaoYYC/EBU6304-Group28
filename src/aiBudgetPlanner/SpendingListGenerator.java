package aiBudgetPlanner;

import java.util.*;

public class SpendingListGenerator {

    // 固定预算类别
    private static final String[] categories = {
            "Food", "Housing/Rent", "Daily Necessities", "Transportation",
            "Entertainment", "Shopping", "Healthcare", "Education",
            "Childcare", "Gifts", "Savings", "Others"
    };

    /**
     * 接收两个 Map（实际消费 + AI预算），构建完整的 SpendingRecord 清单
     */
    public static List<SpendingRecord> buildSpendingList(Map<String, Double> actualData, Map<String, Double> aiBudget) {
        List<SpendingRecord> list = new ArrayList<>();
        for (String category : categories) {
            double actual = actualData.getOrDefault(category, 0.0);
            double budget = aiBudget.getOrDefault(category, 0.0);
            list.add(new SpendingRecord(category, actual, budget));
        }
        return list;
    }

    // 提供静态获取所有类别方法
    public static String[] getDefaultCategories() {
        return categories;
    }
}
