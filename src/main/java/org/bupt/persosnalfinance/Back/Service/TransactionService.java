package org.bupt.persosnalfinance.Back.Service;

import org.bupt.persosnalfinance.dto.TransactionInformation;

import java.util.List;

public interface TransactionService {
    List<TransactionInformation> getAllTransactions();
}
