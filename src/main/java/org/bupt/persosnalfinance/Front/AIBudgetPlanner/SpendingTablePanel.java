/**
 * JPanel that displays a table of spending records, comparing actual spending
 * versus AI-generated budget for each category.
 */
package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

import org.bupt.persosnalfinance.dto.SpendingRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A Swing panel combining a JTable within a JScrollPane to show
 * a list of SpendingRecord entries.
 * The table has three columns: category name, actual spending, and AI budget.
 */
public class SpendingTablePanel extends JPanel {
    /** The JTable used to display spending records. */
    private JTable table;

    /**
     * Constructs a SpendingTablePanel using the provided list of records.
     * Populates the table model with category, actual spending, and AI budget values.
     *
     * @param records list of SpendingRecord objects to display
     */
    public SpendingTablePanel(List<SpendingRecord> records) {
        // Use BorderLayout to center the table scroll pane
        setLayout(new BorderLayout());

        // Define table column headers
        String[] cols = {"Category", "Actual Spending (¥)", "AI Budget (¥)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        // Populate model rows from records
        for (SpendingRecord r : records) {
            model.addRow(new Object[]{
                    r.getCategory(),
                    r.getActualSpending(),
                    r.getAiBudget()
            });
        }

        // Create JTable with the model and set row height
        table = new JTable(model);
        table.setRowHeight(24);

        // Add table inside a scroll pane to the panel center
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}

