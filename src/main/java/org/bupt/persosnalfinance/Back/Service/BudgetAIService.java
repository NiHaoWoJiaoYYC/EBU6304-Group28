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
            根据以下用户信息，为他们制定月度预算（单位：人民币元），包括以下12个类别：
            Food, Housing/Rent, Daily Necessities, Transportation, Entertainment,
            Shopping, Healthcare, Education, Childcare, Gifts, Savings, Others.

            **要求**：
            - 所有类别的预算总和必须等于用户的当月可支配收入（%.2f元）。
            - 预算分配需符合用户的生活场景（职业、城市、家庭负担等）。

            用户信息如下：
            Occupation: %s
            Disposable Income: %.2f
            City: %s
            Number of Elderly to Support: %d
            Number of Children to Support: %d
            Has Partner: %s
            Has Pets: %s

            请只返回 JSON 格式：
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

                    // 调整总和
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

    /** 3. 读取 JSON，统计本月实际支出 **/
    public static Map<String, Double> getActualSpendingFromJson(String filePath) {
        Map<String, Double> spending = new HashMap<>();
        TransactionInformation.loadFromJSON(filePath);
        List<TransactionInformation> recs = TransactionInformation.transactionList;

        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M"));
        for (TransactionInformation r : recs) {
            String date = r.getDate().replace('/', '-');
            String[] parts = date.split("-");
            String ym;
            if (parts[0].length() == 4) {
                ym = parts[0] + "-" + Integer.parseInt(parts[1]);
            } else {
                ym = parts[2] + "-" + Integer.parseInt(parts[0]);
            }
            if (!ym.equals(thisMonth)) continue;
            String type = r.getType();
            if (type != null) {
                String cat = type.contains(":") ? type.split(":", 2)[1] : type;
                spending.put(cat, spending.getOrDefault(cat, 0.0) + r.getAmount());
            }
        }
        return spending;
    }
}
