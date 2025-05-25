package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.bupt.persosnalfinance.Back.Service.BudgetService;
import org.bupt.persosnalfinance.dto.User;
import org.springframework.stereotype.Service;
/**
 * BudgetServiceImpl is the implementation of the BudgetService interface.
 *
 * It performs the logic to compare current quarter spending against the previous quarter
 * and generates alerts if the percentage increase exceeds a given threshold.
 *
 * Categories analyzed include:
 * Food, Rent, Daily Necessities, Transportation, Entertainment, etc.
 */
@Service
public class BudgetServiceImpl implements BudgetService {

    private static final String[] CATEGORIES = {
            "Food", "Housing/Rent", "Daily Necessities", "Transportation",
            "Entertainment", "Shopping", "Healthcare", "Education",
            "Childcare", "Gifts", "Savings", "Others"
    };
    /**
     * Compares user spending data between two quarters and generates alerts
     * for categories where the current quarter exceeds the threshold percentage.
     *
     * @param user      User object containing last and current quarter spending.
     * @param threshold The acceptable threshold for spending increase (e.g., 0.18 for 18%).
     * @return          A BudgetResponse object containing overspending alerts and data snapshots.
     */
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