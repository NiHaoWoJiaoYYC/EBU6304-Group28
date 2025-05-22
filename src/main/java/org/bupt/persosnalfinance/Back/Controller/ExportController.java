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

    /** GET /api/csv -> 返回现有 CSV 文件列表 */
    @GetMapping
    public List<String> list(){ return svc.listCsvFiles(); }

    /** POST /api/csv  {filename:"my"} -> 生成 CSV, 返回文件名 */
    @PostMapping
    public String export(@RequestBody ExportRequest req){
        return svc.exportToCsv(req.getFilename());
    }
}
