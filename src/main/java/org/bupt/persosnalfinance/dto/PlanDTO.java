package org.bupt.persosnalfinance.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Expenditure record data transfer object with added support for Holiday association
 */
public class PlanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;            // primary key
    private Integer holidayId;     // Associated Holiday ID
    private LocalDate date;
    private String category;
    private String paymentMethod;
    private double amount;

    public PlanDTO() {}

    /** This constructor can be used when there is no holiday associated with it. */
    public PlanDTO(LocalDate date, String category, String paymentMethod, double amount) {
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    /** Full constructor with holiday associations */
    public PlanDTO(Integer holidayId, LocalDate date, String category, String paymentMethod, double amount) {
        this.holidayId = holidayId;
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(Integer holidayId) {
        this.holidayId = holidayId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PlanDTO{" +
               "id=" + id +
               ", holidayId=" + holidayId +
               ", date=" + date +
               ", category='" + category + '\'' +
               ", paymentMethod='" + paymentMethod + '\'' +
               ", amount=" + amount +
               '}';
    }
}
