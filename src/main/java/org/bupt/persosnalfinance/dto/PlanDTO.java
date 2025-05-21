package org.bupt.persosnalfinance.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 支出记录数据传输对象
 */
public class PlanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private String category;
    private String paymentMethod;
    private double amount;

    public PlanDTO() {}
    public PlanDTO(LocalDate date, String category, String paymentMethod, double amount) {
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "PlanDTO{" +
               "date=" + date +
               ", category='" + category + '\'' +
               ", paymentMethod='" + paymentMethod + '\'' +
               ", amount=" + amount +
               '}';
    }
}