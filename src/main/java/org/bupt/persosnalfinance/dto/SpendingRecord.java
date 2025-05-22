package org.bupt.persosnalfinance.dto;

import java.util.*;

public class SpendingRecord {
    private final String category;
    private final double actualSpending;
    private final double aiBudget;

    public SpendingRecord(String category, double actualSpending, double aiBudget) {
        this.category = category;
        this.actualSpending = actualSpending;
        this.aiBudget = aiBudget;
    }

    public String getCategory() { return category; }
    public double getActualSpending() { return actualSpending; }
    public double getAiBudget() { return aiBudget; }

    public static List<SpendingRecord> createFromMaps(Map<String, Double> actual, Map<String, Double> ai) {
        List<SpendingRecord> list = new ArrayList<>();
        for (String cat : ai.keySet()) {
            double a = actual.getOrDefault(cat, 0.0);
            double b = ai.get(cat);
            list.add(new SpendingRecord(cat, a, b));
        }
        return list;
    }
}
