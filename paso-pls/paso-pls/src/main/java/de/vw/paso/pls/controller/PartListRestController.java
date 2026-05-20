package de.vw.paso.pls.controller;

import java.time.LocalDate;
import java.util.List;

import de.vw.paso.pls.exception.ErrorCode;
import de.vw.paso.pls.exception.PlsException;
import de.vw.paso.pls.model.dto.ImportStatusDto;
import de.vw.paso.pls.model.dto.PlsPartListDto;
import de.vw.paso.pls.service.PlsService;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = PartListRestController.CONTROLLER_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
public class PartListRestController {

  static final String CONTROLLER_MAPPING = "/partList";
  static final String ENDPOINT_PART_LIST_REQUEST = "/request";
  static final String ENDPOINT_PART_LIST_GET = "/{productDataId}";
  static final String ENDPOINT_IMPORT_STATUS_GET_ALL = "/importStatus";

  //  private static final List<String> PRODUCT_IDS = StaticProductIdReader.read("static_product_ids.txt");

  private final PlsService plsService;

  public PartListRestController(PlsService plsService) {
    this.plsService = plsService;
  }

  /**
   * Request a single vehicle part list and returns the availability status of it.
   *
   * @param productId
   *   the ID of the product
   * @return the status of the requested part list
   */
  @GetMapping(ENDPOINT_PART_LIST_REQUEST)
  public ResponseEntity<ImportStatusDto> requestPartList(@RequestParam("productId") final String productId,
    @RequestParam(value = "importDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate importDate,
    @RequestParam("requester") final String requester) {
    if (productId == null || productId.isEmpty()) {
      throw new PlsException(ErrorCode.INVALID_PRODUCT);
    }

    if (importDate == null) {
      importDate = LocalDate.now();
    }

    ImportStatusDto status = plsService.requestPartList(productId, importDate, requester);
    return ResponseEntity.ok(status);
  }

  /**
   * Return the status list of given import requests.
   *
   * @param productDataIds
   *   the IDs of the import request
   * @return the status of the import request
   */
  @GetMapping(ENDPOINT_IMPORT_STATUS_GET_ALL)
  public ResponseEntity<List<ImportStatusDto>> getPartListStatus(
    @RequestParam("productDataIds") final List<ObjectId> productDataIds) {
    if (productDataIds == null || productDataIds.isEmpty()) {
      throw new PlsException(ErrorCode.INVALID_IMPORT_ID);
    }

    List<ImportStatusDto> importStatuses = productDataIds.stream().map(plsService::getProductDataStatus).toList();
    return ResponseEntity.ok(importStatuses);
  }

  /**
   * Return a single vehicle part list for the given product data ID.
   *
   * @param productDataId
   *   the product data ID of the requested part list
   * @param prNumbers
   *   the prNumbers for specifying criteria for part list creation
   * @param validDate
   *   the valid date of the requested part list
   * @return the single vehicle part list
   */
  @GetMapping(ENDPOINT_PART_LIST_GET)
  public ResponseEntity<PlsPartListDto> createSingleVehiclePartList(
    @PathVariable("productDataId") final ObjectId productDataId,
    @RequestParam(value = "prNumbers", required = false) final String prNumbers,
    @RequestParam("validDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate validDate) {
    if (productDataId == null) {
      throw new PlsException(ErrorCode.INVALID_IMPORT_ID);
    }

    return new ResponseEntity<>(plsService.createSingleVehiclePartList(productDataId, prNumbers, validDate),
      HttpStatus.OK);
  }

}
