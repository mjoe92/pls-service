package de.vw.paso.pls.service;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.pls.PlsApplicationTests;
import de.vw.paso.pls.model.domain.ProductData;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaAutowiringInspection")
class ProductDataTest extends PlsApplicationTests {

  @Autowired
  private ProductDataDriver productDataDriver;

  @Autowired
  private ProductDataService productDataService;

  @BeforeEach
  void setup() {
    productDataDriver.reset();
  }

  @Test
  void saveProductData() {
    final Long[] originalProductDataCount = new Long[1];
    final ObjectId[] savedFileId = new ObjectId[1];
    final ProductData[] savedProductData = new ProductData[1];

    productDataDriver.createProductData()
      .addFileLock()
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .storeCurrentProductDataCount(count -> originalProductDataCount[0] = count)
      .saveProductData(result -> savedProductData[0] = result)
      .thenDocumentExists()
      .thenProductDataExists(savedProductData[0].getId())
      .thenDocumentCountIsIncrementedBy(1, originalProductDataCount[0].intValue())
      .thenFileExists(savedProductData[0].getFileId());
  }

  @Test
  void deleteProductDataWithExpiredLocks() {
    final ObjectId[] savedFileId = new ObjectId[1];
    final ProductData[] savedProductData = new ProductData[1];

    productDataDriver.createProductData()
      .addFileLock(-1)
      .addFileLock(-2)
      .addFileLock(-3)
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> productDataService.deleteProductDataWithExpiredLocks())
      .thenProductDataNotExists(savedProductData[0].getId())
      .thenFileNotExists(savedProductData[0].getFileId());
  }

  @Test
  void loadAllProductData() {
    final Long[] originalProductDataCount = new Long[1];
    final List<ProductData> productDatas = new ArrayList<>();

    productDataDriver.storeCurrentProductDataCount(count -> originalProductDataCount[0] = count)
      .addDefaultProductData()
      .addDefaultProductData()
      .addDefaultProductData()
      .doFluxAction(() -> productDataService.loadAll(), productDatas::addAll)
      .thenProductDataCountEqualsTo(originalProductDataCount[0] + 3);
  }

  @Test
  void addProductDataFileLock() {
    final ObjectId[] fileLockId = new ObjectId[1];
    final ProductData[] savedProductData = new ProductData[1];

    productDataDriver.addDefaultProductData(result -> savedProductData[0] = result)
      .doMonoAction(
        () -> productDataService.addFileLock(savedProductData[0].getId(), 30), result -> fileLockId[0] = result
      )
      .thenFileLockCountIsIncrementedBy(1, savedProductData[0].getFileLocks().size(), savedProductData[0].getId())
      .thenFileLockExists(savedProductData[0].getId(), fileLockId[0]);
  }

//  @Test
//  void removeProductDataFileLock() {
//    final ProductData[] savedProductData = new ProductData[1];
//
//    productDataDriver.addDefaultProductData(result -> savedProductData[0] = result)
//      .doMonoAction(
//        () -> productDataService.removeFileLock(
//          savedProductData[0].getId(), savedProductData[0].getFileLocks().get(0).getId()
//        ),
//        ProductDataDriver::ignoreResult
//      )
//      .thenFileLockCountIsIncrementedBy(-1, savedProductData[0].getFileLocks().size(), savedProductData[0].getId())
//      .thenFileLockNotExists(savedProductData[0].getId(), savedProductData[0].getFileLocks().get(0).getId());
//  }

  @Test
  void addProductDataFileLockWhenProductDataNotExists() {
    final ObjectId[] fileLockId = new ObjectId[1];

    productDataDriver
      .doMonoAction(() -> productDataService.addFileLock(new ObjectId(), 30), result -> fileLockId[0] = result)
      .thenActionResultIsNull(fileLockId[0]);
  }

  @Test
  void removeProductDataFileLockWhenFileLockNotExists() {
    final ProductData[] savedProductData = new ProductData[1];

    productDataDriver.addDefaultProductData(result -> savedProductData[0] = result)
      .doAction(() -> productDataService.removeFileLock(savedProductData[0].getId(), new ObjectId()))
      .thenFileLockCountIsIncrementedBy(0, savedProductData[0].getFileLocks().size(), savedProductData[0].getId());
  }

}
