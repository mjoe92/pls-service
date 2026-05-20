package de.vw.paso.pls.model.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = CorruptedImport.DOCUMENT_CORRUPTED_IMPORT)
public class CorruptedImport {

  private static final String ENTRY_PRODUCT_ID = "productId";
  private static final String ENTRY_IMPORT_STATUS = "importStatus";
  private static final String ENTRY_IMPORT_DATE = "importDate";
  private static final String ENTRY_FILE_LOCKS = "fileLocks";
  private static final String ENTRY_COMPRESSED_FILE_ID = "compressedfileId";
  private static final String ENTRY_RAW_FILE_IDS = "rawfileIds";
  private static final String ENTRY_ERROR_MESSAGE = "errorMessage";
  private static final String ENTRY_STACKTRACE = "stackTrace";

  static final String DOCUMENT_CORRUPTED_IMPORT = "corruptedImport";

  @Setter(value = AccessLevel.NONE)
  @Id
  private ObjectId id;

  @Field(value = ENTRY_PRODUCT_ID, order = 10)
  private String productId;

  @Field(value = ENTRY_IMPORT_DATE, order = 30)
  private LocalDate importDate;

  @Field(value = ENTRY_COMPRESSED_FILE_ID, order = 40)
  private ObjectId compressedFileId;

  @Field(value = ENTRY_RAW_FILE_IDS, order = 50)
  private List<ObjectId> rawFileIds;

  @Field(value = ENTRY_ERROR_MESSAGE, order = 60)
  private String errorMessage;

  @Field(value = ENTRY_STACKTRACE, order = 70)
  private String stackTrace;

}
