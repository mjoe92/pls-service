package de.vw.paso.pls.controller;

import java.util.List;

import de.vw.paso.pls.model.dto.TiWhImportQueueDTO;
import de.vw.paso.pls.service.TiWhImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = TiWhImportController.CONTROLLER_MAPPING)
public class TiWhImportController {

  static final String CONTROLLER_MAPPING = "/tiWhImport";
  static final String ENDPOINT_QUEUE_LIST = "/queue";
  static final String ENDPOINT_VALID_PRODUCT_IDS = "/validProductIds";

  private final TiWhImportService tiWhImportService;

  public TiWhImportController(TiWhImportService tiWhImportService) {
    this.tiWhImportService = tiWhImportService;
  }

  /**
   * List products in queue.
   *
   * @return products in queue
   */
  @GetMapping(value = ENDPOINT_QUEUE_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<TiWhImportQueueDTO>> listQueue() {
    return ResponseEntity.ok(tiWhImportService.getQueue());
  }

  /**
   * Get valid product IDs.
   *
   * @return product IDs.
   */
  @GetMapping(value = ENDPOINT_VALID_PRODUCT_IDS, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getValidProductIds() {
    return ResponseEntity.ok(tiWhImportService.getValidProductIds());
  }

}
