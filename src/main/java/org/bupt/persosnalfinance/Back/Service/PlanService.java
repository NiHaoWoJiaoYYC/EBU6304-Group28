package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.PlanDTO;
import org.bupt.persosnalfinance.Util.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Expenditure recording business service: provide data management interface, persist to JSON file, achieve memorability
 */
public class PlanService {
    // In-memory cache, loaded from persistence at startup
    private List<PlanDTO> records;

    public PlanService() {
        // Reading previously saved records from the DataStore
        this.records = new ArrayList<>(DataStore.loadPlans());

        // Assigning unique IDs to legacy ID-less records
        int maxId = records.stream()
                           .filter(p -> p.getId() != null)
                           .mapToInt(PlanDTO::getId)
                           .max()
                           .orElse(0);
        for (PlanDTO p : records) {
            if (p.getId() == null) {
                p.setId(++maxId);
            }
        }
        // Immediate persistence is done once, ensuring that the plans.json has all the legal IDs.
        DataStore.savePlans(records);
    }

    /**
     * Add a new record and persist it, assign non-repeating IDs to DTOs when adding new records
     */
    public void addRecord(PlanDTO dto) {
        // Calculate a new ID that is larger than the maximum of all current IDs
        int newId = records.stream()
                           .mapToInt(p -> p.getId() != null ? p.getId() : 0)
                           .max()
                           .orElse(0) + 1;
        dto.setId(newId);
        records.add(dto);
        DataStore.savePlans(records);
    }

    /**
     * Delete and persist by record ID
     */
    public void removeRecord(int planId) {
        records.removeIf(p -> planId == p.getId());
        DataStore.savePlans(records);
    }

    /**
     * Get a copy of all records
     */
    public List<PlanDTO> getAllRecords() {
        return new ArrayList<>(records);
    }

    /**
     * Filter by holiday ID and return a list of records
     */
    public List<PlanDTO> getAllRecordsForHoliday(Integer holidayId) {
        return getAllRecords().stream()
                              .filter(p -> holidayId.equals(p.getHolidayId()))
                              .collect(Collectors.toList());
    }
}
