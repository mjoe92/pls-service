package de.vw.paso.consumer.tiwhimport;

import java.util.List;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.service.tiwhimport.ILoadDataStatusConsumer;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.tiwhimport.TiWhImportRestService;
import de.vw.paso.tiwh.domain.TiWhImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadDataStatusConsumer extends AbstractTestConsumer<List<TiWhImport>> implements ILoadDataStatusConsumer {

    @Autowired
    private TiWhImportRestService tiWhImportService;

    @Override
    public void loadDataStatus(String productKey) {
        run(() -> (tiWhImportService.loadDatenstande(productKey).tiWhImportDTOList()).stream().map(this::toEntity)
                .toList());
    }

    private TiWhImport toEntity(TiWhImportDTO tiWhImportDTO) {
        TiWhImport tiWhImport = new TiWhImport();
        tiWhImport.setId(tiWhImportDTO.getId());
        tiWhImport.setProductKey(tiWhImportDTO.getProductKey());
        tiWhImport.setImportStatus(tiWhImportDTO.getImportStatus());
        tiWhImport.setTimestampChange(tiWhImportDTO.getTimestampChange());

        return tiWhImport;
    }
}
