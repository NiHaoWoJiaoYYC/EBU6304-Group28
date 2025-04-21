//package manualEntry;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;

/**
 * @version 3.0
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

    // Save as a CSV file
    public static void saveToCSV(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (transactionInformation t : transactionList) {
                pw.printf("%s,%.2f,%s,%s,%s%n",
                        t.getDate(),
                        t.getAmount(),
                        t.getType().replace(",", " "),  // 防止逗号干扰CSV结构
                        t.getObject().replace(",", " "),
                        t.getRemarks().replace(",", " "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read from the CSV file
    public static void loadFromCSV(String filename) {
        transactionList.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String date = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String type = parts[2];
                    String object = parts[3];
                    String remarks = parts[4];
                    transactionList.add(new transactionInformation(date, amount, type, object, remarks));
                }
            }
        } catch (IOException e) {
            
        }
    }
}
