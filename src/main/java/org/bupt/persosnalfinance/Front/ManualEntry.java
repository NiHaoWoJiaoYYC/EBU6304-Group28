package org.bupt.persosnalfinance.Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.bupt.persosnalfinance.dto.TransactionInformation;

public class ManualEntry {
    public static void main(String[] args) {
        TransactionInformation.loadFromJSON("transactionInformation.json");

        JFrame frame = new JFrame("Transaction information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel transactionDate = new JLabel("Transaction date (MM/dd/yyyy) :");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(transactionDate, gbc);

        JTextField dateField = new JTextField("MM/dd/yyyy", 15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dateField, gbc);

        JLabel transactionAmount = new JLabel("Transaction amount :");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(transactionAmount, gbc);

        JTextField amountField = new JTextField("0", 15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(amountField, gbc);

        JLabel typeLabel = new JLabel("Transaction type :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(typeLabel, gbc);

        String[] types = {
                "expenditure:Food", "expenditure:Shopping", "expenditure:Traffic",
                "expenditure:Entertainment", "expenditure:Education", "expenditure:Medical",
                "expenditure:Treatment", "expenditure:Other expenditures",
                "income:Wage income", "income:Investment income", "income:Other income"
        };
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(typeComboBox, gbc);

        JLabel objectLabel = new JLabel("Transaction object :");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(objectLabel, gbc);

        JTextField objectField = new JTextField("enter...", 15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(objectField, gbc);

        JLabel remarkLabel = new JLabel("Remark :");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(remarkLabel, gbc);

        JTextArea remarkArea = new JTextArea("Add Remark...", 5, 20);
        JScrollPane scrollPane = new JScrollPane(remarkArea);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("cancel");
        JButton saveButton = new JButton("save");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                // 日期验证
                String date = dateField.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                sdf.setLenient(false);
                sdf.parse(date); // 不合法将抛异常

                // 金额验证
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount < 0) {
                    throw new NumberFormatException("Amount must be non-negative.");
                }

                String type = (String) typeComboBox.getSelectedItem();
                String object = objectField.getText().trim();
                String remarks = remarkArea.getText().trim();

                int result = JOptionPane.showConfirmDialog(frame, "Do you want to save the transaction?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    TransactionInformation transaction = new TransactionInformation(date, amount, type, object, remarks);
                    TransactionInformation.addTransaction(transaction);
                    TransactionInformation.saveToJSON("transactionInformation.json");

                    int viewResult = JOptionPane.showConfirmDialog(frame, "Saved successfully! View data?", "View", JOptionPane.YES_NO_OPTION);
                    if (viewResult == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        new DisplayTransactionInformation();
                    }
                }

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Please use MM/dd/yyyy.", "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a non-negative number.", "Amount Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);
    }
}
