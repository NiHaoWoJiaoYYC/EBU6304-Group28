package org.bupt.persosnalfinance.Front;

import org.bupt.persosnalfinance.dto.SpendingRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SpendingTablePanel extends JPanel {
    private JTable table;

    public SpendingTablePanel(List<SpendingRecord> records) {
        setLayout(new BorderLayout());
        String[] cols = {"Category", "Actual Spending (¥)", "AI Budget (¥)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (SpendingRecord r : records) {
            model.addRow(new Object[]{
                    r.getCategory(),
                    r.getActualSpending(),
                    r.getAiBudget()
            });
        }

        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
