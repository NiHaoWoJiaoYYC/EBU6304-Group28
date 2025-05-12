package org.bupt.persosnalfinance.Back.Response;

import java.util.ArrayList;
import java.util.List;

public class BudgetResponse {
    private List<String> alerts = new ArrayList<>();
    private double[] lastQuarterAvg;
    private double[] thisQuarter;

    // Getter 和 Setter 方法
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