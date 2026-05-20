package de.vw.paso.services.tiwhimport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import de.vw.paso.consumer.tiwhimport.LoadDataStatusConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.tiwh.domain.TiWhImport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TiWhImportTests extends AbstractServiceTests {

  @Autowired
  private LoadDataStatusConsumer loadDataStatusConsumer;

  @Test
  public void loadDatenstaende() {
    loadDataStatusConsumer.loadDataStatus("ZZZ");

    List<TiWhImport> tiWhImport = loadDataStatusConsumer.getResult();
    assertTrue(tiWhImport.isEmpty());
  }

  @Test
  public void importPartList() {
    String productKey = "AAA";

    loadDataStatusConsumer.loadDataStatus(productKey);
    int persistedTiWhImportSize = loadDataStatusConsumer.getResult().size();

    TiWhImportDTO tiWhImport = getTiWhImport(productKey);
    assertEquals(productKey, tiWhImport.getProductKey());

    loadDataStatusConsumer.loadDataStatus(productKey);

    assertEquals(persistedTiWhImportSize + 1, loadDataStatusConsumer.getResult().size());
  }

}
