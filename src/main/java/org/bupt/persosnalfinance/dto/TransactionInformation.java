package org.bupt.persosnalfinance.dto;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Represents transaction information and provides static methods for
 * managing a list of transactions including JSON persistence.
 *
 * @author Jing Wenrui
 */

public class TransactionInformation {
    private String date;
    private double amount;
    private String type;
    private String object;
    private String remarks;

    /**
     * Constructs a transaction with all necessary details.
     *
     * @param date    the date of the transaction (format: yyyy/MM/dd)
     * @param amount  the amount of the transaction
     * @param type    the type/category of the transaction
     * @param object  the target/object involved in the transaction
     * @param remarks additional remarks or notes
     */

    public TransactionInformation(String date, double amount, String type, String object, String remarks) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.object = object;
        this.remarks = remarks;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    // Getters
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getObject() { return object; }
    public String getRemarks() { return remarks; }

    /**
     * A static list that stores all transaction records in memory.
     */
    public static List<TransactionInformation> transactionList = new ArrayList<>();

    /**
     * Adds a transaction to the static transaction list.
     *
     * @param transaction the transaction to add
     */
    public static void addTransaction(TransactionInformation transaction) {
        transactionList.add(transaction);
    }

    /**
     * Saves the current list of transactions to a JSON file.
     *
     * @param filename the file path where the JSON will be saved
     */
    public static void saveToJSON(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(transactionList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads transaction records from a JSON file into the static list.
     * Clears the existing list before loading.
     *
     * @param filename the path to the JSON file to load
     */
    public static void loadFromJSON(String filename) {
        transactionList.clear();
        try (Reader reader = new FileReader(filename)) {
            Type listType = new TypeToken<List<TransactionInformation>>() {}.getType();
            List<TransactionInformation> loaded = new Gson().fromJson(reader, listType);
            if (loaded != null) transactionList.addAll(loaded);
        } catch (IOException e) {
            //If the file does not exist, it can be ignored.
        }
    }
}