package org.bupt.persosnalfinance.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 支出记录数据传输对象，增加了对 Holiday 的关联支持
 */
public class PlanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;            // 主键
    private Integer holidayId;     // 关联的节假日 ID
    private LocalDate date;
    private String category;
    private String paymentMethod;
    private double amount;

    public PlanDTO() {}

    /** 不关联节假日时可使用此构造器 */
    public PlanDTO(LocalDate date, String category, String paymentMethod, double amount) {
        this.date = date;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    /** 完整构造器，包含节假日关联 */
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
