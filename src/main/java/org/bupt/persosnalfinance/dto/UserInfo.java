/**
 * DTO representing user information for the budget planner.
 * Contains personal and household details used to generate and adjust budgets.
 */
package org.bupt.persosnalfinance.dto;

/**
 * Holds profile data of a user relevant to budgeting:
 * occupation, disposable income, location, and dependents.
 */
public class UserInfo {
    /** User's occupation or profession. */
    private String occupation;
    /** Total monthly disposable income in CNY. */
    private double disposableIncome;
    /** City where the user lives. */
    private String city;
    /** Number of elderly individuals the user supports. */
    private int numElderlyToSupport;
    /** Number of children the user supports. */
    private int numChildrenToSupport;
    /** Whether the user has a partner in the household. */
    private boolean hasPartner;
    /** Whether the user has pets. */
    private boolean hasPets;

    /**
     * No-argument constructor.
     */
    public UserInfo() { }

    /**
     * All-arguments constructor to initialize all fields.
     *
     * @param occupation             user's occupation
     * @param disposableIncome       user's monthly disposable income
     * @param city                   user's city of residence
     * @param numElderlyToSupport    number of elderly dependents
     * @param numChildrenToSupport   number of child dependents
     * @param hasPartner             true if user has a partner
     * @param hasPets                true if user has pets
     */
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
    /**
     * Sets the user's occupation.
     * @param occupation user's occupation
     */
    public void setOccupation(String occupation) { this.occupation = occupation; }
    /**
     * Sets the user's disposable income.
     * @param disposableIncome monthly disposable income in CNY
     */
    public void setDisposableIncome(double disposableIncome) { this.disposableIncome = disposableIncome; }
    /**
     * Sets the user's city of residence.
     * @param city city name
     */
    public void setCity(String city) { this.city = city; }
    /**
     * Sets the number of elderly dependents.
     * @param numElderlyToSupport number of elderly supported
     */
    public void setNumElderlyToSupport(int numElderlyToSupport) { this.numElderlyToSupport = numElderlyToSupport; }
    /**
     * Sets the number of child dependents.
     * @param numChildrenToSupport number of children supported
     */
    public void setNumChildrenToSupport(int numChildrenToSupport) { this.numChildrenToSupport = numChildrenToSupport; }
    /**
     * Sets whether the user has a partner.
     * @param hasPartner true if user has a partner
     */
    public void setHasPartner(boolean hasPartner) { this.hasPartner = hasPartner; }
    /**
     * Sets whether the user has pets.
     * @param hasPets true if user has pets
     */
    public void setHasPets(boolean hasPets) { this.hasPets = hasPets; }

    // getters
    /**
     * Gets the user's occupation.
     * @return occupation
     */
    public String getOccupation() { return occupation; }
    /**
     * Gets the user's disposable income.
     * @return disposable income
     */
    public double getDisposableIncome() { return disposableIncome; }
    /**
     * Gets the user's city of residence.
     * @return city name
     */
    public String getCity() { return city; }
    /**
     * Gets the count of elderly dependents.
     * @return number of elderly supported
     */
    public int getNumElderlyToSupport() { return numElderlyToSupport; }
    /**
     * Gets the count of child dependents.
     * @return number of children supported
     */
    public int getNumChildrenToSupport() { return numChildrenToSupport; }
    /**
     * Indicates if the user has a partner.
     * @return true if user has a partner
     */
    public boolean isHasPartner() { return hasPartner; }
    /**
     * Indicates if the user has pets.
     * @return true if user has pets
     */
    public boolean isHasPets() { return hasPets; }
}
