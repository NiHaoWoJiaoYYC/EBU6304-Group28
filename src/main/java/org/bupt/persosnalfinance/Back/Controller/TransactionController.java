package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.TransactionService;
import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin      // Swing / JavaFX 本地调用需要
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService svc) {
        this.service = svc;
    }

    /** GET /api/transactions  → 返回所有记录 */
    @GetMapping
    public List<TransactionInformation> all() {
        return service.getAllTransactions();
    }
}
