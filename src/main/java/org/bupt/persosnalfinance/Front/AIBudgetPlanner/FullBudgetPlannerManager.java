/**
 * Manager class to decouple UI launch logic from the application entry point.
 * Provides a simple facade for showing the Full Budget Planner UI.
 */
package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

/**
 * Utility class that delegates the display of the budget planner UI.
 * Abstracts away the direct dependency on FullBudgetPlannerApp.
 */
public class FullBudgetPlannerManager {

    /**
     * Shows the budget planner by invoking the main UI class.
     * This method can be used as a single entry point for launching the planner.
     */
    public static void showBudgetPlanner() {
        FullBudgetPlannerApp.showBudgetPlanner();
    }
}
