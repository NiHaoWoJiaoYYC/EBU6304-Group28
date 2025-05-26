package org.bupt.persosnalfinance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.bupt.persosnalfinance.Back.Service.ServiceImpl.ChatServiceImpl;
import org.bupt.persosnalfinance.dto.ChatRequest;
import org.bupt.persosnalfinance.dto.ChatResponse;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceImplTest {

    private static MockWebServer mockLLM;
    private static final Path JSON_FILE =
            Paths.get("src/main/data/transactionInformation.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private ChatServiceImpl chatSvc;

    @BeforeAll
    static void startServer() throws Exception {
        mockLLM = new MockWebServer();
        mockLLM.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        mockLLM.shutdown();
    }

    @BeforeEach
    void setUp() throws Exception {

        /* 1. 写入一条示例交易到 JSON，供 ChatServiceImpl 读取 */
        Files.createDirectories(JSON_FILE.getParent());
        var sample = List.of(
                new TransactionInformation("2024/05/01", 99,
                        "Food","Shop","WX"));
        Files.writeString(JSON_FILE, new Gson().toJson(sample));

        /* 2. 预置 DeepSeek 模拟响应 */
        mockLLM.enqueue(new MockResponse()
                .setHeader("Content-Type","application/json")
                .setBody("""
                   { "choices":[ { "message":{ "content":"Advice" } } ] }
                   """));

        /* 3. 手动 new 实例并用反射注入私有字段 */
        chatSvc = new ChatServiceImpl();
        inject("apiKey", "sk-test");
        inject("baseUrl", mockLLM.url("/chat/completions").toString());
        inject("systemPrompt", "You are advisor.");
    }

    @AfterEach
    void clean() throws Exception { Files.deleteIfExists(JSON_FILE); }

    /* 真实测试 */
    @Test
    void chat_buildsPrompt_callsApi_andReturnsAnswer() throws Exception {

        ChatResponse resp = chatSvc.chat(new ChatRequest("Hi"), "u1");

        var body = mockLLM.takeRequest().getBody().readUtf8();
        Map<?,?> sent = mapper.readValue(body, Map.class);

    }

    /* 工具：反射塞值 */
    private void inject(String field, Object value) {
        try {
            Field f = ChatServiceImpl.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(chatSvc, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
