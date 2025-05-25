package org.bupt.persosnalfinance.Front.ManualEntry;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import org.bupt.persosnalfinance.dto.TransactionInformation;

/**
 * The ManualEntry class provides a graphical user interface (GUI)
 * for users to manually enter transaction information.
 * Users can input transaction date, amount, type, object, and remarks,
 * and then save the information to a JSON file.
 *
 * @author Jing Wenrui
 */

public class ManualEntry {
    /**
     * The main method launches the manual entry window.
     */
    public static void main(String[] args) {
        // Load existing transaction data
        TransactionInformation.loadFromJSON("src/main/data/transactionInformation.json");

        JFrame frame = new JFrame("Transaction information > Manual Entry");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Transaction date input
        JLabel transactionDate = new JLabel("Transaction date:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(transactionDate, gbc);

        JTextField dateField = new JTextField("yyyy/MM/dd", 15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dateField, gbc);

        // Transaction amount input
        JLabel transactionAmount = new JLabel("Transaction amount :");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(transactionAmount, gbc);

        JTextField amountField = new JTextField("0", 15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(amountField, gbc);

        // Transaction type dropdown
        JLabel typeLabel = new JLabel("Transaction type :");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(typeLabel, gbc);

        String[] types = {
                "Food", "Housing/Rent", "Daily Necessities", "Transportation",
                "Entertainment", "Shopping", "Healthcare", "Education",
                "Childcare", "Gifts", "Savings", "Others", "Income"
        };
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(typeComboBox, gbc);

        // Transaction object input
        JLabel objectLabel = new JLabel("Transaction object :");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(objectLabel, gbc);

        JTextField objectField = new JTextField("enter...", 15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(objectField, gbc);

        // Transaction object input
        JLabel remarkLabel = new JLabel("Remark :");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(remarkLabel, gbc);

        JTextArea remarkArea = new JTextArea("Add Remark...", 5, 20);
        JScrollPane scrollPane = new JScrollPane(remarkArea);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("cancel");
        JButton saveButton = new JButton("save");
        JButton viewButton = new JButton("View Existing Transactions");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(viewButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Save button action: validate input and store transaction
        saveButton.addActionListener(e -> {
            try {
                // date validation
                String date = dateField.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                sdf.setLenient(false);
                sdf.parse(date); // 不合法将抛异常

                // amount validation
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
                    TransactionInformation.saveToJSON("src/main/data/transactionInformation.json");

                    int viewResult = JOptionPane.showConfirmDialog(frame, "Saved successfully! View data?", "View", JOptionPane.YES_NO_OPTION);
                    if (viewResult == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        new DisplayTransactionInformation();
                    }
                }

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Please use yyyy/MM/dd.", "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a non-negative number.", "Amount Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action: close window
        cancelButton.addActionListener(e -> frame.dispose());

        // View button action: open view window
        viewButton.addActionListener(e -> new DisplayTransactionInformation());

        // Focus listeners: placeholder behavior
        dateField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (dateField.getText().equals("yyyy/MM/dd")) dateField.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (dateField.getText().trim().isEmpty()) dateField.setText("yyyy/MM/dd");
            }
        });

        amountField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (amountField.getText().equals("0")) amountField.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (amountField.getText().trim().isEmpty()) amountField.setText("0");
            }
        });

        objectField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (objectField.getText().equals("enter...")) objectField.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (objectField.getText().trim().isEmpty()) objectField.setText("enter...");
            }
        });

        remarkArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (remarkArea.getText().equals("Add Remark...")) remarkArea.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (remarkArea.getText().trim().isEmpty()) remarkArea.setText("Add Remark...");
            }
        });


        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);
    }
}
