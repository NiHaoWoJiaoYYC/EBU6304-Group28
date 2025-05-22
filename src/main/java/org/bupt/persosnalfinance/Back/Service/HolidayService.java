package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.Util.DataStore;
import org.bupt.persosnalfinance.dto.HolidayDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Service 层：管理假期数据的加载、查询和更新，具备持久化记忆性
 */
public class HolidayService {
    private final List<HolidayDTO> apiHolidays  = new ArrayList<>();
    private final List<HolidayDTO> userHolidays;
    private final AtomicInteger idGenerator = new AtomicInteger();

    public HolidayService() {
        // 加载 API 假期
        loadApiHolidays();
        // 从本地持久化加载用户自定义假期
        List<HolidayDTO> persisted = DataStore.loadHolidays();
        userHolidays = new ArrayList<>(persisted);
        // 初始化 ID 生成器基于已存在最大 ID
        int maxId = userHolidays.stream()
                       .map(HolidayDTO::getId)
                       .filter(id -> id != null)
                       .max(Integer::compare)
                       .orElse(0);
        idGenerator.set(maxId + 1);
    }

    private void loadApiHolidays() {
        apiHolidays.clear();
        apiHolidays.add(new HolidayDTO("New Year's Day",   LocalDate.of(2025,1,1),  LocalDate.of(2025,1,1)));
        apiHolidays.add(new HolidayDTO("Independence Day", LocalDate.of(2025,7,4),  LocalDate.of(2025,7,4)));
        apiHolidays.add(new HolidayDTO("Christmas Day",    LocalDate.of(2025,12,25),LocalDate.of(2025,12,25)));
    }

    /**
     * 获取所有假期：先是 API 假期，再是用户自定义
     */
    public List<HolidayDTO> getAllHolidays() {
        List<HolidayDTO> all = new ArrayList<>(apiHolidays);
        all.addAll(userHolidays);
        return all;
    }

    /**
     * 添加自定义假期并持久化
     */
    public void addCustomHoliday(String name) {
        LocalDate today = LocalDate.now();
        HolidayDTO dto = new HolidayDTO(name, today, today);
        dto.setId(idGenerator.getAndIncrement());
        userHolidays.add(dto);
        DataStore.saveHolidays(userHolidays);
    }

    /**
     * 根据名称查找假期（包括 API 和自定义）
     */
    public HolidayDTO findByName(String name) {
        return getAllHolidays().stream()
            .filter(h -> h.getName().equals(name))
            .findFirst().orElse(null);
    }

    /**
     * 更新假期日期，若是自定义假期则持久化
     */
    public void updateHolidayDates(HolidayDTO dto, LocalDate start, LocalDate end) {
        if (dto != null) {
            dto.setStartDate(start);
            dto.setEndDate(end);
            // 如果是用户自定义假期，保存
            if (userHolidays.stream().anyMatch(h -> h.getId().equals(dto.getId()))) {
                DataStore.saveHolidays(userHolidays);
            }
        }
    }

    /**
     * 删除自定义假期并持久化
     */
    public void removeCustomHoliday(Integer holidayId) {
        userHolidays.removeIf(h -> h.getId().equals(holidayId));
        DataStore.saveHolidays(userHolidays);
    }
}

