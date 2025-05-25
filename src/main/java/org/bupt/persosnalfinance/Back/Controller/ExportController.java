package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.ExportService;
import org.bupt.persosnalfinance.dto.ExportRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin
public class ExportController {

    private final ExportService svc;
    public ExportController(ExportService s){ this.svc = s; }

    /** GET /api/csv -> return csv list */
    @GetMapping
    public List<String> list(){ return svc.listCsvFiles(); }

    /** POST /api/csv  {filename:"my"} -> make CSV, return filename */
    @PostMapping
    public String export(@RequestBody ExportRequest req){
        return svc.exportToCsv(req.getFilename());
    }
}
