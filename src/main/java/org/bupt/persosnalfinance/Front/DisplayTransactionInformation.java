package org.bupt.persosnalfinance.Front;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.bupt.persosnalfinance.dto.TransactionInformation;

public class DisplayTransactionInformation {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    public DisplayTransactionInformation() {
        TransactionInformation.loadFromJSON("transactionInformation.json");

        frame = new JFrame("Transaction Information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        String[] columnNames = {"Number", "Date", "Amount", "Type", "Object", "Remarks"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(e -> editSelectedTransaction());
        deleteButton.addActionListener(e -> deleteSelectedTransaction());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<TransactionInformation> transactions = TransactionInformation.transactionList;
        for (int i = 0; i < transactions.size(); i++) {
            TransactionInformation t = transactions.get(i);
            tableModel.addRow(new Object[]{i + 1, t.getDate(), t.getAmount(), t.getType(), t.getObject(), t.getRemarks()});
        }
    }

    private void editSelectedTransaction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a row to edit.");
            return;
        }

        TransactionInformation t = TransactionInformation.transactionList.get(selectedRow);

        JTextField dateField = new JTextField(t.getDate());
        JTextField amountField = new JTextField(String.valueOf(t.getAmount()));
        String[] types = {
                "expenditure:Food", "expenditure:Shopping", "expenditure:Traffic",
                "expenditure:Entertainment", "expenditure:Education", "expenditure:Medical",
                "expenditure:Treatment", "expenditure:Other expenditures",
                "income:Wage income", "income:Investment income", "income:Other income"
        };
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        typeComboBox.setSelectedItem(t.getType());
        JTextField objectField = new JTextField(t.getObject());
        JTextField remarksField = new JTextField(t.getRemarks());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Date (MM/dd/yyyy):"));
        panel.add(dateField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Type:"));
        panel.add(typeComboBox);
        panel.add(new JLabel("Object:"));
        panel.add(objectField);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String date = dateField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String type = (String) typeComboBox.getSelectedItem();
                String object = objectField.getText().trim();
                String remarks = remarksField.getText().trim();

                if (!isValidDate(date)) {
                    throw new IllegalArgumentException("Invalid date format. Use MM/dd/yyyy.");
                }
                if (amount < 0) {
                    throw new IllegalArgumentException("Amount must be non-negative.");
                }

                t.setDate(date);
                t.setAmount(amount);
                t.setType(type);
                t.setObject(object);
                t.setRemarks(remarks);

                TransactionInformation.saveToJSON("transactionInformation.json");
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number for amount.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedTransaction() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            TransactionInformation.transactionList.remove(selectedRow);
            TransactionInformation.saveToJSON("transactionInformation.json");
            refreshTable();
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        new DisplayTransactionInformation();
    }
}
