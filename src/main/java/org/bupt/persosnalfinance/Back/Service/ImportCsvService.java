package org.bupt.persosnalfinance.Back.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ImportCsvService {
    /**
     * 解析 CSV 并把所有记录追加到
     * TransactionInformation.transactionList，write back to JSON。
     *
     * @param csvFile document uploaded from frontend
     * @return record numbers of records
     */
    int importCsv(MultipartFile csvFile);
}