package de.vw.paso.pls.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.vw.paso.pls.AbstractRestControllerTest;
import de.vw.paso.pls.StaticProductIdReader;
import de.vw.paso.pls.model.domain.TiWhImportQueue;
import de.vw.paso.pls.repository.TiWhImportQueueRepository;
import de.vw.paso.pls.service.TiWhImportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

class TiWhImportControllerTest extends AbstractRestControllerTest {

  @Autowired
  private TiWhImportService tiWhImportService;

  @Autowired
  private TiWhImportQueueRepository tiWhImportQueueRepository;

  @Override
  protected String getControllerMapping() {
    return TiWhImportController.CONTROLLER_MAPPING;
  }

  @BeforeEach
  void setUpClass() {
    tiWhImportQueueRepository.deleteAll();
  }

  @Test
  void testListQueue() {
    tiWhImportService.addTiWhRequest("5G0", "testUser");
    tiWhImportService.addTiWhRequest("ASD", "testUser");

    final ResponseEntity<List<TiWhImportQueue>> request = requestList(
      TiWhImportController.ENDPOINT_QUEUE_LIST, new ParameterizedTypeReference<List<TiWhImportQueue>>() {
      }
    );

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check listed queue");
    assertEquals(2, request.getBody().size(), "Check response size");
  }

  @Test
  void testGetValidProductIds() {
    final ResponseEntity<List<String>> request = requestList(
      TiWhImportController.ENDPOINT_VALID_PRODUCT_IDS, new ParameterizedTypeReference<List<String>>() {
      }
    );

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check valid product ids");

    final List<String> productIdsList = StaticProductIdReader.read("static_product_ids.txt");

    assertEquals(productIdsList.size(), request.getBody().size(), "Check response size");
  }
}
