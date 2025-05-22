package org.bupt.persosnalfinance.Util;

import org.bupt.persosnalfinance.dto.TransactionInformation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JSON ↔ 表格转换工具
 */
public class Converter2 {

    /* ---------------------------------------------------------------- */
    /* ① 整表转换方法                                                   */
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

        Map<String, List<TransactionInformation>> grouped = new LinkedHashMap<>();

        list.stream()
                .sorted(Comparator.comparing(t ->
                        LocalDate.parse(t.getDate(), srcFmt)))
                .forEach(t -> {
                    LocalDate d = LocalDate.parse(t.getDate(), srcFmt);
                    String key = d.format(keyFmt);        // e.g. 2024-02
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
                });

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
     * 返回指定月份的完整二维表
     */
    public static String[][] jsonToMonthTable(String jsonPath, String monthKey) {
        return jsonToMonthlyTables(jsonPath)
                .getOrDefault(monthKey, new String[0][0]);
    }

    /* ---------------------------------------------------------------- */
    /* ③ 指定月份 -> 各类别总开销二维表                                 */
    /* ---------------------------------------------------------------- */
    /**
     * @param jsonPath "src/main/data/transactionInformation.json"
     * @param monthKey "yyyy-MM" 形式的月份键（例：2024-02）
     * @return String[row][2]，每行 [type, totalAmount]；若无记录返回空数组
     */
    public static Double[] monthTypeSummary(String jsonPath, String monthKey) {

        // 读取指定月份的全部明细
        String[][] monthTable = jsonToMonthTable(jsonPath, monthKey);
        if (monthTable.length == 0) return new Double[0];

        // 累加各类型金额
        Map<String, Double> sumMap = new LinkedHashMap<>();
        for (String[] row : monthTable) {
            String type  = row[2];
            double amt   = Double.parseDouble(row[1]);
            sumMap.merge(type, amt, Double::sum);
        }

        // 转二维数组
        String[][] summary = new String[sumMap.size()][2];
        int i = 0;
        for (var e : sumMap.entrySet()) {
            summary[i][0] = e.getKey();
            summary[i][1] = String.valueOf(e.getValue());
            i++;
        }
        Double[] values = new Double[summary.length];
        for (int j = 0; j < summary.length; j++) {
            values[j] = Double.parseDouble(summary[j][1]);
        }
        return values;
    }


}
