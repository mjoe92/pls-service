package de.vw.paso.pls.model.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.vw.paso.pls.model.ImportStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "productData")
public class ProductData {

  private static final String ENTRY_PRODUCT_ID = "productId";
  private static final String ENTRY_IMPORT_STATUS = "importStatus";
  private static final String ENTRY_IMPORT_DATE = "importDate";
  private static final String ENTRY_FILE_LOCKS = "fileLocks";
  private static final String ENTRY_FILE_ID = "fileId";

  @Id
  private ObjectId id;

  @Field(value = ENTRY_PRODUCT_ID, order = 10)
  private String productId;

  @Field(value = ENTRY_IMPORT_STATUS, order = 20)
  private ImportStatus importStatus;

  @Field(value = ENTRY_IMPORT_DATE, order = 30)
  private LocalDate importDate;

  @Field(value = ENTRY_FILE_ID, order = 40)
  private ObjectId fileId;

  @Field(value = ENTRY_FILE_LOCKS, order = 50)
  private List<FileLock> fileLocks = new ArrayList<>();

  public ProductData(final String productId) {
    this.productId = productId;
    this.importStatus = ImportStatus.PENDING;
    this.importDate = LocalDate.now();
  }

  public void setFileId(ObjectId fileId) {
    this.fileId = fileId;
  }

  public void setImportStatus(ImportStatus importStatus) {
    this.importStatus = importStatus;
  }

  public ObjectId getId() {
    return id;
  }

  public String getProductId() {
    return productId;
  }

  public ImportStatus getImportStatus() {
    return importStatus;
  }

  public LocalDate getImportDate() {
    return importDate;
  }

  public ObjectId getFileId() {
    return fileId;
  }

  public List<FileLock> getFileLocks() {
    return fileLocks;
  }
}
