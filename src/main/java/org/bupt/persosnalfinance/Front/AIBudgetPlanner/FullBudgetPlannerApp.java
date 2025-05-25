/**
 * Main application for AI‐powered personalized budget planning.
 *
 * <p>Provides a Swing UI for users to input their profile, generate monthly budgets via an AI service,
 * visualize actual vs. AI budgets, and view or edit the saved monthly budget in a separate dialog.</p>
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>Run {@link #main(String[])}</li>
 *   <li>Input user data, click "Generate Budget" to fetch AI budgets and actual spendings</li>
 *   <li>Visualize in table and bar chart</li>
 *   <li>Click "Monthly Budget" to open an editable budget dialog</li>
 * </ul>
 */
package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bupt.persosnalfinance.Back.Service.BudgetAIService;
import org.bupt.persosnalfinance.dto.UserInfo;
import org.bupt.persosnalfinance.dto.SpendingRecord;
import org.bupt.persosnalfinance.Front.visualization.CurrentMonthBudgetPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class FullBudgetPlannerApp {
    /** Singleton instance of the planner. */
    private static FullBudgetPlannerApp instance;
    /** Main application frame. */
    private JFrame frame;
    /** Text area displaying AI suggestions. */
    private JTextArea suggestionArea;
    /** Panel where table and chart are displayed. */
    private JPanel centerPanel;
    /** Dialog frame for the "Monthly Budget" editor. */
    private JFrame budgetFrame;
    /** Path to the JSON file storing the current budget. */
    private static final String BUDGET_JSON = "src/main/data/current_budget.json";

    /**
     * Shows the budget planner, creating the UI if not already visible.
     * Ensures only one instance is displayed at a time.
     */
    public static synchronized void showBudgetPlanner() {
        if (instance == null || instance.frame == null || !instance.frame.isDisplayable()) {
            SwingUtilities.invokeLater(() -> {
                instance = new FullBudgetPlannerApp();
                instance.createAndShow();
            });
        } else {
            instance.frame.toFront();
        }
    }

    /**
     * Application entry point. Launches the budget planner UI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        showBudgetPlanner();
    }

    /**
     * Constructs and displays the main window with user input form,
     * a display area for table/chart, and an AI suggestion field.
     */
    private void createAndShow() {
        frame = new JFrame("AI Personalized Budget Planning");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout(5, 5));

        frame.add(createInputPanel(), BorderLayout.WEST);

        centerPanel = new JPanel(new BorderLayout());
        frame.add(centerPanel, BorderLayout.CENTER);

        suggestionArea = new JTextArea(3, 50);
        suggestionArea.setLineWrap(true);
        suggestionArea.setBorder(BorderFactory.createTitledBorder("AI Suggestion"));
        frame.add(suggestionArea, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Builds the left input panel for collecting user data and buttons.
     *
     * @return a JPanel containing input fields and action buttons
     */
    private JPanel createInputPanel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder("User Information"));

        JTextField occ = new JTextField();
        JTextField inc = new JTextField();
        JTextField city = new JTextField();
        JTextField eld = new JTextField("0");
        JTextField chi = new JTextField("0");
        JCheckBox chPartner = new JCheckBox();
        JCheckBox chPets = new JCheckBox();

        p.add(new JLabel("Occupation:"));                 p.add(occ);
        p.add(new JLabel("Disposable Income:"));          p.add(inc);
        p.add(new JLabel("City:"));                       p.add(city);
        p.add(new JLabel("Number of Elderly to Support:")); p.add(eld);
        p.add(new JLabel("Number of Children to Support:")); p.add(chi);
        p.add(new JLabel("Have a Partner:"));             p.add(chPartner);
        p.add(new JLabel("Have Pets:"));                  p.add(chPets);

        JButton genBtn = new JButton("Generate Budget");
        genBtn.addActionListener((ActionEvent e) -> onGenerate(
                occ.getText(), inc.getText(), city.getText(),
                eld.getText(), chi.getText(),
                chPartner.isSelected(), chPets.isSelected()
        ));
        p.add(genBtn);

        JButton viewBtn = new JButton("Monthly Budget");
        viewBtn.addActionListener(e -> openOrRefreshBudgetFrame());
        p.add(viewBtn);

        return p;
    }

    /**
     * Handles the "Generate Budget" action:
     * 1) Parses inputs and constructs a UserInfo object
     * 2) Calls AI service for budget recommendations and actual spending
     * 3) Saves the AI budget JSON
     * 4) Updates the main display (table + chart)
     * 5) Retrieves AI suggestion text and shows it
     *
     * @param occ   occupation string
     * @param inc   disposable income string
     * @param city  city string
     * @param eld   number of elderly to support string
     * @param chi   number of children to support string
     * @param ptn   hasPartner flag
     * @param pts   hasPets flag
     */
    private void onGenerate(
            String occ, String inc, String city,
            String eld, String chi,
            boolean ptn, boolean pts) {

        double disposableIncome;
        try {
            disposableIncome = Double.parseDouble(inc);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number for Disposable Income.");
            return;
        }

        UserInfo user = new UserInfo();
        user.setOccupation(occ);
        user.setDisposableIncome(disposableIncome);
        user.setCity(city);
        user.setNumElderlyToSupport(Integer.parseInt(eld));
        user.setNumChildrenToSupport(Integer.parseInt(chi));
        user.setHasPartner(ptn);
        user.setHasPets(pts);

        Map<String, Double> aiMap     = BudgetAIService.generateBudget(user);
        Map<String, Double> actualMap = BudgetAIService.getActualSpendingFromJson(
                "src/main/data/transactionInformation.json"
        );

        saveBudgetToJson(disposableIncome, aiMap);
        displayTableAndChart(aiMap, actualMap);

        String advice = BudgetAIService.generateSuggestion(user, actualMap);
        suggestionArea.setText(advice);
    }

    /**
     * Persists the disposable income and AI budget map to JSON.
     *
     * @param income  disposableIncome value to store
     * @param budgets map of category→budget to store
     */
    private void saveBudgetToJson(double income, Map<String, Double> budgets) {
        try (Writer writer = new FileWriter(BUDGET_JSON)) {
            new GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(Map.of(
                            "disposableIncome", income,
                            "budgets", budgets
                    ), writer);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save budget JSON.");
        }
    }

    /**
     * Renders the AI budget vs actual spending as a table and a bar chart.
     *
     * @param budgets map of category→AI budget amounts
     * @param actuals map of category→actual spending amounts
     */
    private void displayTableAndChart(
            Map<String, Double> budgets,
            Map<String, Double> actuals) {

        centerPanel.removeAll();

        List<SpendingRecord> recs = SpendingRecord.createFromMaps(actuals, budgets);
        SpendingTablePanel tablePanel = new SpendingTablePanel(recs);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SpendingRecord r : recs) {
            dataset.addValue(r.getActualSpending(), "Actual", r.getCategory());
            dataset.addValue(r.getAiBudget(),      "AI",     r.getCategory());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Actual vs AI Budget", "Category", "Amount (¥)", dataset
        );
        ChartPanel chartP = new ChartPanel(chart);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, tablePanel, chartP
        );
        split.setDividerLocation(200);
        centerPanel.add(split, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Opens or refreshes the "Monthly Budget" dialog, reloading its panel each time.
     */
    private void openOrRefreshBudgetFrame() {
        if (budgetFrame == null) {
            budgetFrame = new JFrame("Monthly Budget");
            budgetFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else {
            budgetFrame.getContentPane().removeAll();
        }

        CurrentMonthBudgetPanel panel =
                new CurrentMonthBudgetPanel(BUDGET_JSON, this::refreshMainDisplay);
        budgetFrame.getContentPane().add(panel);
        budgetFrame.pack();
        budgetFrame.setLocationRelativeTo(frame);
        budgetFrame.setVisible(true);
    }

    /**
     * Callback after saving in the budget dialog: reload JSON and refresh main display.
     */
    private void refreshMainDisplay() {
        try (Reader reader = new FileReader(BUDGET_JSON)) {
            BudgetData data = new Gson().fromJson(reader, BudgetData.class);
            displayTableAndChart(
                    data.budgets,
                    BudgetAIService.getActualSpendingFromJson("src/main/data/transactionInformation.json")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal data holder matching the saved budget JSON structure.
     */
    private static class BudgetData {
        double disposableIncome;
        Map<String, Double> budgets;
    }
}

