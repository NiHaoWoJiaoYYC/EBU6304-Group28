package org.bupt.persosnalfinance.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 节假日数据传输对象，包含唯一 ID、名称及开始/结束日期。
 */
public class HolidayDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;           // 唯一标识
    private String name;          // 假期名称
    private LocalDate startDate;  // 开始日期
    private LocalDate endDate;    // 结束日期

    public HolidayDTO() {}

    /**
     * 构造器：仅名称与日期，无需 ID
     */
    public HolidayDTO(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 完整构造器：包含 ID
     */
    public HolidayDTO(Integer id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
               "id=" + id +
               ", name='" + name + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               '}';
    }
}


