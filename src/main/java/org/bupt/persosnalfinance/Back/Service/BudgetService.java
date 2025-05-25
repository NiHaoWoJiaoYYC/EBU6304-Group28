package org.bupt.persosnalfinance.Back.Service;


import org.bupt.persosnalfinance.dto.User;
import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.springframework.stereotype.Service;
/**
 * BudgetService is a service interface for performing budget-related calculations.
 *
 * Responsibilities:
 * - Define a method for checking overspending based on user data and a threshold.
 */
@Service
public interface BudgetService {
    BudgetResponse checkOverspending(User user, double threshold);
}