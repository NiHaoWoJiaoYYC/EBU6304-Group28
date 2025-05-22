package org.bupt.persosnalfinance.Front.Localization;

import org.bupt.persosnalfinance.dto.PlanDTO;
import org.bupt.persosnalfinance.Back.Controller.LocalizationController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PlanPanel 支持按 Holiday ID 分组显示和添加预算记录，切换 Holiday 时具有记忆性。
 * 使用 LocalizationController 统一调度业务逻辑。
 */
public class PlanPanel extends JPanel {
    private LocalizationController controller;
    private final DefaultTableModel model;
    private final JTable table;
    private final DefaultPieDataset<String> dataset;
    
    // 当前所属的节假日ID
    private Integer currentHolidayId;
    
    // 添加记录的输入组件
    private final JSpinner dateSpinner;
    private final JComboBox<String> categoryCombo;
    private final JComboBox<String> paymentCombo;
    private final JFormattedTextField amountField;
    private final JButton addButton;
    private final JButton deleteButton;

    public PlanPanel() {
        super(new BorderLayout(5,5));
        setBorder(BorderFactory.createTitledBorder("Spending Plan"));

        // 顶部：输入区域
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        top.add(new JLabel("Date:"));
        top.add(dateSpinner);

        categoryCombo = new JComboBox<>(new String[]{"Food","Housing","Daily","Transport","Entertainment","Shopping","Health","Education","Others"});
        top.add(new JLabel("Category:"));
        top.add(categoryCombo);

        paymentCombo = new JComboBox<>(new String[]{"WX","Alipay","CC","Cash","Others"});
        top.add(new JLabel("Payment:"));
        top.add(paymentCombo);

        amountField = new JFormattedTextField(NumberFormat.getNumberInstance());
        amountField.setColumns(8);
        top.add(new JLabel("Amount:"));
        top.add(amountField);

        addButton = new JButton("Add");
        addButton.addActionListener(e -> onAdd());
        top.add(addButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> onDelete());
        top.add(deleteButton);

        add(top, BorderLayout.NORTH);

        // 中部：表格 + 饼图
        model = new DefaultTableModel(new String[]{"Date","Category","Payment","Amount"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        dataset = new DefaultPieDataset<>();
        JFreeChart chart = ChartFactory.createPieChart("By Category", dataset, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, chartPanel);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);
    }

    /**
     * 注入 Controller 并初始化数据
     */
    public void setController(LocalizationController controller) {
        this.controller = controller;
        // 加载并展示当前 holidayId 对应的数据
        if (currentHolidayId != null) {
            refresh();
        }
    }

    /**
     * 设置当前节假日ID并刷新显示（保留该节假日上次的输入记录）。
     */
    public void setHolidayId(Integer holidayId) {
        this.currentHolidayId = holidayId;
        refresh();
    }

    /**
     * 添加记录：会关联 currentHolidayId，一旦切换回该节假日可见。
     */
    private void onAdd() {
        if (controller == null || currentHolidayId == null) {
            JOptionPane.showMessageDialog(this, "Please select a holiday first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Date d = (Date) dateSpinner.getValue();
            LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String cat = (String) categoryCombo.getSelectedItem();
            String pay = (String) paymentCombo.getSelectedItem();
            Number num = (Number) amountField.getValue();
            if (num == null || num.doubleValue() <= 0) {
                throw new IllegalArgumentException("Amount must be > 0");
            }
            double amt = num.doubleValue();
            controller.addPlan(currentHolidayId, ld, cat, pay, amt);
            clearInputs();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除选中行记录。
     */
    private void onDelete() {
        if (controller == null || currentHolidayId == null) {
            JOptionPane.showMessageDialog(this, "Please select a holiday first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 获取当前节假日的计划列表
        List<PlanDTO> list = controller.getPlansForHoliday(currentHolidayId);
        // 按选中逆序删除
        for (int i = rows.length - 1; i >= 0; i--) {
            controller.removePlan(list.get(rows[i]).getId());
        }
        refresh();
    }

    /**
     * 刷新当前节假日的预算列表与饼图。
     */
    private void refresh() {
        model.setRowCount(0);
        Map<String, Double> sums = new HashMap<>();
        if (controller != null && currentHolidayId != null) {
            for (PlanDTO p : controller.getPlansForHoliday(currentHolidayId)) {
                model.addRow(new Object[]{p.getDate(), p.getCategory(), p.getPaymentMethod(), p.getAmount()});
                sums.merge(p.getCategory(), p.getAmount(), Double::sum);
            }
        }
        dataset.clear();
        if (sums.isEmpty()) {
            dataset.setValue("No Data", 1.0);
        } else {
            sums.forEach(dataset::setValue);
        }
    }

    private void clearInputs() {
        dateSpinner.setValue(new Date());
        categoryCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        amountField.setValue(null);
    }
}


