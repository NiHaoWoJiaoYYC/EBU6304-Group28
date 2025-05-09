//package manualEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @version 3.0
 * @author Jing Wenrui
 * Function: Manually entry transaction information
 */

public class mainPage {

    public static void main(String[] args) {
        transactionInformation.loadFromCSV("transactionInformation.csv");

        JFrame frame = new JFrame("Transaction information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);

        // Set the label to align left
        gbc.anchor = GridBagConstraints.WEST;

        // Transaction date entry
        JLabel transactionDate = new JLabel("Transaction date :");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(transactionDate, gbc);

        JTextField dateField = new JTextField("Month/Day/Year", 15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 200 - dateField.getPreferredSize().width;
        panel.add(dateField, gbc);

        // Transaction amount entry
        JLabel transactionAmount = new JLabel("Transaction amount :");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(transactionAmount, gbc);

        JTextField amountField = new JTextField("0", 15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 200 - dateField.getPreferredSize().width;
        panel.add(amountField, gbc);

        // Transaction type entry
        JLabel typeLabel = new JLabel("Transaction type :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(typeLabel, gbc);

        String[] types = {"expenditure:Food", "expenditure:Shopping", "expenditure:Traffic",
                "expenditure:Entertainment", "expenditure:Education", "expenditure:Medical",
                "expenditure:Treatment", "expenditure:Other expenditures",
                "income:Wage income", "income:Investment income", "income:Other income"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 200 - dateField.getPreferredSize().width;
        panel.add(typeComboBox, gbc);

        // Transaction object entry
        JLabel objectLabel = new JLabel("Transaction object :");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(objectLabel, gbc);

        JTextField objectField = new JTextField("enter...", 15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 200 - dateField.getPreferredSize().width;
        panel.add(objectField, gbc);

        // Remark entry
        JLabel remarkLabel = new JLabel("Remark :");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(remarkLabel, gbc);

        JTextArea remarkArea = new JTextArea("Add Remark...", 5, 20);
        JScrollPane scrollPane = new JScrollPane(remarkArea);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 200 - dateField.getPreferredSize().width;
        panel.add(scrollPane, gbc);

        // cancel button and save button
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("cancel");
        JButton saveButton = new JButton("save");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Save button event
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String date = dateField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    String type = (String) typeComboBox.getSelectedItem();
                    String object = objectField.getText();
                    String remarks = remarkArea.getText();

                    transactionInformation transaction = new transactionInformation(date, amount, type, object, remarks);
                    transactionInformation.addTransaction(transaction);

                    //save to transactionInformation.csv
                    transactionInformation.saveToCSV("transactionInformation.csv");

                    int result = JOptionPane.showConfirmDialog(frame, "The transaction record is saved successfully！Do you want to view the saved information?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        new displayPage();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for the transaction amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Set the window to be centered relative to the screen
        frame.setLocationRelativeTo(null);

        // Add panel to window
        frame.add(panel);
        frame.setVisible(true);

    }

}
