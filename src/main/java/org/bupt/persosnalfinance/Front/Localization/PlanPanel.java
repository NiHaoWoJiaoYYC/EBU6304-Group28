package org.bupt.persosnalfinance.Front.Localization;

import org.bupt.persosnalfinance.dto.PlanDTO;
import org.bupt.persosnalfinance.Back.Controller.LocalizationController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

/**
 * PlanPanel 支持按 Holiday ID 分组显示和添加预算记录，切换 Holiday 时具有记忆性。
 * 使用 LocalizationController 统一调度业务逻辑。
 */
public class PlanPanel extends JPanel {
    private LocalizationController controller;
    private Integer currentHolidayId;

    private final DefaultTableModel model;
    private final JTable table;
    private JLabel totalLabel;
    private DefaultPieDataset dataset = new DefaultPieDataset();

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

        // 中部：表格
    // 把原来只有 Date/Category/Payment/Amount 的列，改为第一列专门存放 ID
    model = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Payment", "Amount"}, 0);
    table = new JTable(model);
    // 隐藏 ID 列
    table.getColumnModel().getColumn(0).setMinWidth(0);
    table.getColumnModel().getColumn(0).setMaxWidth(0);
JScrollPane tableScroll = new JScrollPane(table);

        totalLabel = new JLabel("Total: 0.00", SwingConstants.LEFT);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        leftPanel.add(totalLabel, BorderLayout.SOUTH);

        // 饼图
        ChartPanel chartPanel = new ChartPanel(createPieChart());
        // 调整饼图面板首选尺寸，防止过大
        chartPanel.setPreferredSize(new Dimension(300, 250));

        // 水平拆分：左侧表格+Total，右侧饼图
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chartPanel);
        // 这里可以用比例或具体像素定位分割线
        splitPane.setResizeWeight(0.6);      // 左侧占 60%
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER); 
        
        // 第一次加载数据
        refresh();
    }


    /**
     * 注入 Controller，并触发第一次刷新（需在 CombinedUI 中调用）
     */
    public void setController(LocalizationController controller) {
        this.controller = controller;
        // initial display—CombinedUI 应该在节假日选择后调用 setHolidayId(...)
    }

    /**
     * 设置当前节假日ID并刷新显示（CombinedUI 切换假期时调用）
     */
    public void setHolidayId(Integer holidayId) {
        this.currentHolidayId = holidayId;
        refresh();
    }

    /**
     * 添加记录：关联 currentHolidayId
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
            controller.addPlan(currentHolidayId, ld, cat, pay, num.doubleValue());
            clearInputs();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除选中行记录
     */
    private void onDelete() {
        if (controller == null || currentHolidayId == null) {
            JOptionPane.showMessageDialog(this, "Please select a holiday first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
         int[] rows = table.getSelectedRows();
    if (rows.length == 0) {
        JOptionPane.showMessageDialog(this, "请先选中要删除的行", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }
    int choice = JOptionPane.showConfirmDialog(this, "确认删除选中记录？", "Confirm", JOptionPane.YES_NO_OPTION);
    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    // 调用 controller.removePlan(id) 删除
    for (int i = rows.length - 1; i >= 0; i--) {
        // 第 0 列即为 ID
        Integer id = (Integer) model.getValueAt(rows[i], 0);
        controller.removePlan(id);
    }
    // 删除后重新刷新：表格、饼图、totalLabel 都会一起更新
    refresh();
    }

    /**
     * 刷新表格与饼图
     */
    private void refresh() {
    model.setRowCount(0);
    Map<String, Double> sums = new HashMap<>();
    double total = 0.0;

    if (controller != null && currentHolidayId != null) {
        List<PlanDTO> plans = controller.getPlansForHoliday(currentHolidayId);
        for (PlanDTO p : plans) {
            // 第 0 列存放 ID，保证后面删除能准确找到记录
            model.addRow(new Object[]{
                p.getId(),
                p.getDate(),
                p.getCategory(),
                p.getPaymentMethod(),
                p.getAmount()
            });
            total += p.getAmount();
            sums.merge(p.getCategory(), p.getAmount(), Double::sum);
        }
    }

    // 更新总计显示（这里用本地货币格式，也可以自定义格式）
    NumberFormat fmt = NumberFormat.getCurrencyInstance();
    totalLabel.setText("Total: " + fmt.format(total));

    // 下面是更新饼图的原有逻辑，不需改动
    dataset.clear();
    if (sums.isEmpty()) {
        dataset.setValue("No Data", 1.0);
    } else {
        sums.forEach(dataset::setValue);
    }
}
    

    /**
     * 清空输入控件
     */
    private void clearInputs() {
        dateSpinner.setValue(new Date());
        categoryCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        amountField.setValue(null);
    }

    /**
     * 新增：根据 dataset 构建并返回一个 JFreeChart 饼图
     */
    private JFreeChart createPieChart() {
        JFreeChart chart = ChartFactory.createPieChart(
            "Spending by Category",  // 标题
            dataset,                // 数据集
            true,                   // 是否显示图例
            true,                   // 是否生成工具提示
            false                   // 是否生成 URLs
        );
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        return chart;
    }
}