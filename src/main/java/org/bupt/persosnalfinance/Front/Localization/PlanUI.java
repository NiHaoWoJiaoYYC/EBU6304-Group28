package org.bupt.persosnalfinance.Front.Localization;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


import org.bupt.persosnalfinance.Back.Service.PlanService;
import org.bupt.persosnalfinance.Back.Service.ChartService;
import org.bupt.persosnalfinance.dto.PlanDTO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class PlanUI extends JFrame {
    private final PlanService planService = new PlanService();
    private final ChartService chartService = new ChartService();

    private JTable table;
    private DefaultTableModel tableModel;
    private DefaultPieDataset<String> dataset;

    private JSpinner dateSpinner;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> paymentCombo;
    private JFormattedTextField amountField;
    private JButton addButton;  // “Add” 按钮

    public PlanUI() {
        super("Plan Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5,5));
        initUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        // 顶部输入栏
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        top.add(dateSpinner);

        top.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"Food","Housing","Daily","Transport","Entertainment","Shopping","Health","Education","Others"});
        top.add(categoryCombo);

        top.add(new JLabel("Payment:"));
        paymentCombo = new JComboBox<>(new String[]{"WX","Alipay","CC","Cash","Others"});
        top.add(paymentCombo);

        top.add(new JLabel("Amount:"));
        amountField = new JFormattedTextField(java.text.NumberFormat.getNumberInstance());
        amountField.setColumns(8);
        top.add(amountField);

        addButton = new JButton("Add");
        addButton.addActionListener(e -> onAdd());
        top.add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> onDelete());
        top.add(deleteButton);

        add(top, BorderLayout.NORTH);

        // 表格和饼图
        tableModel = new DefaultTableModel(new String[]{"Date","Category","Payment","Amount"}, 0);
        table = new JTable(tableModel);
        JScrollPane tablePane = new JScrollPane(table);

        dataset = new DefaultPieDataset<>();
        JFreeChart chart = ChartFactory.createPieChart("Spending by Category", dataset, true, true, false);
        ChartPanel chartPane = new ChartPanel(chart);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePane, chartPane);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        refreshData();
    }

    private void onAdd() {
        try {
            Date d = (Date) dateSpinner.getValue();
            java.time.LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String cat = (String) categoryCombo.getSelectedItem();
            String pay = (String) paymentCombo.getSelectedItem();
            Number num = (Number) amountField.getValue();
            if (num == null || num.doubleValue() <= 0) {
                throw new IllegalArgumentException("Amount must be > 0");
            }
            double amt = num.doubleValue();

            // 新增记录
            planService.addRecord(new PlanDTO(ld, cat, pay, amt));
            clearInputs();
            refreshData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one row to delete.");
            return;
        }
        // 逆序删除
        for (int i = rows.length - 1; i >= 0; i--) {
            planService.removeRecord(rows[i]);
        }
        // 删除后保证按钮标签为 “Add”
        if (!"Add".equals(addButton.getText())) {
            addButton.setText("Add");
        }
        refreshData();
    }

    private void refreshData() {
        // 刷新表格
        tableModel.setRowCount(0);
        List<PlanDTO> list = planService.getAllRecords();
        Map<String, Double> sum = new HashMap<>();
        for (PlanDTO r : list) {
            tableModel.addRow(new Object[]{
                r.getDate(), r.getCategory(), r.getPaymentMethod(), r.getAmount()
            });
            sum.merge(r.getCategory(), r.getAmount(), Double::sum);
        }
        // 刷新饼图
        dataset.clear();
        if (sum.isEmpty()) {
            dataset.setValue("No Data", 1.0);
        } else {
            sum.forEach(dataset::setValue);
        }
    }

    private void clearInputs() {
        dateSpinner.setValue(new Date());
        categoryCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        amountField.setValue(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlanUI::new);
    }
}
