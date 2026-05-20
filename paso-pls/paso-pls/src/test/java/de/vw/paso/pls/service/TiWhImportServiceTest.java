package de.vw.paso.pls.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.vw.paso.pls.PlsApplicationTests;
import de.vw.paso.pls.model.domain.TiWhImportQueue;
import de.vw.paso.pls.repository.TiWhImportQueueRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TiWhImportServiceTest extends PlsApplicationTests {

  @Autowired
  private TiWhImportService tiWhImportService;

  @Autowired
  private TiWhImportQueueRepository tiWhImportQueueRepository;

  @BeforeEach
  void setUp() {
    tiWhImportQueueRepository.deleteAll();
  }

  @Test
  void testOfferRequest() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");

    TiWhImportQueue result = tiWhImportQueueRepository.findAll().getFirst();

    assertNotNull(result, "Check offer request");
    assertTrue(result.getRequesterIds().contains("testUser1"), "Check offer request requester");
    assertEquals("5G0", result.getProductId(), "Check offer request product id");
  }

  @Test
  void testOfferRequestWithDifferentProductAndWithSameRequester() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");
    tiWhImportService.addTiWhRequest("ASD", "testUser1");

    final List<TiWhImportQueue> result = tiWhImportQueueRepository.findAll();

    assertNotNull(result, "Check offer request");
    assertEquals(2, result.size(), "Check offer request size");
    assertEquals(1, result.get(0).getRequesterIds().size(), "Check offer request requester ids size");
  }

  @Test
  void testOfferRequestWithSameProductAndMultipleRequester() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");
    tiWhImportService.addTiWhRequest("5G0", "testUser2");

    final List<TiWhImportQueue> result = tiWhImportQueueRepository.findAll();

    assertNotNull(result, "Check offer request");
    assertEquals(1, result.size(), "Check offer request size");
    assertEquals(2, result.get(0).getRequesterIds().size(), "Check offer request requester ids size");
  }

  @Test
  void testOfferRequestWithTwoSameRequest() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");
    tiWhImportService.addTiWhRequest("5G0", "testUser1");

    final List<TiWhImportQueue> result = tiWhImportQueueRepository.findAll();

    assertNotNull(result, "Check offer request");
    assertEquals(1, result.size(), "Check offer request size");
    assertEquals(1, result.get(0).getRequesterIds().size(), "Check offer request requester ids size");
  }

  @Test
  void testPeekRequestWithDifferentRequestCount() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");
    tiWhImportService.addTiWhRequest("ASD", "testUser1");
    tiWhImportService.addTiWhRequest("ASD", "testUser2");

    final TiWhImportQueue test = tiWhImportService.getNextTiWhRequest();

    assertNotNull(test, "Check peek request");
    assertEquals("ASD", test.getProductId(), "Check peek request result");
    assertNotNull(test.getRequesterIds(), "Check result requester ids");
    assertEquals(2, test.getRequesterIds().size(), "Check result requester ids count");
  }

  @Test
  void testPeekRequestWithSameRequestCount() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");
    tiWhImportService.addTiWhRequest("5G0", "testUser2");
    tiWhImportService.addTiWhRequest("ASD", "testUser1");
    tiWhImportService.addTiWhRequest("ASD", "testUser2");

    final TiWhImportQueue test = tiWhImportService.getNextTiWhRequest();

    assertNotNull(test, "Check peek request");
    assertEquals("5G0", test.getProductId(), "Check peek request result");
    assertNotNull(test.getRequesterIds(), "Check result requester ids");
    assertEquals(2, test.getRequesterIds().size(), "Check result requester ids count");
  }

  @Test
  void testPeekRequestWithMultipleProducts() {
    tiWhImportService.addTiWhRequest("5G0", "testUser1");
    tiWhImportService.addTiWhRequest("ASD", "testUser1");
    tiWhImportService.addTiWhRequest("6XD", "testUser1");
    tiWhImportService.addTiWhRequest("6XD", "testUser2");
    tiWhImportService.addTiWhRequest("ASD", "testUser2");
    tiWhImportService.addTiWhRequest("ASD", "testUser3");
    tiWhImportService.addTiWhRequest("ZZZ", "testUser1");
    tiWhImportService.addTiWhRequest("5G0", "testUser2");

    final TiWhImportQueue test = tiWhImportService.getNextTiWhRequest();

    assertNotNull(test, "Check peek request");
    assertEquals("ASD", test.getProductId(), "Check peek request result");
    assertNotNull(test.getRequesterIds(), "Check result requester ids");
    assertEquals(3, test.getRequesterIds().size(), "Check result requester ids count");
  }
}
