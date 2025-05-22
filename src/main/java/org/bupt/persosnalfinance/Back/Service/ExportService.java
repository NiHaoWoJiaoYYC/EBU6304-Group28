package org.bupt.persosnalfinance.Back.Service;

import java.util.List;

public interface ExportService {

    /** 返回 data/transactionCSV 目录下所有 CSV 文件名（含 .csv） */
    List<String> listCsvFiles();

    /**
     * 把 transactionInformation.json 转为 CSV 并保存.
     * @param filename 不含扩展名，如 "myData" -> 生成 myData.csv
     * @return 完整文件名（含 .csv）
     */
    String exportToCsv(String filename);
}
