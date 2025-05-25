package org.bupt.persosnalfinance.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bupt.persosnalfinance.dto.HolidayDTO;
import org.bupt.persosnalfinance.dto.PlanDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Generic data storage tool: save persistent files in the src/main/data directory of the projectcurl -I https://github.com
 */
public class DataStore {
    private static final ObjectMapper M = new ObjectMapper()
        // Registering the Java 8 Time module to support LocalDate serialisation
        .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
        .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // Storage root directory: project root/src/main/data
    private static final Path BASE = Paths.get(System.getProperty("user.dir"), "src", "main", "data");
    private static final String HOLIDAY_FILE = "holidays.json";
    private static final String PLAN_FILE    = "plans.json";

    private static <T> List<T> load(String filename, TypeReference<List<T>> type) {
        try {
            Files.createDirectories(BASE);
            File f = BASE.resolve(filename).toFile();
            if (!f.exists()) return List.of();
            return M.readValue(f, type);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private static <T> void save(String filename, List<T> data) {
        try {
            Files.createDirectories(BASE);
            File f = BASE.resolve(filename).toFile();
            M.writerWithDefaultPrettyPrinter().writeValue(f, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<HolidayDTO> loadHolidays() {
        return load(HOLIDAY_FILE, new TypeReference<>() {});
    }

    public static void saveHolidays(List<HolidayDTO> list) {
        save(HOLIDAY_FILE, list);
    }

    public static List<PlanDTO> loadPlans() {
        return load(PLAN_FILE, new TypeReference<>() {});
    }

    public static void savePlans(List<PlanDTO> list) {
        save(PLAN_FILE, list);
    }
}



