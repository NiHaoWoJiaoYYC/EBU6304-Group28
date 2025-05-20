package org.bupt.persosnalfinance.Back.Controller;

import org.bupt.persosnalfinance.Back.Service.ImportCsvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importcsv")
@CrossOrigin
public class ImportCsvController {

    private final ImportCsvService service;

    public ImportCsvController(ImportCsvService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Integer> upload(@RequestPart("file") MultipartFile file) {
        int inserted = service.importCsv(file);
        return ResponseEntity.ok(inserted);
    }
}
