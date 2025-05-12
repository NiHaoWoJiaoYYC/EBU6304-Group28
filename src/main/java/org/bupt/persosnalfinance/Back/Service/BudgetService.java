package org.bupt.persosnalfinance.Back.Service;


import org.bupt.persosnalfinance.dto.User;
import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.springframework.stereotype.Service;

@Service
public interface BudgetService {
    BudgetResponse checkOverspending(User user, double threshold);
}