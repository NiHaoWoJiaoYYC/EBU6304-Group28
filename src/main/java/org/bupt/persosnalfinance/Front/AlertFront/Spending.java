package org.bupt.persosnalfinance.Front.AlertFront;

import javafx.beans.property.*;

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