package de.vw.paso.pls.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.vw.paso.pls.model.dto.MbtFileDTO;
import de.vw.paso.pls.service.MbtImportService;

@RestController
@RequestMapping(MbtImportController.MBT_IMPORT_URL)
public class MbtImportController {

    final static String MBT_IMPORT_URL = "/mbt-import";

    private final MbtImportService mbtImportService;

    public MbtImportController(MbtImportService mbtImportService) {
        this.mbtImportService = mbtImportService;
    }

    @GetMapping
    public Collection<MbtFileDTO> getMbtFile() throws Exception {
        return mbtImportService.doImport();
    }
}
