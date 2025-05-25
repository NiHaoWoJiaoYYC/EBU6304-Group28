package org.bupt.persosnalfinance.Back.Response;

import java.util.ArrayList;
import java.util.List;

public class BudgetResponse {
    private List<String> alerts = new ArrayList<>();
    private double[] lastQuarterAvg;
    private double[] thisQuarter;

    /**
     * Response object for returning the results of the overspending check.
     *
     * Fields:
     * - alerts: A list of textual alerts for categories where overspending was detected.
     * - lastQuarterAvg: Original data sent back for possible reuse.
     * - thisQuarter: Current data sent back for possible reuse.
     *
     * Purpose:
     * - Communicates alert messages to the frontend when spending exceeds the set threshold.
     */
    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }

    public void addAlert(String alert) {
        this.alerts.add(alert);
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