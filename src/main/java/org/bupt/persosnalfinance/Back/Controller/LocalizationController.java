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

    /** 获取所有假期（每条都有 ID） */
    public List<HolidayDTO> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    /** 添加自定义假期（名称），自动分配 ID */
    public void addCustomHoliday(String name) {
        holidayService.addCustomHoliday(name);
    }

    /** 修改节假日名称（API 或自定义都支持），即时生效 */
    public void editHolidayName(HolidayDTO dto, String newName) {
        holidayService.updateHolidayName(dto, newName);
    }

    /** 删除一条假期（API 或自定义都支持） */
    public void deleteHoliday(HolidayDTO dto) {
        holidayService.removeHoliday(dto);
    }

    /** 为某个假期添加预算 */
    public void addPlan(Integer holidayId, LocalDate date, String category, String payment, double amt) {
        planService.addRecord(new PlanDTO(holidayId, date, category, payment, amt));
    }

    /** 获取指定假期的所有预算记录 */
    public List<PlanDTO> getPlansForHoliday(Integer holidayId) {
        return planService.getAllRecordsForHoliday(holidayId);
    }

    /** 删除单条预算记录 */
    public void removePlan(int planId) {
        planService.removeRecord(planId);
    }
}
