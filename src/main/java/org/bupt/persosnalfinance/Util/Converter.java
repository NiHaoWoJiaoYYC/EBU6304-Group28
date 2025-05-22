package org.bupt.persosnalfinance.Util;

import org.bupt.persosnalfinance.dto.TransactionInformation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Converter {
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
