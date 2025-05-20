package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.bupt.persosnalfinance.Back.Service.ChatService;
import org.bupt.persosnalfinance.dto.ChatRequest;
import org.bupt.persosnalfinance.dto.ChatResponse;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {

    @Value("${ai.deepseek.api-key}")  private String apiKey;
    @Value("${ai.deepseek.base-url}") private String baseUrl;
    @Value("${ai.prompt.system}")     private String systemPrompt;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new Gson();
    private final Map<String,List<Map<String,String>>> history = new HashMap<>();

    private static final String FILE =
            "src/main/data/transactionInformation.json";

    @Override
    public ChatResponse chat(ChatRequest req, String uid) {

        /* =========== 准备对话历史 =========== */
        List<Map<String,String>> msgs = history
                .computeIfAbsent(uid, k -> new ArrayList<>());

        if (msgs.isEmpty()) {
            TransactionInformation.loadFromJSON(FILE);
            String json = gson.toJson(TransactionInformation.transactionList);

            msgs.add(Map.of("role","system",
                    "content", systemPrompt +
                            "\n\nHere is user's full transaction JSON:\n" + json));
        }

        msgs.add(Map.of("role","user", "content", req.getMessage()));

        /* =========== DeepSeek 调用 =========== */
        Map<String,Object> payload = new HashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("messages", msgs);
        payload.put("stream", false);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setEntity(new StringEntity(mapper.writeValueAsString(payload),
                    StandardCharsets.UTF_8));

            try (CloseableHttpResponse resp = client.execute(post)) {
                JsonNode root = mapper.readTree(resp.getEntity().getContent());
                String answer = root.path("choices").path(0)
                        .path("message").path("content").asText();
                msgs.add(Map.of("role","assistant","content",answer));
                return new ChatResponse(answer);
            }
        } catch (Exception e) {
            return new ChatResponse("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public List<String> getDialog(String uid) {
        return history.getOrDefault(uid, List.of())
                .stream().map(m -> m.get("role")+": "+m.get("content")).toList();
    }
}