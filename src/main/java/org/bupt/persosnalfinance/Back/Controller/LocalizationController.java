package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.HolidayService;
import org.bupt.persosnalfinance.Back.Service.PlanService;
import org.bupt.persosnalfinance.dto.HolidayDTO;
import org.bupt.persosnalfinance.dto.PlanDTO;

import java.time.LocalDate;
import java.util.List;

public class LocalizationController {
    private final HolidayService holidayService = new HolidayService();
    private final PlanService planService       = new PlanService();

    /** 假期列表，按需要 UI 可直接拿来填 ComboBox */
    public List<HolidayDTO> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    /** 当面板选中某假期、或修改日期时调用 */
    public void updateHolidayDates(HolidayDTO dto, LocalDate start, LocalDate end) {
        holidayService.updateHolidayDates(dto, start, end);
    }

    /** 添加自定义假期 */
    public void addCustomHoliday(String name) {
        holidayService.addCustomHoliday(name);
    }

    /** 支出列表，UI 按假期 ID 调用 */
    public List<PlanDTO> getPlansForHoliday(Integer holidayId) {
        return planService.getAllRecordsForHoliday(holidayId);
    }

    /** 新增预算 */
    public void addPlan(Integer holidayId, LocalDate date, String category, String payment, double amt) {
        PlanDTO p = new PlanDTO(holidayId, date, category, payment, amt);
        planService.addRecord(p);
    }

    /** 删除预算 */
    public void removePlan(int planId) {
        planService.removeRecord(planId);
    }
}
