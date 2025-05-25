package org.bupt.persosnalfinance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bupt.persosnalfinance.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCheckOverspending_withRealService() throws Exception {

        double[] lastQuarter = {
                100.0, 800.0, 150.0, 200.0,
                120.0, 300.0, 100.0, 180.0,
                90.0,  60.0,  400.0, 70.0
        };

        double[] thisQuarter = {
                130.0, 880.0, 180.0, 220.0,
                140.0, 350.0, 120.0, 200.0,
                95.0,  70.0,  420.0, 80.0
        };

        User user = new User();
        user.setLastQuarterAvg(lastQuarter);
        user.setThisQuarter(thisQuarter);

        mockMvc.perform(post("/api/budget/check")
                        .param("threshold", "0.05") // 5%
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alerts.length()").value(12))
                .andExpect(jsonPath("$.alerts[0]").value(org.hamcrest.Matchers.containsString("overspent")));
    }
}