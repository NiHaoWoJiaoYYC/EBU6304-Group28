/**
 * DTO representing a single budget category's spending record,
 * including the actual spending and the AI-generated budget for that category.
 */
package org.bupt.persosnalfinance.dto;

import java.util.*;

/**
 * A record of spending for a specific budget category.
 * Contains both the user's actual spending and the AI-provided budget.
 */
public class SpendingRecord {
    /** Budget category name (e.g., "Food", "Transportation"). */
    private final String category;
    /** The actual amount spent by the user in this category. */
    private final double actualSpending;
    /** The budget allocated by the AI for this category. */
    private final double aiBudget;

    /**
     * Constructs a SpendingRecord with the specified values.
     *
     * @param category        the name of the budget category
     * @param actualSpending  the actual spending amount in this category
     * @param aiBudget        the budget amount recommended by AI
     */
    public SpendingRecord(String category, double actualSpending, double aiBudget) {
        this.category = category;
        this.actualSpending = actualSpending;
        this.aiBudget = aiBudget;
    }

    /**
     * Gets the budget category name.
     *
     * @return the category name
     */
    public String getCategory() { return category; }

    /**
     * Gets the actual spending for this category.
     *
     * @return the actual spending amount
     */
    public double getActualSpending() { return actualSpending; }

    /**
     * Gets the AI-generated budget for this category.
     *
     * @return the budget amount recommended by AI
     */
    public double getAiBudget() { return aiBudget; }

    /**
     * Creates a list of SpendingRecord objects by merging two maps:
     * one for actual spending and one for AI budgets. For each category
     * in the AI map, retrieves the actual spending (defaulting to 0.0)
     * and the AI budget, then constructs a record.
     *
     * @param actual a map from category name to actual spending amount
     * @param ai     a map from category name to AI-generated budget amount
     * @return a list of SpendingRecord for each category in the AI map
     */
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
