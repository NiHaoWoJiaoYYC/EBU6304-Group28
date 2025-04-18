package manualEntry;

import javax.swing.*;

/**
 * @version 1.0
 * @author Jing Wenrui
 * Function: display the transaction information  
 */

public class displayPage {

	public displayPage() {
        JFrame frame = new JFrame("Transaction information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JTable table;
        String[] columnNames = {"Number", "Date", "Amount", "Type", "Object", "Remarks"};
        Object[][] data = new Object[transactionInformation.transactionList.size()][6];

        for (int i = 0; i < transactionInformation.transactionList.size(); i++) {
            transactionInformation transaction = transactionInformation.transactionList.get(i);
            data[i][0] = i + 1;
            data[i][1] = transaction.getDate();
            data[i][2] = transaction.getAmount();
            data[i][3] = transaction.getType();
            data[i][4] = transaction.getObject();
            data[i][5] = transaction.getRemarks();
	    }
        
        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}
    
    public static void main(String[] args) {
        new displayPage();
    }
}
