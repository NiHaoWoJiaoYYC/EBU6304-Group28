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

        // 1) Load the custom holiday first and initialise the ID generator
        List<HolidayDTO> persisted = DataStore.loadHolidays();
        userHolidays.addAll(persisted);
        int maxId = userHolidays.stream()
                       .map(HolidayDTO::getId)
                       .filter(Objects::nonNull)
                       .max(Integer::compareTo)
                       .orElse(0);
        idGenerator.set(maxId + 1);

        // 2) Pull and merge API holidays, assign IDs
        loadAndMergeApiHolidays();
    }

    /**
     * Pull the holidays of the month before and after, filter them to [today-1month, today+1month].
     * Merge intervals with the same name and assign unique IDs.
     */
    private void loadAndMergeApiHolidays() {
        apiHolidays.clear();
        LocalDate today      = LocalDate.now();
        LocalDate startRange = today.minusMonths(1);
        LocalDate endRange   = today.plusMonths(1);

        // at the instant sth happens：name -> list of dates
        Map<String, List<LocalDate>> holidayDates = new HashMap<>();

        // Just pull the month before and the month after
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
                    // analysis period
                    String key = entry.getKey(); // "yyyy-MM-dd"
                    LocalDate date = key.matches("\\d{4}-\\d{2}-\\d{2}")
                                     ? LocalDate.parse(key)
                                     : LocalDate.parse(month.getYear() + "-" + key);
                    // Out-of-filter date
                    if (date.isBefore(startRange) || date.isAfter(endRange)) {
                        continue;
                    }
                    // Filtering of "remedial classes"(holiday=false)
                    JsonNode node = entry.getValue();
                    if (node.isObject() && node.has("holiday") && !node.get("holiday").asBoolean()) {
                        continue;
                    }
                    // Parse name
                    String name = node.isTextual() ? node.asText()
                                  : node.has("name") ? node.get("name").asText()
                                  : "";
                    if (name.isBlank()) continue;
                    // collects
                    holidayDates.computeIfAbsent(name, k -> new ArrayList<>()).add(date);
                }
            } catch (Exception ex) {
                System.err.println("加载节假日 API 失败: " + ex.getMessage());
            }
        }

        // Merge with same name, give ID
        for (Map.Entry<String, List<LocalDate>> e : holidayDates.entrySet()) {
            List<LocalDate> dates = e.getValue();
            dates.sort(Comparator.naturalOrder());
            LocalDate start = dates.get(0);
            LocalDate end   = dates.get(dates.size() - 1);
            HolidayDTO dto = new HolidayDTO(e.getKey(), start, end);
            dto.setId(idGenerator.getAndIncrement());
            apiHolidays.add(dto);
        }

        // Ascending order by start date
        apiHolidays.sort(Comparator.comparing(HolidayDTO::getStartDate));
    }

    /** Returns all holidays: API + customisation (each holiday has an ID) */
    public List<HolidayDTO> getAllHolidays() {
        List<HolidayDTO> all = new ArrayList<>(apiHolidays);
        all.addAll(userHolidays);
        return all;
    }
    
    /**
 * Add a custom holiday (with only the day as the start and end) and persist it
 */
public void addCustomHoliday(String name) {
    LocalDate today = LocalDate.now();
    HolidayDTO dto = new HolidayDTO(name, today, today);
    dto.setId(idGenerator.getAndIncrement());
    userHolidays.add(dto);
    DataStore.saveHolidays(userHolidays);
}

/**
 * Update the date range of the holiday (either API Holiday or Custom can be used)
 */
public void updateHolidayDates(HolidayDTO dto, LocalDate start, LocalDate end) {
    if (dto == null) return;
    dto.setStartDate(start);
    dto.setEndDate(end);
    // If it's a custom holiday, do a persistent save

    if (userHolidays.stream().anyMatch(h -> Objects.equals(h.getId(), dto.getId()))) {
        DataStore.saveHolidays(userHolidays);
    }
}

/**
 * Update the holiday name (either API Holiday or Custom can be used)
 */
public void updateHolidayName(HolidayDTO dto, String newName) {
    if (dto == null || newName == null || newName.isBlank()) return;
    dto.setName(newName);
    // If it's a custom holiday, do persistence saving
    if (userHolidays.stream().anyMatch(h -> Objects.equals(h.getId(), dto.getId()))) {
        DataStore.saveHolidays(userHolidays);
    }
}

/**
 * Remove holidays (API holidays are only removed in memory, custom holidays are persisted at the same time)
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
