package org.bupt.persosnalfinance.Back.Controller;

import jakarta.annotation.Resource;
import org.apache.tomcat.util.buf.UEncoder;
import org.bupt.persosnalfinance.Back.Response.BudgetDataResponse;
import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.bupt.persosnalfinance.Back.Service.BudgetService;
import org.bupt.persosnalfinance.Util.Converter2;
import org.bupt.persosnalfinance.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * REST controller for budget-related endpoints.
 *
 * Endpoints:
 * - GET /api/budget/data: Returns category labels and spending data for both quarters.
 * - POST /api/budget/check: Accepts user spending data and a threshold, and returns overspending alerts.
 *
 * Services:
 * - Uses BudgetService for business logic and Converter2 for reading and converting JSON data.
 */

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Resource
    private BudgetService budgetService;


    @PostMapping("/check")
    public BudgetResponse checkOverspending(@RequestBody User user,
                                            @RequestParam double threshold) {

        return budgetService.checkOverspending(user, threshold);
    }
    @GetMapping("/data")
    public BudgetDataResponse getBudgetData() {
        User user= new User();
        Double[] thisQ=Converter2.monthTypeSummary("src/main/data/transactionInformation.json","2024-01");
        double[] thisQuarter=new double[thisQ.length];
        for (int i = 0; i < thisQ.length; i++) {
            thisQuarter[i]=thisQ[i];

        }
        user.setThisQuarter(thisQuarter);


        Double[] lastQ=Converter2.monthTypeSummary("src/main/data/transactionInformation.json","2024-02");
        double[] lastQuarter=new double[thisQ.length];
        for (int i = 0; i < lastQ.length; i++) {
            lastQuarter[i]=lastQ[i];

        }
        user.setLastQuarterAvg(lastQuarter);

        BudgetDataResponse response = new BudgetDataResponse();

        response.setCategories(new String[]{
                "Food", "Housing/Rent", "Daily Necessities", "Transportation",
                "Entertainment", "Shopping", "Healthcare", "Education",
                "Childcare", "Gifts", "Savings", "Others"
        });

        response.setLastQuarterAvg(user.getLastQuarterAvg());
        response.setThisQuarter(user.getThisQuarter());

        return response;
    }
}