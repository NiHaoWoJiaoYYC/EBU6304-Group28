package aiBudgetPlanner;

public class SpendingRecord {
    private String category;
    private double actualSpending;
    private double aiBudget;  // ✅ 新增字段

    public SpendingRecord(String category, double actualSpending, double aiBudget) {
        this.category = category;
        this.actualSpending = actualSpending;
        this.aiBudget = aiBudget;  // ✅ 赋值
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

    public double getAiBudget() {  // ✅ 新增 getter
        return aiBudget;
    }

    public void setAiBudget(double aiBudget) {  // ✅ 新增 setter
        this.aiBudget = aiBudget;
    }

    @Override
    public String toString() {
        return "SpendingRecord{" +
                "category='" + category + '\'' +
                ", actualSpending=" + actualSpending +
                ", aiBudget=" + aiBudget +
                '}';
    }
}
