package org.bupt.persosnalfinance.Back.Service;

import com.google.gson.*;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.bupt.persosnalfinance.dto.UserInfo;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BudgetAIService {
    public static final String API_KEY = "sk-d82ebaab7f284198a4a743e416ac184f";
    public static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    /** 1. 根据用户信息构造 Prompt **/
    public static String buildPrompt(UserInfo user) {
        return String.format("""
            Based on the user information below, please create a monthly budget (in CNY) covering these 12 categories:
            Food, Housing/Rent, Daily Necessities, Transportation, Entertainment,
            Shopping, Healthcare, Education, Childcare, Gifts, Savings, Others.

            **Requirements**:
            - The sum of all category budgets must equal the user's disposable income (%.2f CNY).
            - Allocation should reflect the user's situation (occupation, city, number of dependents, etc.).

            User Information:
            Occupation: %s
            Disposable Income: %.2f
            City: %s
            Number of Elderly to Support: %d
            Number of Children to Support: %d
            Has Partner: %s
            Has Pets: %s

            Please return ONLY a JSON object, for example:
            {
              "Food": 1000,
              "Housing/Rent": 2000,
              ...
            }
            """,
                user.getDisposableIncome(),
                user.getOccupation(),
                user.getDisposableIncome(),
                user.getCity(),
                user.getNumElderlyToSupport(),
                user.getNumChildrenToSupport(),
                user.isHasPartner(),
                user.isHasPets()
        );
    }

    /** 2. 调用 AI 接口，得到预算 **/
    public static Map<String, Double> generateBudget(UserInfo user) {
        Map<String, Double> result = new HashMap<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

            JsonObject root = new JsonObject();
            root.addProperty("model", "qwen-turbo");
            JsonObject input = new JsonObject();
            input.addProperty("prompt", buildPrompt(user));
            root.add("input", input);
            root.addProperty("result_format", "text");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(root.toString().getBytes());
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JsonObject resp = JsonParser.parseString(sb.toString()).getAsJsonObject();
                String content = resp.getAsJsonObject("output").get("text").getAsString();
                int s = content.indexOf("{"), e = content.lastIndexOf("}");
                if (s >= 0 && e > s) {
                    JsonObject bj = JsonParser.parseString(content.substring(s, e + 1)).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> en : bj.entrySet()) {
                        double value = Math.round(en.getValue().getAsDouble() * 10) / 10.0;
                        result.put(en.getKey(), value);
                    }
                    // 确保总和 = 可支配收入
                    double total = result.values().stream().mapToDouble(Double::doubleValue).sum();
                    double disposableIncome = user.getDisposableIncome();
                    if (total > disposableIncome) {
                        double scale = disposableIncome / total;
                        result.replaceAll((k, v) -> Math.round(v * scale * 10) / 10.0);
                    } else if (total < disposableIncome) {
                        double remaining = Math.round((disposableIncome - total) * 10) / 10.0;
                        result.put("Savings", result.getOrDefault("Savings", 0.0) + remaining);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** 3. 读取 JSON，统计“本月”实际支出 **/
    public static Map<String, Double> getActualSpendingFromJson(String filePath) {
        Map<String, Double> spending = new HashMap<>();
        TransactionInformation.loadFromJSON(filePath);
        List<TransactionInformation> recs = TransactionInformation.transactionList;

        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M"));
        for (TransactionInformation r : recs) {
            String date = r.getDate().replace('/', '-');
            String[] parts = date.split("-");
            String ym = parts[0].length() == 4
                    ? parts[0] + "-" + Integer.parseInt(parts[1])
                    : parts[2] + "-" + Integer.parseInt(parts[0]);
            if (!ym.equals(thisMonth)) continue;
            String type = r.getType();
            if (type != null) {
                String cat = type.contains(":") ? type.split(":", 2)[1] : type;
                spending.put(cat, spending.getOrDefault(cat, 0.0) + r.getAmount());
            }
        }
        return spending;
    }

    /** 4. 根据用户信息 + 本月实际支出，让 AI 给出文字建议 **/
    public static String generateSuggestion(UserInfo user, Map<String, Double> actual) {
        StringBuilder sb = new StringBuilder();
        sb.append("Based on the user information and the actual spending below, ");
        sb.append("please give a concise suggestion to help them manage their budget:\n\n");
        sb.append("User Info:\n");
        sb.append(String.format("- Occupation: %s\n", user.getOccupation()));
        sb.append(String.format("- Disposable Income: %.2f\n", user.getDisposableIncome()));
        sb.append(String.format("- City: %s\n", user.getCity()));
        sb.append(String.format("- Elderly to Support: %d\n", user.getNumElderlyToSupport()));
        sb.append(String.format("- Children to Support: %d\n", user.getNumChildrenToSupport()));
        sb.append(String.format("- Has Partner: %s\n", user.isHasPartner()));
        sb.append(String.format("- Has Pets: %s\n\n", user.isHasPets()));
        sb.append("Actual Spending This Month:\n");
        actual.forEach((cat, amt) ->
                sb.append(String.format("- %s: %.2f\n", cat, amt))
        );
        sb.append("\nPlease respond with a single paragraph in English.");

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

            JsonObject root = new JsonObject();
            root.addProperty("model", "qwen-turbo");
            JsonObject input = new JsonObject();
            input.addProperty("prompt", sb.toString());
            root.add("input", input);
            root.addProperty("result_format", "text");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(root.toString().getBytes());
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder respSb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    respSb.append(line);
                }
                JsonObject resp = JsonParser.parseString(respSb.toString()).getAsJsonObject();
                return resp.getAsJsonObject("output").get("text").getAsString().trim();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get suggestion from AI.";
        }
    }
}
