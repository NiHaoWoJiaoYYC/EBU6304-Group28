package org.bupt.persosnalfinance.Back.Service;

import java.util.List;

public interface ExportService {

    /** Response data/transactionCSV  ALL CSV */
    List<String> listCsvFiles();

    /**
     *  transform transactionInformation.json into CSV and save.
     * @param filename no expansion，e.g. "myData" ->  myData.csv
     * @return Full document（.csv）
     */
    String exportToCsv(String filename);
}
