package org.bupt.persosnalfinance.Back.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bupt.persosnalfinance.Util.DataStore;
import org.bupt.persosnalfinance.dto.HolidayDTO;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HolidayService {
    private final RestTemplate restTemplate;
    private final String monthUrlTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<HolidayDTO> apiHolidays;
    private final List<HolidayDTO> userHolidays;
    private final AtomicInteger idGenerator;

    public HolidayService(RestTemplate restTemplate, String monthUrlTemplate) {
        this.restTemplate     = restTemplate;
        this.monthUrlTemplate = monthUrlTemplate;
        this.apiHolidays      = new ArrayList<>();
        this.userHolidays     = new ArrayList<>();
        this.idGenerator      = new AtomicInteger();

        // 1) 先加载自定义假期，并初始化 ID 生成器
        List<HolidayDTO> persisted = DataStore.loadHolidays();
        userHolidays.addAll(persisted);
        int maxId = userHolidays.stream()
                       .map(HolidayDTO::getId)
                       .filter(Objects::nonNull)
                       .max(Integer::compareTo)
                       .orElse(0);
        idGenerator.set(maxId + 1);

        // 2) 拉取并合并 API 假期，赋予 ID
        loadAndMergeApiHolidays();
    }

    /**
     * 拉取前后一个月的节假日，过滤到 [today-1month, today+1month] 之内，
     * 合并同名区间，并赋予唯一 ID。
     */
    private void loadAndMergeApiHolidays() {
        apiHolidays.clear();
        LocalDate today      = LocalDate.now();
        LocalDate startRange = today.minusMonths(1);
        LocalDate endRange   = today.plusMonths(1);

        // 临时：name -> list of dates
        Map<String, List<LocalDate>> holidayDates = new HashMap<>();

        // 只需拉取前一个月和后一个月
        for (int offset = -1; offset <= 1; offset++) {
            LocalDate month = today.plusMonths(offset);
            String url = String.format(monthUrlTemplate,
                                       month.getYear(),
                                       month.getMonthValue());
            try {
                String json = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(json).path("holiday");
                for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    // 解析日期
                    String key = entry.getKey(); // "yyyy-MM-dd"
                    LocalDate date = key.matches("\\d{4}-\\d{2}-\\d{2}")
                                     ? LocalDate.parse(key)
                                     : LocalDate.parse(month.getYear() + "-" + key);
                    // 过滤范围外日期
                    if (date.isBefore(startRange) || date.isAfter(endRange)) {
                        continue;
                    }
                    // 过滤“补班” (holiday=false)
                    JsonNode node = entry.getValue();
                    if (node.isObject() && node.has("holiday") && !node.get("holiday").asBoolean()) {
                        continue;
                    }
                    // 解析名称
                    String name = node.isTextual() ? node.asText()
                                  : node.has("name") ? node.get("name").asText()
                                  : "";
                    if (name.isBlank()) continue;
                    // 收集
                    holidayDates.computeIfAbsent(name, k -> new ArrayList<>()).add(date);
                }
            } catch (Exception ex) {
                System.err.println("加载节假日 API 失败: " + ex.getMessage());
            }
        }

        // 合并同名，赋予 ID
        for (Map.Entry<String, List<LocalDate>> e : holidayDates.entrySet()) {
            List<LocalDate> dates = e.getValue();
            dates.sort(Comparator.naturalOrder());
            LocalDate start = dates.get(0);
            LocalDate end   = dates.get(dates.size() - 1);
            HolidayDTO dto = new HolidayDTO(e.getKey(), start, end);
            dto.setId(idGenerator.getAndIncrement());
            apiHolidays.add(dto);
        }

        // 按开始日期升序
        apiHolidays.sort(Comparator.comparing(HolidayDTO::getStartDate));
    }

    /** 返回所有假期：API + 自定义（每条假期均有 ID） */
    public List<HolidayDTO> getAllHolidays() {
        List<HolidayDTO> all = new ArrayList<>(apiHolidays);
        all.addAll(userHolidays);
        return all;
    }
    
    /**
 * 添加一个自定义假期（只设当天为开始和结束），并持久化
 */
public void addCustomHoliday(String name) {
    LocalDate today = LocalDate.now();
    HolidayDTO dto = new HolidayDTO(name, today, today);
    dto.setId(idGenerator.getAndIncrement());
    userHolidays.add(dto);
    DataStore.saveHolidays(userHolidays);
}

/**
 * 更新假期的日期区间（API 假期或自定义都可）
 */
public void updateHolidayDates(HolidayDTO dto, LocalDate start, LocalDate end) {
    if (dto == null) return;
    dto.setStartDate(start);
    dto.setEndDate(end);
    // 如果是自定义假期，做持久化保存
    if (userHolidays.stream().anyMatch(h -> Objects.equals(h.getId(), dto.getId()))) {
        DataStore.saveHolidays(userHolidays);
    }
}

/**
 * 更新假期名称（API 假期或自定义都可）
 */
public void updateHolidayName(HolidayDTO dto, String newName) {
    if (dto == null || newName == null || newName.isBlank()) return;
    dto.setName(newName);
    // 如果是自定义假期，做持久化保存
    if (userHolidays.stream().anyMatch(h -> Objects.equals(h.getId(), dto.getId()))) {
        DataStore.saveHolidays(userHolidays);
    }
}

/**
 * 删除假期（API 假期只在内存移除，自定义假期同时持久化）
 */
public void removeHoliday(HolidayDTO dto) {
    if (dto == null) return;
    if (userHolidays.removeIf(h -> Objects.equals(h.getId(), dto.getId()))) {
        DataStore.saveHolidays(userHolidays);
    } else {
        apiHolidays.removeIf(h -> Objects.equals(h.getId(), dto.getId()));
    }
}
}
