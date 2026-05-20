package de.vw.paso.pls.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.pls.AbstractRestControllerTest;
import de.vw.paso.pls.model.domain.FileLock;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.repository.ProductDataRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

class ProductDataRestControllerTest extends AbstractRestControllerTest {

  @Autowired
  private ProductDataRepository productDataRepository;

  @Override
  protected String getControllerMapping() {
    return ProductDataRestController.CONTROLLER_MAPPING;
  }

  @BeforeEach
  void setUp() {
    productDataRepository.deleteAll();
  }

  @Test
  void testGetProductDataValidProductDataId() {
    final ProductData productData = new ProductData("5G0");
    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());

    final ResponseEntity<ProductData> request = getRequest(ProductDataRestController.ENDPOINT_PRODUCT_DATA_GET,
      ProductData.class, pathVariable);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check valid productDataId");
  }

  @Test
  void testGetProductDataUnknownProductDataId() {
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", new ObjectId().toHexString());

    final ResponseEntity<ProductData> request = getRequest(ProductDataRestController.ENDPOINT_PRODUCT_DATA_GET,
      ProductData.class, pathVariable);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNull(request.getBody(), "Check unknown productDataId");
  }

  @Test
  void testGetProductDataInvalidProductDataId() {
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", "-8");

    final ResponseEntity<ProductData> request = getRequest(ProductDataRestController.ENDPOINT_PRODUCT_DATA_GET,
      ProductData.class, pathVariable);

    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  void testListProductDataValidProductId() {
    final ProductData productData = new ProductData("5G0");

    productDataRepository.save(productData);

    final ResponseEntity<List<ProductData>> request = requestList(
      ProductDataRestController.ENDPOINT_PRODUCT_DATA_SEARCH, new ParameterizedTypeReference<List<ProductData>>() { },
      "productId", "5G0");

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check valid product id");
    assertEquals(1, request.getBody().size());
    assertEquals("5G0", request.getBody().get(0).getProductId());
  }

  @Test
  void testListProductDataUnknownProductId() {
    final ResponseEntity<List<ProductData>> request = requestList(
      ProductDataRestController.ENDPOINT_PRODUCT_DATA_SEARCH, new ParameterizedTypeReference<List<ProductData>>() { },
      "productId", "9999");

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check unknown product id");
    assertEquals(0, request.getBody().size(), "check response size");
  }

  @Test
  void testListProductDataInvalidProductId() {
    final ResponseEntity<Object> request = getRequest(ProductDataRestController.ENDPOINT_PRODUCT_DATA_SEARCH,
      Object.class, "productId", "-8");

    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  void testAddProductDataFileLockValidProductDataId() {
    final ProductData productData = new ProductData("5G0");
    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());

    final ResponseEntity<ObjectId> request = postRequest(null, ProductDataRestController.ENDPOINT_FILE_LOCK,
      ObjectId.class, pathVariable, "plusDays", ProductDataRestController.DEFAULT_PLUS_DAYS);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check valid product data id");
  }

  @Test
  void testAddProductDataFileLockUnknownProductDataId() {
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", new ObjectId().toHexString());

    final ResponseEntity<ObjectId> request = postRequest(null, ProductDataRestController.ENDPOINT_FILE_LOCK,
      ObjectId.class, pathVariable, "plusDays", ProductDataRestController.DEFAULT_PLUS_DAYS);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNull(request.getBody(), "Check unknown product data id");
  }

  @Test
  void testAddProductDataFileLockInvalidProductDataId() {
    final ResponseEntity<ObjectId> request = postRequest(null, ProductDataRestController.ENDPOINT_PRODUCT_DATA_SEARCH,
      ObjectId.class, "productDataId", "-8");

    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  void testRemoveProductDataFileLockValidFileLockId() {
    final ProductData productData = new ProductData("5G0");
    productData.getFileLocks().add(new FileLock());
    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());
    pathVariable.put("fileLockId", savedProductData.getFileLocks().get(0).getId());

    final ResponseEntity<Void> request = deleteRequest(ProductDataRestController.ENDPOINT_FILE_LOCK_DELETE, Void.class,
      pathVariable);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
  }

  @Test
  void testRemoveProductDataFileLockUnknownFileLockId() {
    final ProductData productData = new ProductData("5G0");
    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());
    pathVariable.put("fileLockId", new ObjectId().toHexString());

    final ResponseEntity<Void> request = deleteRequest(ProductDataRestController.ENDPOINT_FILE_LOCK_DELETE, Void.class,
      pathVariable);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNull(request.getBody(), "Check unknown file lock id");
  }

  @Test
  void testRemoveProductDataFileLockInvalidFileLockId() {
    final ProductData productData = new ProductData("5G0");
    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());
    pathVariable.put("fileLockId", "-8");

    final ResponseEntity<Void> request = deleteRequest(ProductDataRestController.ENDPOINT_FILE_LOCK_DELETE, Void.class,
      pathVariable);

    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  void testGetFileLockLastExpiryDateValidProductDataIdWithFileLock() {
    final ProductData productData = new ProductData("5G0");

    productData.getFileLocks().add(new FileLock());
    productData.getFileLocks().add(new FileLock());

    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());

    final ResponseEntity<LocalDateTime> request = getRequest(
      ProductDataRestController.ENDPOINT_FILE_LOCK_LAST_EXPIRY_DATE, LocalDateTime.class, pathVariable);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(request.getBody(), "Check last expiry date");

    LocalDateTime lastExpiryDate = savedProductData.getFileLocks().stream().map(FileLock::getExpiryDate)
      .max(LocalDateTime::compareTo).get();

    // ignore microseconds (may be lost during conversion, precision not required)
    lastExpiryDate = lastExpiryDate.truncatedTo(ChronoUnit.SECONDS);
    LocalDateTime responseDate = request.getBody().truncatedTo(ChronoUnit.SECONDS);

    assertEquals(lastExpiryDate, responseDate, "Check response last expiry date");
  }

  @Test
  void testGetFileLockLastExpiryDateValidProductDataIdWithoutFileLock() {
    final ProductData productData = new ProductData("5G0");
    final ProductData savedProductData = productDataRepository.save(productData);
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", savedProductData.getId());

    final ResponseEntity<Object> request = getRequest(ProductDataRestController.ENDPOINT_FILE_LOCK_LAST_EXPIRY_DATE,
      Object.class, pathVariable);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNull(request.getBody(), "Check last expiry date");
  }

  @Test
  void testGetFileLockLastExpiryDateInvalidProductDataId() {
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", "-8");

    final ResponseEntity<ObjectId> request = getRequest(ProductDataRestController.ENDPOINT_FILE_LOCK_LAST_EXPIRY_DATE,
      ObjectId.class, pathVariable);

    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

}
