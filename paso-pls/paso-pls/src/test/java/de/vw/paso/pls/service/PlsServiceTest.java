package de.vw.paso.pls.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import de.vw.paso.pls.model.ImportStatus;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.model.dto.PlsPartListDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PlsServiceTest {

  @Test
  void testFilteredOutShouldStillCreateRootElement() throws IOException, URISyntaxException {
    ObjectId productDataId = new ObjectId();
    ProductData productData = Mockito.mock(ProductData.class);
    Mockito.when(productData.getId()).thenReturn(productDataId);
    Mockito.when(productData.getImportStatus()).thenReturn(ImportStatus.READY);
    Mockito.when(productData.getFileId()).thenReturn(productDataId);
    Mockito.when(productData.getImportDate()).thenReturn(LocalDate.now());
    Mockito.when(productData.getProductId()).thenReturn("5G0");

    ProductDataService productDataService = Mockito.mock(ProductDataService.class);
    Mockito.when(productDataService.findById(productDataId)).thenReturn(Optional.of(productData));

    byte[] bytes = Files.readAllBytes(Path.of(getClass().getResource("filteredout.zip").toURI()));
    Mockito.when(productDataService.loadProductDataFile(productDataId)).thenReturn(bytes);

    PlsService service = new PlsService(productDataService,null);
    PlsPartListDto dto = service.createSingleVehiclePartList(productDataId,"AAA:BBB", LocalDate.now());
    assertNotNull(dto.rootElement());
    assertEquals("WAGEN", dto.rootElement().getNodeLabel());
  }

}
