package org.bupt.persosnalfinance.Back.Controller;

import org.apache.tomcat.util.buf.UEncoder;
import org.bupt.persosnalfinance.Back.Response.BudgetDataResponse;
import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.bupt.persosnalfinance.Back.Service.BudgetService;
import org.bupt.persosnalfinance.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/check")
    public BudgetResponse checkOverspending(@RequestBody User user,
                                            @RequestParam double threshold) {
        User userT= User.mockUser();
        return budgetService.checkOverspending(userT, threshold);
    }
    @GetMapping("/data")
    public BudgetDataResponse getBudgetData() {
        User mockUser = User.mockUser();
        BudgetDataResponse response = new BudgetDataResponse();

        response.setCategories(new String[]{
                "Food", "Housing/Rent", "Daily Necessities", "Transportation",
                "Entertainment", "Shopping", "Healthcare", "Education",
                "Childcare", "Gifts", "Savings", "Others"
        });

        response.setLastQuarterAvg(mockUser.getLastQuarterAvg());
        response.setThisQuarter(mockUser.getThisQuarter());

        return response;
    }
}