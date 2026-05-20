package de.vw.paso.pls.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import de.vw.paso.pls.datarequest.DataRequestInitException;
import de.vw.paso.pls.datarequest.filestorage.FileHandler;
import de.vw.paso.pls.datarequest.filestorage.FileStorageStrategy;
import de.vw.paso.pls.model.ImportStatus;
import de.vw.paso.pls.model.domain.FileLock;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.repository.ProductDataRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductDataService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductDataService.class);

  private final ProductDataRepository productDataRepository;
  private final FileHandler fileHandler;

  public ProductDataService(ProductDataRepository productDataRepository, FileStorageStrategy fileStorageStrategy)
          throws DataRequestInitException {
    this.productDataRepository = productDataRepository;
    this.fileHandler = fileStorageStrategy.createFileHandler();
  }

  public ObjectId addFileLock(final ObjectId productDataId, final long plusDays) {
    LOG.info("Add {} days file lock to product data with id: {}", plusDays, productDataId);
    ProductData productData = productDataRepository.findById(productDataId).orElse(null);
    if (productData != null) {
      FileLock fileLock = new FileLock(plusDays);

      productData.getFileLocks().add(fileLock);
      productDataRepository.save(productData);
      LOG.info("Product data found. Added file lock with id: {}", fileLock.getId());
      return fileLock.getId();
    } else {
      LOG.info("Product data not found. Nothing to do");
      return null;
    }
  }

  public void deleteAllProductData() {
    productDataRepository.deleteAll();
  }

  public void deleteProductData(ProductData productData) {
    productDataRepository.delete(productData);
  }

  public void deleteProductDataWithExpiredLocks() {
    LOG.info("Delete expired product data");
    final LocalDateTime now = LocalDateTime.now();
    final List<ProductData> deletedItems = productDataRepository.findAllEmptyOrExpiredFileLock(now);
    LOG.info("Found {} items to delete", deletedItems.size());
    deletedItems.forEach(productData -> {
      final List<FileLock> locks = productData.getFileLocks();

      removeExpiredLock(now, locks);

      if (locks.isEmpty()) {
        if (productData.getFileId() != null) {
          fileHandler.deleteFileFromStorage(productData.getFileId());
        }

        productDataRepository.delete(productData);
      } else {
        productDataRepository.save(productData);
      }
    });
  }

  public Optional<ProductData> findById(ObjectId productDataId) {
    return productDataRepository.findById(productDataId);
  }

  public Optional<LocalDateTime> getFileLockLastExpiryDate(final ObjectId productDataId) {
    ProductData pro = productDataRepository.findById(productDataId).orElse(null);
    if (pro != null) {
      return pro.getFileLocks().stream().map(FileLock::getExpiryDate).max(LocalDateTime::compareTo);
    } else {
      return Optional.empty();
    }
  }

  public ProductData findByProductIdAndImportDate(final String productId, final LocalDate importDate) {
    return productDataRepository.findByProductIdAndImportDate(productId, importDate);
  }

  public List<ProductData> loadAll() {
    return productDataRepository.findAll();
  }

  public List<ProductData> loadAllByProductId(final String productId) {
    return productDataRepository.findAllByProductId(productId);
  }

  public byte[] loadProductDataFile(final ObjectId fileId) {
    return fileHandler.readFileFromStorage(fileId);
  }

  public void removeFileLock(final ObjectId productDataId, final ObjectId fileLockId) {
    LOG.info("Remove file lock with id: {} from product id: {}", fileLockId, productDataId);
    ProductData productData = productDataRepository.findById(productDataId).orElse(null);
    if (productData != null) {
      productData.getFileLocks().stream().filter(lock -> Objects.equals(lock.getId(), fileLockId)).findFirst()
        .ifPresent(lock -> productData.getFileLocks().remove(lock));
      productDataRepository.save(productData);
      LOG.info("Found product, file lock removed");
    } else {
      LOG.info("Product not found. Cannot remove file lock");
    }
  }

  public void save(ProductData productData) {
    productDataRepository.save(productData);
  }

  public ProductData saveProductData(final String productId) {
    return productDataRepository.save(new ProductData(productId));
  }

  public void saveProductData(String productId, byte[] compressedFile) {
    LOG.info("Save Product data for id: {}", productId);
    ProductData productData = getCreateProductData(productId);
    productData.setImportStatus(ImportStatus.READY);
    ObjectId fileId = fileHandler.writeFileToStorage(compressedFile);
    productData.setFileId(fileId);

    productDataRepository.save(productData);
  }

  public ProductData saveProductDataError(String productId) {
    LOG.info("Save error product data for product id: {}", productId);
    ProductData productData = getCreateProductData(productId);
    productData.setImportStatus(ImportStatus.ERROR);
    productDataRepository.save(productData);

    return productData;
  }

  public void saveTimeoutProductData(String productId) {
    LOG.info("Save timed out product data with product id: {}", productId);
    ProductData productData = getCreateProductData(productId);
    productData.setImportStatus(ImportStatus.TIMEOUT);
    productDataRepository.save(productData);
  }

  private ProductData getCreateProductData(String productId) {
    ProductData productData = productDataRepository.findByProductIdAndImportDate(productId, LocalDate.now());
    if (productData == null) {
      productData = new ProductData(productId);
    }
    return productData;
  }

  private void removeExpiredLock(final LocalDateTime now, final List<FileLock> locks) {
    for (int index = (locks.size() - 1); index >= 0; index--) {
      if (locks.get(index).getExpiryDate().isBefore(now)) {
        locks.remove(index);
      }
    }
  }
}
