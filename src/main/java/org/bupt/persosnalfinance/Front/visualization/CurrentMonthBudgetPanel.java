package org.bupt.persosnalfinance.Front.visualization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bupt.persosnalfinance.Back.Service.BudgetAIService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class CurrentMonthBudgetPanel extends JPanel {
    private final String jsonPath;
    private final Runnable onSave;
    private final JTextField incomeField;
    private LinkedHashMap<String, Double> budgets;        // 当前预算
    private final Map<String, Double> actuals;            // 实际支出
    private final DefaultTableModel model;
    private final Set<String> userEditedCategories = new HashSet<>();

    public CurrentMonthBudgetPanel(String jsonPath, Runnable onSave) {
        this.jsonPath = jsonPath;
        this.onSave   = onSave;

        // 1. 读取 JSON
        LinkedHashMap<String, Double> tmp = new LinkedHashMap<>();
        JTextField tmpIncome = new JTextField();
        try (Reader r = new FileReader(jsonPath)) {
            Gson gson = new Gson();
            Type t = new TypeToken<BudgetData>() {}.getType();
            BudgetData data = gson.fromJson(r, t);
            tmpIncome.setText(String.valueOf(data.disposableIncome));
            tmp.putAll(data.budgets);
        } catch (Exception e) {
            tmpIncome.setText("0");
            e.printStackTrace();
        }

        // 2. 读取实际支出
        Map<String, Double> tmpActuals =
                BudgetAIService.getActualSpendingFromJson("src/main/data/transactionInformation.json");

        this.incomeField = tmpIncome;
        this.budgets     = tmp;
        this.actuals     = tmpActuals;

        // 3. 表格模型，只允许第2列（预算）编辑
        this.model = new DefaultTableModel(new String[]{"类别","预算 (¥)","实际支出 (¥)"},0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1;
            }
        };

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5,5));

        // 顶部：可支配收入 + 保存按钮
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("可支配收入："));
        top.add(incomeField);
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> saveData());
        top.add(saveBtn);
        add(top, BorderLayout.NORTH);

        // 表格：类别 | 预算 | 实际支出
        budgets.forEach((cat,b) -> {
            double a = actuals.getOrDefault(cat,0.0);
            model.addRow(new Object[]{cat, b, a});
        });
        JTable table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void saveData() {
        try {
            double newIncome = Double.parseDouble(incomeField.getText());

            // 1) 从表格读出所有行的新预算
            LinkedHashMap<String, Double> updated = new LinkedHashMap<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                String cat = model.getValueAt(i,0).toString();
                double b   = Double.parseDouble(model.getValueAt(i,1).toString());
                updated.put(cat,b);
            }

            // 2) 哪些是用户本次主动编辑的？跟旧 budgets 比
            Set<String> newlyEdited = updated.entrySet().stream()
                    .filter(e -> !Objects.equals(e.getValue(), budgets.get(e.getKey())))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            userEditedCategories.addAll(newlyEdited);

            // 3) 计算已编辑类别预算总和 & 未编辑类别原预算总和
            double sumEdited = userEditedCategories.stream()
                    .mapToDouble(updated::get).sum();
            double sumOthersOrig = budgets.entrySet().stream()
                    .filter(e -> !userEditedCategories.contains(e.getKey()))
                    .mapToDouble(Map.Entry::getValue).sum();

            // 4) 差值分配给“未编辑”那部分
            double diff = newIncome - sumEdited;
            LinkedHashMap<String, Double> finalBudgets = new LinkedHashMap<>();
            for (String cat : updated.keySet()) {
                if (userEditedCategories.contains(cat)) {
                    finalBudgets.put(cat, updated.get(cat));
                } else {
                    double orig = budgets.get(cat);
                    double scaled = orig * (diff / sumOthersOrig);
                    finalBudgets.put(cat, scaled);
                }
            }

            // 5) 写回 JSON
            BudgetData data = new BudgetData();
            data.disposableIncome = newIncome;
            data.budgets          = finalBudgets;
            try (Writer w = new FileWriter(jsonPath)) {
                new Gson().toJson(data, w);
            }

            // 6) 更新内存 & 表格视图
            budgets.clear();
            budgets.putAll(finalBudgets);
            for (int i = 0; i < model.getRowCount(); i++) {
                String cat = model.getValueAt(i,0).toString();
                model.setValueAt(finalBudgets.get(cat), i, 1);
            }

            JOptionPane.showMessageDialog(this, "保存成功！");
            onSave.run();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "保存失败：" + ex.getMessage());
        }
    }

    private static class BudgetData {
        double disposableIncome;
        Map<String, Double> budgets;
    }
}
