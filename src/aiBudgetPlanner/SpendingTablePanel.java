package aiBudgetPlanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SpendingTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public SpendingTablePanel(List<SpendingRecord> spendingList) {
        setLayout(new BorderLayout());

        String[] columnNames = {"Category", "Actual Spending (¥)"};
        model = new DefaultTableModel(columnNames, 0);

        // 添加记录到表格
        for (SpendingRecord record : spendingList) {
            Object[] row = {record.getCategory(), record.getActualSpending()};
            model.addRow(row);
        }

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 可选：更新方法
    public void updateTable(List<SpendingRecord> newList) {
        model.setRowCount(0); // 清空旧数据
        for (SpendingRecord r : newList) {
            model.addRow(new Object[]{r.getCategory(), r.getActualSpending()});
        }
    }
}
