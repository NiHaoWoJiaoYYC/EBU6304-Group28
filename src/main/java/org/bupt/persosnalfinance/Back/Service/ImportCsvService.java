package org.bupt.persosnalfinance.Back.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ImportCsvService {
    /**
     * 解析 CSV 并把所有记录追加到
     * TransactionInformation.transactionList，再写回 JSON。
     *
     * @param csvFile 前端上传的文件
     * @return 成功写入的记录条数
     */
    int importCsv(MultipartFile csvFile);
}