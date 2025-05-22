package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import com.opencsv.CSVWriter;
import org.bupt.persosnalfinance.Back.Service.ExportService;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private static final Path DATA_DIR  = Paths.get("src/main/data");
    private static final Path JSON_SRC  = DATA_DIR.resolve("transactionInformation.json");
    private static final Path CSV_DIR   = DATA_DIR.resolve("transactionCSV");

    private static final String[] HEADER =
            {"date","amount","type","object","remarks"};

    static {
        // 确保目录存在
        try { Files.createDirectories(CSV_DIR); } catch (Exception ignored) {}
    }

    @Override
    public List<String> listCsvFiles() {
        List<String> list = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(CSV_DIR, "*.csv")) {
            ds.forEach(p -> list.add(p.getFileName().toString()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public String exportToCsv(String filename) {

        if (filename == null || filename.isBlank())
            filename = "export_" + System.currentTimeMillis();

        String full = filename.endsWith(".csv") ? filename : filename + ".csv";
        Path target = CSV_DIR.resolve(full);

        // 读 JSON -> 内存列表
        TransactionInformation.loadFromJSON(JSON_SRC.toString());
        List<TransactionInformation> list = TransactionInformation.transactionList;

        // 写 CSV
        try (CSVWriter writer = new CSVWriter(new FileWriter(target.toFile()))) {
            writer.writeNext(HEADER);
            for (TransactionInformation t : list) {
                writer.writeNext(new String[]{
                        t.getDate(),
                        String.valueOf(t.getAmount()),
                        t.getType(),
                        t.getObject(),
                        t.getRemarks()
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Export CSV failed: " + e.getMessage(), e);
        }
        return full;
    }
}

