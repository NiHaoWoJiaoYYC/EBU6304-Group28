package aiBudgetPlanner;

public class UserInfo {
    private String occupation;
    private double disposableIncome;
    private String city;
    private String familySituation;
    private int numElderlyToSupport;
    private int numChildren;
    private boolean hasPartner;
    private boolean hasPets;

    public UserInfo(String occupation, double disposableIncome, String city, String familySituation,
                    int numElderlyToSupport, int numChildren, boolean hasPartner, boolean hasPets) {
        this.occupation = occupation;
        this.disposableIncome = disposableIncome;
        this.city = city;
        this.familySituation = familySituation;
        this.numElderlyToSupport = numElderlyToSupport;
        this.numChildren = numChildren;
        this.hasPartner = hasPartner;
        this.hasPets = hasPets;
    }

    public String getOccupation() { return occupation; }
    public double getDisposableIncome() { return disposableIncome; }
    public String getCity() { return city; }
    public String getFamilySituation() { return familySituation; }
    public int getNumElderlyToSupport() { return numElderlyToSupport; }
    public int getNumChildren() { return numChildren; }
    public boolean isHasPartner() { return hasPartner; }
    public boolean isHasPets() { return hasPets; }

    @Override
    public String toString() {
        return "UserInfo{" +
                "occupation='" + occupation + '\'' +
                ", disposableIncome=" + disposableIncome +
                ", city='" + city + '\'' +
                ", familySituation='" + familySituation + '\'' +
                ", numElderlyToSupport=" + numElderlyToSupport +
                ", numChildren=" + numChildren +
                ", hasPartner=" + hasPartner +
                ", hasPets=" + hasPets +
                '}';
    }
}
