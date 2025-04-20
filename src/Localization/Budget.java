package Localization;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Budget.java
 * 支出面板：用户可按【日期、类别、支付方式、金额】添加记录，展示在表格中，并实时生成按类别汇总的饼图。
 * 如果无数据，则显示“No Data”占位。
 */
public class Budget extends JFrame {
    private static final String[] DEFAULT_CATEGORIES = {
        "Food", "Housing/Rent", "Daily Necessities", "Transportation",
        "Entertainment", "Shopping", "Healthcare", "Education",
        "Childcare", "Gifts", "Savings", "Others"
    };
    private static final String[] PAYMENT_METHODS = {"WX", "Alipay", "CC", "Cash", "Others"};

    private JSpinner dateSpinner;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> paymentCombo;
    private JTextField amountField;

    private DefaultPieDataset dataset;
    private ChartPanel chartPanel;

    private JTable recordTable;
    private DefaultTableModel recordModel;

    public Budget() {
        super("Budget");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5,5));
        initUI();
        pack();
        setLocationRelativeTo(null);
        // 确保分割位置
        SwingUtilities.invokeLater(() -> {
            JSplitPane split = (JSplitPane) getContentPane().getComponent(1);
            split.setDividerLocation(0.7);
        });
        setVisible(true);
    }

    private void initUI() {
        // 顶部输入面板
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy/MM/dd"));
        topPanel.add(dateSpinner);

        topPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(DEFAULT_CATEGORIES);
        topPanel.add(categoryCombo);

        topPanel.add(new JLabel("Payment:"));
        paymentCombo = new JComboBox<>(PAYMENT_METHODS);
        topPanel.add(paymentCombo);

        topPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(8);
        topPanel.add(amountField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> onAdd());
        topPanel.add(addBtn);

        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(e -> onEdit());
        topPanel.add(editBtn);

        JButton delBtn = new JButton("Delete");
        delBtn.addActionListener(e -> onDelete());
        topPanel.add(delBtn);

        add(topPanel, BorderLayout.NORTH);

        // 饼图初始化
        dataset = new DefaultPieDataset();
        // 初始显示 No Data
        dataset.setValue("No Data", 1);
        JFreeChart chart = ChartFactory.createPieChart("Spending Distribution", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        // 记录表格
        String[] cols = {"Date", "Category", "Payment", "Expenditure"};
        recordModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        recordTable = new JTable(recordModel);
        JScrollPane tableScroll = new JScrollPane(recordTable);
        tableScroll.setPreferredSize(new Dimension(400, 150));

        // 中央分割
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, tableScroll);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);
    }

    private void onAdd() {
        try {
            Date d = (Date) dateSpinner.getValue();
            String dateStr = new java.text.SimpleDateFormat("yyyy/MM/dd").format(d);
            String cat = (String) categoryCombo.getSelectedItem();
            String pay = (String) paymentCombo.getSelectedItem();
            double amt = Double.parseDouble(amountField.getText().trim());
            recordModel.addRow(new Object[]{dateStr, cat, pay, amt});
            amountField.setText("");
            refreshDataset();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入合法数字", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        int idx = recordTable.getSelectedRow();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "请选择一行进行编辑"); return; }
        String date = (String) recordModel.getValueAt(idx, 0);
        String cat = (String) recordModel.getValueAt(idx, 1);
        String pay = (String) recordModel.getValueAt(idx, 2);
        Object val = recordModel.getValueAt(idx, 3);
        String input = JOptionPane.showInputDialog(this, String.format("Edit record (%s, %s, %s):", date, cat, pay), val);
        if (input != null) {
            try {
                double amt = Double.parseDouble(input.trim());
                recordModel.setValueAt(amt, idx, 3);
                refreshDataset();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入合法数字");
            }
        }
    }

    private void onDelete() {
        int idx = recordTable.getSelectedRow();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "请选择一行删除"); return; }
        recordModel.removeRow(idx);
        refreshDataset();
    }

    private void refreshDataset() {
        dataset.clear();
        int rows = recordModel.getRowCount();
        if (rows == 0) {
            dataset.setValue("No Data", 1);
            return;
        }
        java.util.Map<String, Double> sumMap = new java.util.HashMap<>();
        for (int i = 0; i < rows; i++) {
            String cat = (String) recordModel.getValueAt(i, 1);
            double amt = ((Number) recordModel.getValueAt(i, 3)).doubleValue();
            sumMap.put(cat, sumMap.getOrDefault(cat, 0.0) + amt);
        }
        for (java.util.Map.Entry<String, Double> e : sumMap.entrySet()) {
            dataset.setValue(e.getKey(), e.getValue());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Budget::new);
    }
}
