package org.bupt.persosnalfinance.Front.AlertFront;

import javafx.beans.property.*;
/**
 * Represents a spending entry for a specific category.
 *
 * Fields:
 * - category: The name of the spending category.
 * - lastQuarterAvg: The average spending for this category in the last quarter.
 * - thisQuarter: The spending amount in the current quarter.
 * - status: Indicates whether the spending is "Normal" or "Overspending" based on a given threshold.
 *
 * Methods:
 * - updateStatus(double threshold): Updates the status field based on the percentage difference between last and current quarter.
 */
public class Spending {
    private final StringProperty category;
    private final DoubleProperty lastQuarterAvg;
    private final DoubleProperty thisQuarter;
    private final StringProperty status;

    public Spending(String category, double lastQuarterAvg, double thisQuarter) {
        this.category       = new SimpleStringProperty(category);
        this.lastQuarterAvg = new SimpleDoubleProperty(lastQuarterAvg);
        this.thisQuarter    = new SimpleDoubleProperty(thisQuarter);
        this.status         = new SimpleStringProperty();
    }

    public void updateStatus(double threshold) {
        double pct = (thisQuarter.get() - lastQuarterAvg.get()) / lastQuarterAvg.get();
        if (pct > threshold) {
            status.set("Overspending   \u2B24");
        } else {
            status.set("Normal   \u2714");
        }
    }

    public StringProperty categoryProperty()       { return category; }
    public DoubleProperty lastQuarterAvgProperty() { return lastQuarterAvg; }
    public DoubleProperty thisQuarterProperty()    { return thisQuarter; }
    public StringProperty statusProperty()         { return status; }
}