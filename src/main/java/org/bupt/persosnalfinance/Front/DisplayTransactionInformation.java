package org.bupt.persosnalfinance.Front;

import javax.swing.*;
import org.bupt.persosnalfinance.dto.TransactionInformation;

public class DisplayTransactionInformation {
    public DisplayTransactionInformation() {
        TransactionInformation.loadFromJSON("transactionInformation.json");

        JFrame frame = new JFrame("Transaction information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        String[] columnNames = {"No.", "Date", "Amount", "Type", "Object", "Remarks"};
        Object[][] data = new Object[TransactionInformation.transactionList.size()][6];

        for (int i = 0; i < TransactionInformation.transactionList.size(); i++) {
            TransactionInformation t = TransactionInformation.transactionList.get(i);
            data[i][0] = i + 1;
            data[i][1] = t.getDate();
            data[i][2] = t.getAmount();
            data[i][3] = t.getType();
            data[i][4] = t.getObject();
            data[i][5] = t.getRemarks();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new DisplayTransactionInformation();
    }
}
