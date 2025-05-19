package org.bupt.persosnalfinance.dto;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class TransactionInformation {
    private String date;
    private double amount;
    private String type;
    private String object;
    private String remarks;

    public TransactionInformation(String date, double amount, String type, String object, String remarks) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.object = object;
        this.remarks = remarks;
    }

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


    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getObject() { return object; }
    public String getRemarks() { return remarks; }

    public static List<TransactionInformation> transactionList = new ArrayList<>();

    public static void addTransaction(TransactionInformation transaction) {
        transactionList.add(transaction);
    }

    public static void saveToJSON(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(transactionList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromJSON(String filename) {
        transactionList.clear();
        try (Reader reader = new FileReader(filename)) {
            Type listType = new TypeToken<List<TransactionInformation>>() {}.getType();
            List<TransactionInformation> loaded = new Gson().fromJson(reader, listType);
            if (loaded != null) transactionList.addAll(loaded);
        } catch (IOException e) {
            // 文件不存在可忽略
        }
    }
}
