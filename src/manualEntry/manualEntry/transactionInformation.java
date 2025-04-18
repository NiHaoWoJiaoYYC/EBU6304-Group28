package manualEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author Jing Wenrui
 * Function: Save the transaction information  
 */

public class transactionInformation {
    private String date;
    private double amount;
    private String type;
    private String object;
    private String remarks;

	public transactionInformation(String date, double amount, String type, String object, String remarks) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.object = object;
        this.remarks = remarks;
	}
	
    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getObject() {
        return object;
    }

    public String getRemarks() {
        return remarks;
    }
    
 // Define a static list to save the transaction information
    public static List<transactionInformation> transactionList = new ArrayList<>();

 // Add the transaction information into the list
    public static void addTransaction(transactionInformation transaction) {
        transactionList.add(transaction);
    }    

}
