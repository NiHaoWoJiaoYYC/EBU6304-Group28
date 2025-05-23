package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.bupt.persosnalfinance.Back.Service.BudgetService;
import org.bupt.persosnalfinance.dto.User;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceImpl implements BudgetService {

    private static final String[] CATEGORIES = {
            "Food", "Housing/Rent", "Daily Necessities", "Transportation",
            "Entertainment", "Shopping", "Healthcare", "Education",
            "Childcare", "Gifts", "Savings", "Others"
    };

    @Override
    public BudgetResponse checkOverspending(User user, double threshold) {
        double[] lastQuarterAvg = user.getLastQuarterAvg();
        double[] thisQuarter    = user.getThisQuarter();

        BudgetResponse response = new BudgetResponse();

        for (int i = 0; i < CATEGORIES.length; i++) {
            double diff       = thisQuarter[i] - lastQuarterAvg[i];
            double percentage = diff / lastQuarterAvg[i];
            double pct100     = percentage * 100;
            String formatted  = String.format("%.2f", pct100);

            if (percentage > threshold) {
                response.addAlert(CATEGORIES[i] + " overspent by " + formatted + "%");
            } else {
                response.addAlert(CATEGORIES[i] + " normal");
            }
        }

        response.setLastQuarterAvg(lastQuarterAvg);
        response.setThisQuarter(thisQuarter);
        return response;
    }
}