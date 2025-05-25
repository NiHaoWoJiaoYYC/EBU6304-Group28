/**
 * Entry point and simple UI for generating and displaying an AI-powered budget.
 * Uses BudgetAIService to produce budget recommendations and actual spending,
 * then maps them into SpendingRecord objects displayed in a table.
 */
package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

import org.bupt.persosnalfinance.dto.UserInfo;
import org.bupt.persosnalfinance.Back.Service.BudgetAIService;
import org.bupt.persosnalfinance.dto.SpendingRecord;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Simple Swing-based UI to display AI-generated budget versus actual spending.
 * Initializes a JFrame, simulates user input, invokes the AI service,
 * and renders results in a SpendingTablePanel.
 */
public class BudgetPlannerUI {

    /**
     * Application entry point. Launches the UI on the Swing Event Dispatch Thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetPlannerUI().createAndShowGUI());
    }

    /**
     * Constructs and displays the main application window.
     * Simulates user data, retrieves AI budget and actual spending,
     * converts to SpendingRecord list, and shows in a table panel.
     */
    private void createAndShowGUI() {
        // Set up main frame
        JFrame frame = new JFrame("AI Personalized Budget Planning");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Simulate user information input
        UserInfo user = new UserInfo(
                "student",   // occupation
                5000,         // disposable income
                "Beijing",   // city
                0,            // number of elderly to support
                0,            // number of children to support
                false,        // has partner
                false         // has pets
        );

        // Generate AI budget and fetch actual spending
        Map<String, Double> aiBudget = BudgetAIService.generateBudget(user);
        Map<String, Double> actualSpending =
                BudgetAIService.getActualSpendingFromJson(
                        "src/main/data/transactionInformation.json"
                );

        // Merge into records and display in table panel
        List<SpendingRecord> records =
                SpendingRecord.createFromMaps(actualSpending, aiBudget);

        SpendingTablePanel tablePanel = new SpendingTablePanel(records);
        frame.add(tablePanel);

        // Show the window
        frame.setVisible(true);
    }
}

