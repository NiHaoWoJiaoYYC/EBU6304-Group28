package org.bupt.persosnalfinance.Util;

import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JSON ↔ 表格转换工具
 */
@Component
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
    /* ③ 指定月份 -> 固定顺序类别金额数组                               */
    /* ---------------------------------------------------------------- */
    /**
     * @param jsonPath JSON 文件路径
     * @param monthKey yyyy-MM 形式（例: 2024-02）
     * @return Double[12]（或你分类数），顺序见 CATEGORIES；无记录处为 0.0
     */
    public static Double[] monthTypeSummary(String jsonPath, String monthKey) {

        final String[] CATEGORIES = {
                "Food", "Housing/Rent", "Daily Necessities", "Transportation",
                "Entertainment", "Shopping", "Healthcare", "Education",
                "Childcare", "Gifts", "Savings", "Others"
        };

        /* 1. 读取指定月份明细 */
        String[][] monthTable = jsonToMonthTable(jsonPath, monthKey);
        if (monthTable.length == 0) {
            return new Double[CATEGORIES.length]; // 全部 null -> 调用侧可视同 0
        }

        /* 2. 汇总到 Map<type, total> */
        Map<String, Double> sum = new HashMap<>();
        for (String[] r : monthTable) {
            sum.merge(r[2], Double.parseDouble(r[1]), Double::sum);
        }

        /* 3. 按固定顺序填值，缺失补 0 */
        Double[] result = new Double[CATEGORIES.length];
        for (int i = 0; i < CATEGORIES.length; i++) {
            result[i] = sum.getOrDefault(CATEGORIES[i], 0.0);
        }
        return result;
    }


}
