package org.bupt.persosnalfinance.dto;

public class UserInfo {
    private String occupation;
    private double disposableIncome;
    private String city;
    private int numElderlyToSupport;
    private int numChildrenToSupport;
    private boolean hasPartner;
    private boolean hasPets;

    public UserInfo() { }

    public UserInfo(String occupation,
                    double disposableIncome,
                    String city,
                    int numElderlyToSupport,
                    int numChildrenToSupport,
                    boolean hasPartner,
                    boolean hasPets) {
        this.occupation = occupation;
        this.disposableIncome = disposableIncome;
        this.city = city;
        this.numElderlyToSupport = numElderlyToSupport;
        this.numChildrenToSupport = numChildrenToSupport;
        this.hasPartner = hasPartner;
        this.hasPets = hasPets;
    }

    // setters
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public void setDisposableIncome(double disposableIncome) { this.disposableIncome = disposableIncome; }
    public void setCity(String city) { this.city = city; }
    public void setNumElderlyToSupport(int numElderlyToSupport) { this.numElderlyToSupport = numElderlyToSupport; }
    public void setNumChildrenToSupport(int numChildrenToSupport) { this.numChildrenToSupport = numChildrenToSupport; }
    public void setHasPartner(boolean hasPartner) { this.hasPartner = hasPartner; }
    public void setHasPets(boolean hasPets) { this.hasPets = hasPets; }

    // getters
    public String getOccupation() { return occupation; }
    public double getDisposableIncome() { return disposableIncome; }
    public String getCity() { return city; }
    public int getNumElderlyToSupport() { return numElderlyToSupport; }
    public int getNumChildrenToSupport() { return numChildrenToSupport; }
    public boolean isHasPartner() { return hasPartner; }
    public boolean isHasPets() { return hasPets; }
}
