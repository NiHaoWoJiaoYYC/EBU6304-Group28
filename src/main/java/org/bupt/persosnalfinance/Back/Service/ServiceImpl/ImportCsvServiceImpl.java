package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.bupt.persosnalfinance.Back.Service.ImportCsvService;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ImportCsvServiceImpl implements ImportCsvService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    public int importCsv(MultipartFile file) {

        int counter = 0;
        try (CSVReader reader =
                     new CSVReader(new InputStreamReader(file.getInputStream()))) {

            String[] header = reader.readNext();        // 跳过表头
            if (header == null) return 0;

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length == 0 || row[0].trim().isEmpty()) break; // 空行终止

                TransactionInformation t = new TransactionInformation(
                        row[0],                               // date 字符串，保持 DTO 现有格式
                        Double.parseDouble(row[1]),           // amount
                        row[2],                               // type
                        row[3],                               // object
                        row.length > 4 ? row[4] : ""          // remarks
                );
                TransactionInformation.addTransaction(t);
                counter++;
            }
        } catch (Exception e) {   // IOException | CsvValidationException
            throw new RuntimeException("CSV 解析失败: " + e.getMessage(), e);
        }

        // write to json
        TransactionInformation.saveToJSON(
                "src/main/data/transactionInformation.json");

        return counter;
    }
}
