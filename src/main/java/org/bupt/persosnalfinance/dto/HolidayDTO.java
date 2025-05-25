package org.bupt.persosnalfinance.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Holiday data transfer object with unique ID, name and start/end date.
 */
public class HolidayDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;           // unique identification
    private String name;          // Holiday Name
    private LocalDate startDate;  // Start date
    private LocalDate endDate;    // End date

    public HolidayDTO() {}

    /**
     * Constructor: name and date only, no ID required
     */
    public HolidayDTO(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Full constructor: contains the ID
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





