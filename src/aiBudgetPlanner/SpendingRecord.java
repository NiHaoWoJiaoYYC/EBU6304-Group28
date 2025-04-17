package aiBudgetPlanner;

public class SpendingRecord {
    private String category;
    private double actualSpending;

    public SpendingRecord(String category, double actualSpending) {
        this.category = category;
        this.actualSpending = actualSpending;
    }

    public String getCategory() {
        return category;
    }

    public double getActualSpending() {
        return actualSpending;
    }

    public void setActualSpending(double actualSpending) {
        this.actualSpending = actualSpending;
    }

    @Override
    public String toString() {
        return "SpendingRecord{" +
                "category='" + category + '\'' +
                ", actualSpending=" + actualSpending +
                '}';
    }
}
