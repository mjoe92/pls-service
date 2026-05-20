package de.vw.paso.services.masterdata.salesregion;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import de.vw.paso.consumer.masterdata.salesregion.LoadSalesRegionsConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SalesRegionsTests extends AbstractServiceTests {

  @Autowired
  private LoadSalesRegionsConsumer loadSalesRegionsConsumer;

  @Test
  public void loadSalesRegions() {
    loadSalesRegionsConsumer.loadSalesRegions();

    List<SalesRegionDTO> salesRegions = loadSalesRegionsConsumer.getResult();
    assertTrue(salesRegions.size() >= 8);
  }

}
