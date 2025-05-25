package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.TransactionService;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService svc) {
        this.service = svc;
    }

    /** GET /api/transactions  → response records */
    @GetMapping
    public List<TransactionInformation> all() {
        return service.getAllTransactions();
    }
}
