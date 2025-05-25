package org.bupt.persosnalfinance.Back.Response;
/**
 * Response object for returning budget data to the frontend.
 *
 * Fields:
 * - categories: Array of budget categories.
 * - lastQuarterAvg: Spending data for the last quarter.
 * - thisQuarter: Spending data for the current quarter.
 *
 * Purpose:
 * - Supplies data to be used in visual charts and tables in the frontend application.
 */
public class BudgetDataResponse {
    private String[] categories;
    private double[] lastQuarterAvg;
    private double[] thisQuarter;

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

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
}