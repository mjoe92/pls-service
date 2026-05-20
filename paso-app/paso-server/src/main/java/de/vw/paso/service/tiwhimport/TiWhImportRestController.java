package de.vw.paso.service.tiwhimport;

import java.util.List;

import de.vw.paso.logic.tiwhimport.TiWhImportManager;
import de.vw.paso.status.ImportStatus;
import de.vw.paso.tiwh.domain.TiWhImport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TiWhImportRestService.URL)
public class TiWhImportRestController implements TiWhImportRestService {

    private final TiWhImportManager tiWhImportManager;

    public TiWhImportRestController(TiWhImportManager tiWhImportManager) {
        this.tiWhImportManager = tiWhImportManager;
    }

    @Override
    @GetMapping(path = "/{productKey}")
    public TiWhImportListDTO loadDatenstande(@PathVariable String productKey) {
        List<TiWhImportDTO> tiWhImportDTOS = tiWhImportManager.loadDatenstaende(productKey).stream()
                .map(TiWhImport::convertToTiWhImportDTO).toList();
        return new TiWhImportListDTO(tiWhImportDTOS);
    }

    @Override
    @GetMapping(path = TiWhImportRestService.PART_LIST + "/{productKey}")
    public TiWhImportDTO importPartList(@PathVariable String productKey) {
        return TiWhImport.convertToTiWhImportDTO(tiWhImportManager.importPartList(productKey));
    }

    @Override
    @GetMapping(path = TiWhImportRestService.IMPORT_STATUS + "/{tiWhImportId}")
    public ImportStatus loadImportStatus(@PathVariable Long tiWhImportId) {
        return tiWhImportManager.loadImportStatus(tiWhImportId);
    }

}
