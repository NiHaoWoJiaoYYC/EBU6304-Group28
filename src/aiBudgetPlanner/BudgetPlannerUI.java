package aiBudgetPlanner;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BudgetPlannerUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BudgetPlannerUI().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        setUIFont(new Font("Microsoft YaHei", Font.PLAIN, 15));

        JFrame frame = new JFrame("AI Personalized Budget Planning");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(250, 250, 250));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(8);
        splitPane.setContinuousLayout(true);

        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());

        frame.add(splitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void setUIFont(Font f) {
        UIManager.put("Label.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("TitledBorder.font", f);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(10, 10));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(Color.WHITE);

        leftPanel.add(createUserFormPanel(), BorderLayout.NORTH);
        leftPanel.add(createChartPlaceholder(), BorderLayout.CENTER);

        // 已删除左下 System Alert
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        // 表格上方展示
        JScrollPane tableScrollPane = createBudgetTablePanel();
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 包裹按钮 + Alert
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(createButtonPanel(), BorderLayout.NORTH);
        wrapperPanel.add(createAlertPanel(), BorderLayout.SOUTH);

        rightPanel.add(wrapperPanel, BorderLayout.SOUTH);
        return rightPanel;
    }

    private JPanel createUserFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            "AI Personalized Budget Planning"
        ));
        panel.setBackground(new Color(248, 250, 252));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        String[] labels = {
            "Occupation:", "Disposable Income:", "City You Live In:",
            "Family Situation:", "Number of Elderly:", "Number of Children:",
            "Have a Partner?", "Have Pets?"
        };

        for (String label : labels) {
            panel.add(new JLabel(label), gbc);
            gbc.gridx = 1;
            panel.add(new JTextField(14), gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }

        return panel;
    }

    private JPanel createChartPlaceholder() {
        JPanel chart = new JPanel();
        chart.setPreferredSize(new Dimension(400, 180));
        chart.setBackground(new Color(240, 245, 250));
        chart.setBorder(BorderFactory.createTitledBorder("Chart Placeholder"));
        return chart;
    }

    private JPanel createAlertPanel() {
        JPanel alert = new JPanel();
        alert.setLayout(new BoxLayout(alert, BoxLayout.Y_AXIS));
        alert.setBackground(Color.WHITE);
        alert.setBorder(BorderFactory.createTitledBorder("System Alert"));

        alert.add(new JLabel("1. Spending in one category exceeded budget."));
        alert.add(new JLabel("2. Reduce food delivery frequency – potential savings: ¥300/week."));
        alert.add(new JLabel("3. Limit entertainment expenses – excessive spending on mahjong."));

        return alert;
    }

    private JScrollPane createBudgetTablePanel() {
        String[] columns = {"Category", "Subcategory", "%", "¥"};
        Object[][] data = {
                {"Food", "", "", ""},
                {"Housing/Rent", "", "", ""},
                {"Daily Necessities", "", "", ""},
                {"Transportation", "", "", ""},
                {"Entertainment", "", "", ""},
                {"Shopping", "", "", ""},
                {"Healthcare", "", "", ""},
                {"Education", "", "", ""},
                {"Childcare", "", "", ""},
                {"Gifts", "", "", ""},
                {"Savings", "", "", ""},
                {"Others", "", "", ""}
        };

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI-Recommended Budget Plan"));

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);

        JButton adjustBtn = new JButton("Adjust");
        JButton addBtn = new JButton("Add category");
        JButton saveBtn = new JButton("Save");

        for (JButton btn : new JButton[]{adjustBtn, addBtn, saveBtn}) {
            btn.setBackground(new Color(230, 240, 255));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(130, 35));
        }

        panel.add(adjustBtn);
        panel.add(addBtn);
        panel.add(saveBtn);

        return panel;
    }
}
