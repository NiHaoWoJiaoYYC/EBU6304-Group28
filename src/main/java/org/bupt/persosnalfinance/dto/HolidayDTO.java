package org.bupt.persosnalfinance.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO: 节假日数据传输对象
 * 包含假期名称、开始日期和结束日期
 */
public class HolidayDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;


    public HolidayDTO() {
    }


    public HolidayDTO(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "HolidayDTO{" +
               "name='" + name + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               '}';
    }
}

