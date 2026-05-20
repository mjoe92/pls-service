package de.vw.paso.consumer.tiwhimport;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.service.tiwhimport.IImportPartListConsumer;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.tiwhimport.TiWhImportRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportPartListConsumer extends AbstractTestConsumer<TiWhImportDTO> implements IImportPartListConsumer {

  @Autowired
  private TiWhImportRestService tiWhImportService;

  @Override
  public void importPartList(String productKey){
    run(() -> tiWhImportService.importPartList(productKey));
  }

}
