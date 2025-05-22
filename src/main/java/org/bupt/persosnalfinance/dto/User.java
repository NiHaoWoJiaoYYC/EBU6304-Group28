package org.bupt.persosnalfinance.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class User {
    private double[] lastQuarterAvg; // 上期花费
    private double[] thisQuarter;    // 本期花费

    // Getters 和 Setters
    public double[] getLastQuarterAvg() {
        return lastQuarterAvg;
    }

    public void setLastQuarterAvg(double[] lastQuarterAvg) {
        this.lastQuarterAvg = lastQuarterAvg;
    }

    public double[] getThisQuarter() {
        return thisQuarter;
    }

    public void setThisQuarter(double[] thisQuarter) {
        this.thisQuarter = thisQuarter;
    }

    // ✅ 添加静态方法生成测试数据
    public static User mockUser() {
        User user = new User();

        // 假设有 12 个分类（与 BudgetController 中定义的分类顺序保持一致）
        user.lastQuarterAvg = new double[]{
                1000, 2000, 300, 400,
                500, 600, 700, 800,
                900, 150, 1200, 100
        };

        user.thisQuarter = new double[]{
                1100, 1800, 350, 450,
                550, 700, 800, 900,
                950, 200, 1000, 120
        };

        return user;
    }


    /* ---------------------------------------------------------------- */
    /* ① 整表转换方法（保留）                                           */
    /* ---------------------------------------------------------------- */
    public static String[][] jsonToTable(String jsonPath) {
        TransactionInformation.loadFromJSON(jsonPath);
        List<TransactionInformation> list = TransactionInformation.transactionList;

        String[][] table = new String[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            TransactionInformation t = list.get(i);
            table[i][0] = t.getDate();
            table[i][1] = String.valueOf(t.getAmount());
            table[i][2] = t.getType();
            table[i][3] = t.getObject();
            table[i][4] = t.getRemarks();
        }
        return table;
    }

    /* ---------------------------------------------------------------- */
    /* ② 按月份拆分为多张二维数组                                       */
    /* ---------------------------------------------------------------- */
    public static Map<String, String[][]> jsonToMonthlyTables(String jsonPath) {

        TransactionInformation.loadFromJSON(jsonPath);
        List<TransactionInformation> list = TransactionInformation.transactionList;
        if (list.isEmpty()) return Map.of();

        DateTimeFormatter srcFmt = DateTimeFormatter.ofPattern("yyyy/M/d");
        DateTimeFormatter keyFmt = DateTimeFormatter.ofPattern("yyyy-MM");

        /* 使用 TreeMap 先按日期排序，再分月 */
        Map<String, List<TransactionInformation>> grouped = new LinkedHashMap<>();

        list.stream()
                .sorted(Comparator.comparing(t ->
                        LocalDate.parse(t.getDate(), srcFmt)))
                .forEach(t -> {
                    LocalDate d = LocalDate.parse(t.getDate(), srcFmt);
                    String key = d.format(keyFmt);      // 2024-02
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
                });

        /* 转二维数组 */
        Map<String, String[][]> result = new LinkedHashMap<>();
        for (var e : grouped.entrySet()) {
            List<TransactionInformation> rows = e.getValue();
            String[][] arr = new String[rows.size()][5];
            for (int i = 0; i < rows.size(); i++) {
                TransactionInformation t = rows.get(i);
                arr[i][0] = t.getDate();
                arr[i][1] = String.valueOf(t.getAmount());
                arr[i][2] = t.getType();
                arr[i][3] = t.getObject();
                arr[i][4] = t.getRemarks();
            }
            result.put(e.getKey(), arr);
        }
        return result;
    }

    /**
     * 读取 JSON 并仅返回指定月份 (yyyy-MM) 的二维数组。
     * 若该月份不存在交易记录，返回空数组。
     */
    public static String[][] jsonToMonthTable(String jsonPath, String monthKey) {

        /* 先调用已存在的分表方法，避免重复代码 */
        Map<String, String[][]> byMonth = jsonToMonthlyTables(jsonPath);

        return byMonth.getOrDefault(monthKey, new String[0][0]);
    }

}