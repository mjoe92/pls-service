package de.vw.paso.pls.job;

import de.vw.paso.pls.PlsApplicationTests;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.service.ProductDataDriver;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("SpringJavaAutowiringInspection")
class CleanupSchedulerTest extends PlsApplicationTests {

  @Autowired
  private ProductDataDriver productDataDriver;

  @Autowired
  private CleanupScheduler cleanupScheduler;

  private ObjectId[] savedFileId;
  private ProductData[] savedProductData;

  @BeforeEach
  void setup() {
    savedFileId = new ObjectId[1];
    savedProductData = new ProductData[1];

    productDataDriver.reset();
  }

  @Test
  void cleanUpWithSingleFileLockAndExpired() {
    productDataDriver.createProductData()
      .addFileLock(-1)
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> cleanupScheduler.runFileLockCleanup())
      .thenProductDataNotExists(savedProductData[0].getId())
      .thenFileNotExists(savedProductData[0].getFileId());
  }

  @Test
  void cleanUpWithSingleFileLockAndNotExpired() {
    productDataDriver.createProductData()
      .addFileLock(1)
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> cleanupScheduler.runFileLockCleanup())
      .thenProductDataExists(savedProductData[0].getId())
      .thenFileExists(savedProductData[0].getFileId());
  }

  @Test
  void cleanUpWithMultipleFileLockAndOneExpired() {
    productDataDriver.createProductData()
      .addFileLock(-1)
      .addFileLock(1)
      .addFileLock(1)
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> cleanupScheduler.runFileLockCleanup())
      .thenProductDataExists(savedProductData[0].getId())
      .thenFileExists(savedProductData[0].getFileId());
  }

  @Test
  void cleanUpWithMultipleFileLockAndAllExpired() {
    productDataDriver.createProductData()
      .addFileLock(-1)
      .addFileLock(-2)
      .addFileLock(-3)
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> cleanupScheduler.runFileLockCleanup())
      .thenProductDataNotExists(savedProductData[0].getId())
      .thenFileNotExists(savedProductData[0].getFileId());
  }

  @Test
  void cleanUpWithoutFileLock() {
    productDataDriver.createProductData()
      .saveFile(result -> savedFileId[0] = result)
      .setFileId(savedFileId[0])
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> cleanupScheduler.runFileLockCleanup())
      .thenProductDataNotExists(savedProductData[0].getId())
      .thenFileNotExists(savedProductData[0].getFileId());
  }

  @Test
  void cleanUpWithExpiredFileLockWhenFileNotExists() {
    productDataDriver.createProductData()
      .addFileLock(-1)
      .setFileId(new ObjectId())
      .saveProductData(result -> savedProductData[0] = result)
      .doAction(() -> cleanupScheduler.runFileLockCleanup())
      .thenProductDataNotExists(savedProductData[0].getId())
      .thenFileNotExists(savedProductData[0].getFileId());
  }

}
