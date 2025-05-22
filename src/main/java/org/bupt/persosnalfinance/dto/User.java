package org.bupt.persosnalfinance.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bupt.persosnalfinance.dto.TransactionInformation;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;


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


    /**
     * @param jsonPath "src/main/data/transactionInformation.json"
     *
     * @return String[row][col]  表头顺序：date, amount, type, object, remarks
     */
    public static String[][] jsonToTable(String jsonPath) {

        // 利用 DTO 自带的读文件方法
        TransactionInformation.loadFromJSON(jsonPath);

        List<TransactionInformation> list =
                TransactionInformation.transactionList;

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
}