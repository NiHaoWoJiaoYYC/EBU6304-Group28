package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.HolidayDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service 层：管理假期数据的加载、查询和更新
 */
public class HolidayService {
    private final List<HolidayDTO> apiHolidays  = new ArrayList<>();
    private final List<HolidayDTO> userHolidays = new ArrayList<>();

    public HolidayService() {
        loadApiHolidays();
    }

    private void loadApiHolidays() {
        apiHolidays.clear();
        apiHolidays.add(new HolidayDTO("New Year's Day",   LocalDate.of(2025,1,1),  LocalDate.of(2025,1,1)));
        apiHolidays.add(new HolidayDTO("Independence Day", LocalDate.of(2025,7,4),  LocalDate.of(2025,7,4)));
        apiHolidays.add(new HolidayDTO("Christmas Day",    LocalDate.of(2025,12,25),LocalDate.of(2025,12,25)));
    }

    public List<HolidayDTO> getAllHolidays() {
        List<HolidayDTO> all = new ArrayList<>(apiHolidays);
        all.addAll(userHolidays);
        return all;
    }

    public void addCustomHoliday(String name) {
        LocalDate today = LocalDate.now();
        userHolidays.add(new HolidayDTO(name, today, today));
    }

    public HolidayDTO findByName(String name) {
        return getAllHolidays().stream()
            .filter(h -> h.getName().equals(name))
            .findFirst().orElse(null);
    }

    public void updateHolidayDates(HolidayDTO dto, LocalDate start, LocalDate end) {
        if (dto != null) {
            dto.setStartDate(start);
            dto.setEndDate(end);
        }
    }
}

