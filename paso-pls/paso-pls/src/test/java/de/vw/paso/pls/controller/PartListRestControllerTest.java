package de.vw.paso.pls.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.vw.paso.pls.AbstractRestControllerTest;
import de.vw.paso.pls.model.ImportStatus;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.model.dto.ImportStatusDto;
import de.vw.paso.pls.model.dto.PlsPartListDto;
import de.vw.paso.pls.repository.GridFsRepository;
import de.vw.paso.pls.repository.ProductDataRepository;
import de.vw.paso.pls.service.TiWhImportService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

class PartListRestControllerTest extends AbstractRestControllerTest {

  @Autowired
  private ProductDataRepository productDataRepository;

  @Autowired
  private GridFsRepository gridFsRepository;

  @Autowired
  private TiWhImportService tiWhImportService;

  @Override
  protected String getControllerMapping() {
    return PartListRestController.CONTROLLER_MAPPING;
  }

  @BeforeEach
  void setUp() {
    productDataRepository.deleteAll();
    tiWhImportService.deleteTiWhFiles();
    tiWhImportService.deleteTiWhFiles();
  }

  @Test
  void testRequestInvalidProductID() {
    ResponseEntity<String> request = getRequest(PartListRestController.ENDPOINT_PART_LIST_REQUEST, String.class,
      "productId", "1", "importDate", "2011-11-11");
    assertNotNull(request);
    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  void testRequestValidProductID() {

    final ProductData productData = new ProductData("5G0");

    productData.setImportStatus(ImportStatus.READY);

    productDataRepository.save(productData);

    ResponseEntity<ImportStatusDto> request = getRequest(PartListRestController.ENDPOINT_PART_LIST_REQUEST,
      ImportStatusDto.class, "productId", "5G0", "importDate", LocalDate.now().toString(), "requester", "testUser");
    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");

    ImportStatusDto sr = request.getBody();
    assertNotNull(sr);
    assertNotNull(sr.productDataId(), "Check valid importId");
    assertEquals(ImportStatus.READY, sr.status(), "Check status");

  }

  //  @Test
  //  void testRequestValidProductID() {
  //    ResponseEntity<ImportStatusDto> request = request(PartListRestController.ENDPOINT_PART_LIST_REQUEST, ImportStatusDto.class,
  //      "prNumber", "wer;abc;poi",
  //      "product", "5G0",
  //      "date", "11-11-2011");
  //    assertTrue("Check response status", request.getStatusCode().is2xxSuccessful());
  //
  //    ImportStatusDto sr = request.getBody();
  //    assertNotNull("Check valid importId", sr.getProductDataId());
  //    assertEquals("Check status", ImportStatus.READY, sr.getStatus());
  //
  //  }
  //@Test
  //void testRequestValidProductIDPartListNotReady() {
  //  ResponseEntity<ImportStatusDto> request = request(PartListRestController.ENDPOINT_PART_LIST_REQUEST, ImportStatusDto.class,
  //    "prNumber", "wer;abc;poi",
  //    "product", "60D",
  //    "date", "11-11-2011");
  //  assertTrue("Check response status", request.getStatusCode().is2xxSuccessful());
  //
  //  ImportStatusDto sr = request.getBody();
  //  assertNotNull("Check valid importId", sr.getProductDataId());
  //  assertEquals("Check status", ImportStatus.PENDING, sr.getStatus());
  //
  //}

  @Test
  void testRequestValidProductIDPartListNotReady() {
    ResponseEntity<ImportStatusDto> request = getRequest(PartListRestController.ENDPOINT_PART_LIST_REQUEST,
      ImportStatusDto.class, "productId", "5G0", "importDate", "2011-11-11", "requester", "testUser");
    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");

    ImportStatusDto sr = request.getBody();
    assertNotNull(sr);
    assertNotNull(sr.productDataId(), "Check valid importId");
    assertEquals(ImportStatus.PENDING, sr.status(), "Check status");
  }

  @Test
  void testMultiStatusInvalidImportId() {
    ResponseEntity<String> request = getRequest(PartListRestController.ENDPOINT_IMPORT_STATUS_GET_ALL, String.class,
      "productDataId", "1", "productDataId", "x");
    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  void testMultiStatusUnknownImportId() {
    ResponseEntity<List<ImportStatusDto>> request = requestList(PartListRestController.ENDPOINT_IMPORT_STATUS_GET_ALL,
      new ParameterizedTypeReference<>() { }, "productDataId", new ObjectId().toHexString(), "productDataId",
      new ObjectId().toHexString(), "productDataId", new ObjectId().toHexString(), "productDataId",
      new ObjectId().toHexString(), "productDataId", new ObjectId().toHexString());
    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");
    List<ImportStatusDto> response = request.getBody();
    assertNotNull(response);
    assertEquals(5, response.size(), "check response size");
    response.forEach(e -> assertEquals(ImportStatus.UNKNOWN, e.status(), "Check status"));
  }

  /**
   * Fix this after devstack and part list creation works normal again
   */
  @Test
  @Disabled
  void testMultiStatusValidImportId() {
    final ProductData productData = new ProductData("5G0");

    productData.setImportStatus(ImportStatus.READY);

    productDataRepository.save(productData);

    ResponseEntity<ImportStatusDto> pendingRequest = getRequest(PartListRestController.ENDPOINT_PART_LIST_REQUEST,
      ImportStatusDto.class, "productId", "5G0", "importDate", "2011-11-11", "requester", "testUser");
    HttpStatusCode statusCode = pendingRequest.getStatusCode();
    assertTrue(pendingRequest.getStatusCode().is2xxSuccessful(), "Check response status for pendingRequest");
    assertNotNull(pendingRequest.getBody());
    ObjectId pendingId = pendingRequest.getBody().productDataId();

    ResponseEntity<ImportStatusDto> readyRequest = getRequest(PartListRestController.ENDPOINT_PART_LIST_REQUEST,
      ImportStatusDto.class, "productId", "5G0", "importDate", "2011-11-12", "requester", "testUser");
    assertTrue(statusCode.is2xxSuccessful(), "Check response status for readyRequest");
    assertNotNull(readyRequest.getBody());
    ObjectId pendingId2 = readyRequest.getBody().productDataId();

    ResponseEntity<List<ImportStatusDto>> statusRequest = requestList(
      PartListRestController.ENDPOINT_IMPORT_STATUS_GET_ALL,
      new ParameterizedTypeReference<List<ImportStatusDto>>() { }, "productDataId", pendingId2.toHexString(),
      "productDataId", pendingId.toHexString());
    assertTrue(statusRequest.getStatusCode().is2xxSuccessful(), "Check response status");
    List<ImportStatusDto> response = statusRequest.getBody();
    assertNotNull(response);
    assertEquals(2, response.size(), "check response size");

    ImportStatusDto importStatusDtoReady = response.stream().filter(e -> e.productDataId().equals(pendingId2))
      .findFirst().orElse(null);
    assertNotNull(importStatusDtoReady, "Check existence of response for ready request");
    assertEquals(ImportStatus.PENDING, importStatusDtoReady.status(), "check status of ready request");

    ImportStatusDto importStatusDtoPending = response.stream().filter(e -> e.productDataId().equals(pendingId))
      .findFirst().orElse(null);
    assertNotNull(importStatusDtoPending, "Check existence of response for ready request");
    assertEquals(ImportStatus.PENDING, importStatusDtoPending.status(), "check status of ready request");
  }

  @Test
  void testCreateInvalidImportId() {
    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", "-8");

    ResponseEntity<String> request = getRequest(PartListRestController.ENDPOINT_PART_LIST_GET, String.class,
      pathVariable, "prNumbers", "AAA;BBB", "validDate", "2020-11-11");
    assertTrue(request.getStatusCode().is4xxClientError(), "Check response status");
  }

  @Test
  //This test uses an old PPF File which is not compatible. We Disabled this for now. After the part list creation is finished, we have to fix this
  @Disabled
  void testCreateValidImportId() throws IOException {
    String product = "5G0";
    String prNumber = "AAA;BBB";
    String importDate = LocalDate.now().toString();
    LocalDate validDate = LocalDate.of(2020, 11, 11);
    InputStream in = getClass().getClassLoader().getResourceAsStream("data.ppf");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zipOut = new ZipOutputStream(baos);
    zipOut.putNextEntry(new ZipEntry("data.ppf"));
    assertNotNull(in);
    IOUtils.copy(in, zipOut);
    zipOut.close();

    final ObjectId fileId = gridFsRepository.save("test", new ByteArrayInputStream(baos.toByteArray()));

    final ProductData productData = new ProductData(product);

    productData.setImportStatus(ImportStatus.READY);
    productData.setFileId(fileId);

    productDataRepository.save(productData);

    ResponseEntity<ImportStatusDto> readyRequest = getRequest(PartListRestController.ENDPOINT_PART_LIST_REQUEST,
      ImportStatusDto.class, "productId", product, "importDate", importDate, "requester", "testUser");
    assertTrue(readyRequest.getStatusCode().is2xxSuccessful(), "Check response status");
    assertNotNull(readyRequest.getBody());
    ObjectId readyId = readyRequest.getBody().productDataId();

    final Map<String, Object> pathVariable = new HashMap<>();

    pathVariable.put("productDataId", readyId.toHexString());

    ResponseEntity<PlsPartListDto> request = getRequest(PartListRestController.ENDPOINT_PART_LIST_GET,
      PlsPartListDto.class, pathVariable, "validDate", validDate.toString(), "prNumbers", prNumber);

    assertTrue(request.getStatusCode().is2xxSuccessful(), "Check response status");

    PlsPartListDto response = request.getBody();

    assertNotNull(response);
    assertEquals("Check product data id", response.productDataId(), String.valueOf(readyId));
  }

}
