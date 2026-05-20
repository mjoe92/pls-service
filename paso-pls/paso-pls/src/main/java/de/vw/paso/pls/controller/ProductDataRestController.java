package de.vw.paso.pls.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.service.ProductDataService;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = ProductDataRestController.CONTROLLER_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductDataRestController {

  static final String CONTROLLER_MAPPING = "/productData";
  static final String ENDPOINT_PRODUCT_DATA_GET = "/{productDataId}";
  static final String ENDPOINT_PRODUCT_DATA_SEARCH = "/search";
  static final String ENDPOINT_FILE_LOCK = "/{productDataId}/fileLock";
  static final String ENDPOINT_FILE_LOCK_DELETE = ENDPOINT_FILE_LOCK + "/{fileLockId}";
  static final String ENDPOINT_FILE_LOCK_LAST_EXPIRY_DATE = ENDPOINT_FILE_LOCK + "/lastExpiryDate";
  static final String ENDPOINT_DELETE_EXPIRED_PRODUCTDATA = "/removeExpiredProductData";
  static final String ENDPOINT_DELETE_ALL = "/clear";

  static final String DEFAULT_PLUS_DAYS = "30";

  private final ProductDataService productDataService;

  public ProductDataRestController(ProductDataService productDataService) {
    this.productDataService = productDataService;
  }

  /**
   * Load a product data by ID.
   *
   * @param productDataId
   *   the ID of the product data
   * @return product data
   */
  @GetMapping(ENDPOINT_PRODUCT_DATA_GET)
  public ResponseEntity<ProductData> getProductData(@PathVariable("productDataId") final ObjectId productDataId) {
    if (productDataId == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(productDataService.findById(productDataId).orElse(null));
  }

  /**
   * Lists every product data by ID.
   *
   * @param productId
   *   product ID of the product data
   * @return product data that matches the param
   */
  @GetMapping(ENDPOINT_PRODUCT_DATA_SEARCH)
  public ResponseEntity<List<ProductData>> listProductData(
    @RequestParam("productId") @Size(min = 3, max = 4) final String productId) {
    if (productId == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(productDataService.loadAllByProductId(productId));
  }

  /**
   * Add new file lock for the specified product data.
   *
   * @param productDataId
   *   ID of the product data
   * @param plusDays
   *   additional days, still the product data have to be valid
   * @return ID of the new file lock
   */
  @PostMapping(ENDPOINT_FILE_LOCK)
  public ResponseEntity<ObjectId> addProductDataFileLock(@PathVariable("productDataId") final ObjectId productDataId,
    @RequestParam(value = "plusDays", required = false, defaultValue = DEFAULT_PLUS_DAYS) final long plusDays) {
    if (productDataId == null) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(productDataService.addFileLock(productDataId, plusDays));
  }

  /**
   * Remove a file lock from the specified product data.
   *
   * @param productDataId
   *   ID of the product data
   * @param fileLockId
   *   ID of the file lock
   */
  @DeleteMapping(ENDPOINT_FILE_LOCK_DELETE)
  public ResponseEntity removeProductDataFileLock(@PathVariable("productDataId") final ObjectId productDataId,
    @PathVariable("fileLockId") final ObjectId fileLockId) {
    if ((productDataId == null) || (fileLockId == null)) {
      return ResponseEntity.badRequest().build();
    }
    productDataService.removeFileLock(productDataId, fileLockId);
    return ResponseEntity.ok().build();
  }

  /**
   * Get the latest expiry date from all file locks which belong to the specified product data.
   *
   * @param productDataId
   *   ID of the product data
   * @return latest expiry date from all file locks
   */
  @GetMapping(ENDPOINT_FILE_LOCK_LAST_EXPIRY_DATE)
  public ResponseEntity<Optional<LocalDateTime>> getFileLockLastExpiryDate(
    @PathVariable("productDataId") final ObjectId productDataId) {

    if (productDataId == null) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(productDataService.getFileLockLastExpiryDate(productDataId));
  }

  @GetMapping(ENDPOINT_DELETE_EXPIRED_PRODUCTDATA)
  public void deleteProductDataWithExpiredLocks() {
    productDataService.deleteProductDataWithExpiredLocks();
  }

  @DeleteMapping(ENDPOINT_DELETE_ALL)
  public void clearProductData() {
    productDataService.deleteAllProductData();
  }
}
