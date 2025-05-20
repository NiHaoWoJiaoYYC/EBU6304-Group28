package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import org.bupt.persosnalfinance.Back.Service.TransactionService;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    /** 直接利用 DTO 的静态 JSON 读写方法 */
    @Override
    public List<TransactionInformation> getAllTransactions() {
        // 确保内存列表已加载
        TransactionInformation.loadFromJSON(
                "src/main/data/transactionInformation.json");
        return TransactionInformation.transactionList;
    }
}

