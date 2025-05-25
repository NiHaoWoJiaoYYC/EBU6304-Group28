package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.HolidayService;
import org.bupt.persosnalfinance.Back.Service.PlanService;
import org.bupt.persosnalfinance.dto.HolidayDTO;
import org.bupt.persosnalfinance.dto.PlanDTO;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

public class LocalizationController {
    private static final String HOLIDAY_API_URL_TEMPLATE =
        "https://timor.tech/api/holiday/year/%d-%02d?type=Y";

    private final HolidayService holidayService;
    private final PlanService    planService = new PlanService();

    public LocalizationController() {
        RestTemplate restTemplate = new RestTemplate();
        this.holidayService = new HolidayService(restTemplate, HOLIDAY_API_URL_TEMPLATE);
    }

    /** Get all holidays (each with an ID) */
    public List<HolidayDTO> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    /** Add a custom holiday (name) with automatic ID assignment */
    public void addCustomHoliday(String name) {
        holidayService.addCustomHoliday(name);
    }

    /** Modify the name of the holiday (API or customisation is supported) for immediate effect.*/
    public void updateHolidayName(HolidayDTO dto, String newName) {
        holidayService.updateHolidayName(dto, newName);
    }

    /** Delete a holiday (supported by API or customisation)*/
    public void removeHoliday(HolidayDTO dto) {
        holidayService.removeHoliday(dto);
    }

    /** Modify the holiday date range (API or customisable) for immediate effect. */
    public void updateHolidayDates(HolidayDTO dto, LocalDate start, LocalDate end) {
        holidayService.updateHolidayDates(dto, start, end);
    }

    /** Adding a budget for a particular holiday */
    public void addPlan(Integer holidayId, LocalDate date, String category, String payment, double amt) {
        planService.addRecord(new PlanDTO(holidayId, date, category, payment, amt));
    }

    /** Get all budget records for a given holiday */
    public List<PlanDTO> getPlansForHoliday(Integer holidayId) {
        return planService.getAllRecordsForHoliday(holidayId);
    }

    /** Deletion of individual budget records*/
    public void removePlan(int planId) {
        planService.removeRecord(planId);
    }
}
