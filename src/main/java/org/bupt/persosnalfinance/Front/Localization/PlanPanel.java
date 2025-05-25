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
 * PlanPanel supports displaying and adding budget records grouped by Holiday ID, with memory when switching between Holidays.
 * Use LocalizationController to unify the scheduling of business logic.
 */
public class PlanPanel extends JPanel {
    private LocalizationController controller;
    private Integer currentHolidayId;

    private final DefaultTableModel model;
    private final JTable table;
    private JLabel totalLabel;
    private DefaultPieDataset dataset = new DefaultPieDataset();

    // Input components for adding records
    private final JSpinner dateSpinner;
    private final JComboBox<String> categoryCombo;
    private final JComboBox<String> paymentCombo;
    private final JFormattedTextField amountField;
    private final JButton addButton;
    private final JButton deleteButton;

    public PlanPanel() {
        super(new BorderLayout(5,5));
        setBorder(BorderFactory.createTitledBorder("Spending Plan"));

        // Top: Input area
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

        // Centre: Table
    // Instead of only Date/Category/Payment/Amount columns, the first column is dedicated to IDs.
    model = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Payment", "Amount"}, 0);
    table = new JTable(model);
    // Hide ID Column
    table.getColumnModel().getColumn(0).setMinWidth(0);
    table.getColumnModel().getColumn(0).setMaxWidth(0);
JScrollPane tableScroll = new JScrollPane(table);

        totalLabel = new JLabel("Total: 0.00", SwingConstants.LEFT);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        leftPanel.add(totalLabel, BorderLayout.SOUTH);

        // pie chart
        ChartPanel chartPanel = new ChartPanel(createPieChart());
        // Adjust the preferred size of the pie chart panel to prevent it from being too large
        chartPanel.setPreferredSize(new Dimension(300, 250));

        // Split horizontally: table + Total on the left, pie chart on the right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, chartPanel);
        // Here you can position the segmentation line using proportions or specific pixels
        splitPane.setResizeWeight(0.6);      // 60 per cent on the left side
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER); 
        
        // Loading data for the first time
        refresh();
    }


    /**
     * Injects the Controller and triggers the first refresh (needs to be called in CombinedUI)
     */
    public void setController(LocalizationController controller) {
        this.controller = controller;
        // initial display-CombinedUI should call setHolidayId(...) after the holiday has been selected.
    }

    /**
     * Set the current holiday ID and refresh the display (called when CombinedUI switches holidays)
     */
    public void setHolidayId(Integer holidayId) {
        this.currentHolidayId = holidayId;
        refresh();
    }

    /**
     * Add record: associate currentHolidayId
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
     * Delete selected rows
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

    // Call controller.removePlan(id) to remove the
    for (int i = rows.length - 1; i >= 0; i--) {
        // Column 0 is the ID
        Integer id = (Integer) model.getValueAt(rows[i], 0);
        controller.removePlan(id);
    }
    // Refresh after deletion: tables, pie charts, totalLabel all update together!
    refresh();
    }

    /**
     * Refresh Tables and Pie Charts
     */
    private void refresh() {
    model.setRowCount(0);
    Map<String, Double> sums = new HashMap<>();
    double total = 0.0;

    if (controller != null && currentHolidayId != null) {
        List<PlanDTO> plans = controller.getPlansForHoliday(currentHolidayId);
        for (PlanDTO p : plans) {
            // Column 0 holds the ID to ensure that later deletions can find the exact record.
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

    // Update the totals display (local currency format is used here, or you can customise the format)
    NumberFormat fmt = NumberFormat.getCurrencyInstance();
    totalLabel.setText("Total: " + fmt.format(total));

    // Here's the original logic for updating the pie chart without changes
    dataset.clear();
    if (sums.isEmpty()) {
        dataset.setValue("No Data", 1.0);
    } else {
        sums.forEach(dataset::setValue);
    }
}
    

    /**
     * Clear Input Controls
     */
    private void clearInputs() {
        dateSpinner.setValue(new Date());
        categoryCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        amountField.setValue(null);
    }

    /**
     * New: Build and return a JFreeChart pie chart based on dataset.
     */
    private JFreeChart createPieChart() {
        JFreeChart chart = ChartFactory.createPieChart(
            "Spending by Category",  // caption
            dataset,                // data set
            true,                   // Whether to display the legend
            true,                   // Whether to generate tooltips
            false                   // Whether to generate URLs
        );
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        return chart;
    }
}