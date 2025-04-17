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
     * 接收传入的 Map（实际消费），构建完整的 SpendingRecord 清单
     */
    public static List<SpendingRecord> buildSpendingList(Map<String, Double> actualData) {
        List<SpendingRecord> list = new ArrayList<>();
        for (String category : categories) {
            double value = actualData.getOrDefault(category, 0.0);
            list.add(new SpendingRecord(category, value));
        }
        return list;
    }

    // 可选：提供静态获取所有类别方法
    public static String[] getDefaultCategories() {
        return categories;
    }
}
