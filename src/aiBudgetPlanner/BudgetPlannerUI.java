package aiBudgetPlanner;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import aiBudgetPlanner.BudgetAIService;

public class BudgetPlannerUI {

    private JTextField occupationField;
    private JTextField incomeField;
    private JTextField cityField;
    private JTextField elderlyField;
    private JTextField childrenField;
    private JTextField partnerField;
    private JTextField petsField;

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

        JPanel userFormPanel = createUserFormPanel();
        leftPanel.add(userFormPanel, BorderLayout.NORTH);
        leftPanel.add(createChartPlaceholder(), BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        JScrollPane tableScrollPane = createBudgetTablePanel();
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

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

        panel.add(new JLabel("Occupation:"), gbc); gbc.gridx = 1;
        occupationField = new JTextField(14); panel.add(occupationField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        panel.add(new JLabel("Disposable Income:"), gbc); gbc.gridx = 1;
        incomeField = new JTextField(14); panel.add(incomeField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        panel.add(new JLabel("City You Live In:"), gbc); gbc.gridx = 1;
        cityField = new JTextField(14); panel.add(cityField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        panel.add(new JLabel("Number of Elderly:"), gbc); gbc.gridx = 1;
        elderlyField = new JTextField(14); panel.add(elderlyField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        panel.add(new JLabel("Number of Children:"), gbc); gbc.gridx = 1;
        childrenField = new JTextField(14); panel.add(childrenField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        panel.add(new JLabel("Have a Partner?"), gbc); gbc.gridx = 1;
        partnerField = new JTextField(14); panel.add(partnerField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        panel.add(new JLabel("Have Pets?"), gbc); gbc.gridx = 1;
        petsField = new JTextField(14); panel.add(petsField, gbc);
        gbc.gridx = 0; gbc.gridy++;

        JButton generateBtn = new JButton("Generate Budget");
        generateBtn.setBackground(new Color(180, 220, 250));
        generateBtn.setFocusPainted(false);
        gbc.gridwidth = 2;
        panel.add(generateBtn, gbc);

        generateBtn.addActionListener(e -> {
            try {
                String occupation = occupationField.getText();
                double income = Double.parseDouble(incomeField.getText());
                String city = cityField.getText();
                int elderly = Integer.parseInt(elderlyField.getText());
                int children = Integer.parseInt(childrenField.getText());
                boolean hasPartner = Boolean.parseBoolean(partnerField.getText());
                boolean hasPets = Boolean.parseBoolean(petsField.getText());

                UserInfo user = new UserInfo(
                        occupation, income, city,
                        elderly, children, hasPartner, hasPets
                );

                Map<String, Double> aiBudget = BudgetAIService.generateBudget(user);

                StringBuilder sb = new StringBuilder("AI 预算生成成功：\n");
                for (Map.Entry<String, Double> entry : aiBudget.entrySet()) {
                    sb.append(entry.getKey()).append("：￥").append(entry.getValue()).append("\n");
                }

                JOptionPane.showMessageDialog(null, sb.toString(), "生成成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "请输入正确的格式，例如收入必须是数字！", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        });

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
        String[] columns = {"Category", "%", "¥"};
        Object[][] data = {
                {"Food", "", ""},
                {"Housing/Rent", "", ""},
                {"Daily Necessities", "", ""},
                {"Transportation", "", ""},
                {"Entertainment", "", ""},
                {"Shopping", "", ""},
                {"Healthcare", "", ""},
                {"Education", "", ""},
                {"Childcare", "", ""},
                {"Gifts", "", ""},
                {"Savings", "", ""},
                {"Others", "", ""}
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
