package org.bupt.persosnalfinance.Back.Service.ServiceImpl;

import org.bupt.persosnalfinance.Back.Service.TransactionService;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {


    @Override
    public List<TransactionInformation> getAllTransactions() {
        TransactionInformation.loadFromJSON(
                "src/main/data/transactionInformation.json");
        return TransactionInformation.transactionList;
    }
}

