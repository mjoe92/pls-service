package de.vw.paso.pls.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import de.vw.paso.pls.model.ImportStatus;
import de.vw.paso.pls.model.domain.FileLock;
import de.vw.paso.pls.model.domain.ProductData;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public final class ProductDataDriver {

  private final MongoTemplate mongoTemplate;
  private final GridFSBucket gridFSBucket;

  private ProductData productData;

  @Autowired
  public ProductDataDriver(final MongoTemplate mongoTemplate, @Qualifier("bucket") GridFSBucket gridFSBucket) {
    this.mongoTemplate = mongoTemplate;
    this.gridFSBucket = gridFSBucket;
  }

  public static void ignoreResult(Void result) {
    result = null;
  }

  public void reset() {
    productData = null;
  }

  public ProductDataDriver addDefaultProductData() {
    final ObjectId[] savedFileId = new ObjectId[1];

    createProductData();
    addFileLock();
    saveFile(result -> savedFileId[0] = result);
    setFileId(savedFileId[0]);
    saveProductData();

    return this;
  }

  public ProductDataDriver addDefaultProductData(final Consumer<ProductData> savedProductData) {
    final ObjectId[] savedFileId = new ObjectId[1];

    createProductData();
    addFileLock();
    saveFile(result -> savedFileId[0] = result);
    setFileId(savedFileId[0]);
    saveProductData(savedProductData);

    return this;
  }

  public ProductDataDriver createProductData() {
    productData = new ProductData("5G0");

    return this;
  }

  public ProductDataDriver setImportStatus(final ImportStatus importStatus) {
    productData.setImportStatus(importStatus);

    return this;
  }

  public ProductDataDriver addFileLock() {
    return addFileLock(0);
  }

  public ProductDataDriver addFileLock(final long plusDays) {
    productData.getFileLocks().add(new FileLock(plusDays));

    return this;
  }

  public ProductDataDriver setFileId(final ObjectId fileId) {
    productData.setFileId(fileId);

    return this;
  }

  public void saveProductData() {
    mongoTemplate.save(productData);
  }

  public ProductDataDriver saveProductData(final Consumer<ProductData> savedProductData) {
    savedProductData.accept(mongoTemplate.save(productData));

    return this;
  }

  public ProductDataDriver saveFile(final Consumer<ObjectId> savedFileId) {
    ObjectId saveTestFile = gridFSBucket.uploadFromStream("saveTestFile", new ByteArrayInputStream("ProductDataTest".getBytes()));
    savedFileId.accept(saveTestFile);
    return this;
  }

  public ProductDataDriver doAction(final Runnable action) {
    action.run();

    return this;
  }

  public <T extends OUT, OUT> ProductDataDriver doMonoAction(
    final Supplier<T> action, final Consumer<OUT> resultAction
  ) {
    resultAction.accept(action.get());

    return this;
  }

  public <T extends List<OUT>, OUT> ProductDataDriver doFluxAction(
    final Supplier<T> action, final Consumer<List<OUT>> resultAction
  ) {
    resultAction.accept(action.get());

    return this;
  }

  public ProductDataDriver storeCurrentProductDataCount(final Consumer<Long> count) {
    count.accept(mongoTemplate.count(new Query(), ProductData.class));

    return this;
  }

  public ProductData get() {
    return productData;
  }

  public ProductDataDriver thenDocumentExists() {
    final Boolean isExists = mongoTemplate.collectionExists(ProductData.class);

    assertNotNull(isExists, "Document does not exist");
    assertTrue(isExists, "Document does not exist");

    return this;
  }

  public ProductDataDriver thenDocumentCountIsIncrementedBy(final int number, final int originalSize) {
    final Long productDataCount = mongoTemplate.count(new Query(), ProductData.class);

    assertNotNull(productDataCount, "Count does not filteredOut");
    assertEquals(originalSize + number, productDataCount.longValue(), "Count does not filteredOut");

    return this;
  }

  public ProductDataDriver thenProductDataExists(final ObjectId productDataId) {
    final ProductData data = mongoTemplate.findById(productDataId, ProductData.class);

    assertNotNull(data, "Part list data does not exist");

    return this;
  }

  public ProductDataDriver thenProductDataNotExists(final ObjectId productDataId) {
    final ProductData data = mongoTemplate.findById(productDataId, ProductData.class);

    assertNull(data, "Part list data exists");

    return this;
  }

  public ProductDataDriver thenFileExists(final ObjectId fileId) {
    final GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", fileId)).first();

    assertNotNull(gridFSFile, "File not found");
    assertTrue(gridFSFile.getLength() > 0, "File is empty");

    return this;
  }

  public ProductDataDriver thenFileNotExists(final ObjectId fileId) {
    final GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", fileId)).first();

    assertNull(gridFSFile, "File exists");

    return this;
  }

  public ProductDataDriver thenFileLockExists(final ObjectId productDataId, final ObjectId fileLockId) {
    final ProductData productData = mongoTemplate.findById(productDataId, ProductData.class);

    assertNotNull(productData);

    final boolean hasFileLock =
      productData.getFileLocks().stream().anyMatch(fileLock -> fileLock.getId().equals(fileLockId));

    assertTrue(hasFileLock, "File lock does not exist");

    return this;
  }

  public ProductDataDriver thenFileLockNotExists(final ObjectId productDataId, final ObjectId fileLockId) {
    final ProductData productData = mongoTemplate.findById(productDataId, ProductData.class);

    assertNotNull(productData);

    final boolean hasFileLock =
      productData.getFileLocks().stream().anyMatch(fileLock -> fileLock.getId().equals(fileLockId));

    assertFalse(hasFileLock, "File lock exists");

    return this;
  }

  public ProductDataDriver thenFileLockCountIsIncrementedBy(
    final int number, final int originalSize, final ObjectId productDataId
  ) {
    final ProductData productData = mongoTemplate.findById(productDataId, ProductData.class);

    assertNotNull(productData);
    assertEquals(originalSize + number, productData.getFileLocks().size(), "Document count does not filteredOut");

    return this;
  }

  public ProductDataDriver thenProductDataCountEqualsTo(final long productDataSize) {
    final Long currentProductDataCount = mongoTemplate.count(new Query(), ProductData.class);

    assertNotNull(currentProductDataCount);
    assertEquals(currentProductDataCount.longValue(), productDataSize);

    return this;
  }

  public ProductDataDriver thenActionResultIsNull(final Object obj) {
    assertNull(obj);

    return this;
  }

}
