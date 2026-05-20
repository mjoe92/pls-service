package de.vw.paso.pls.model.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
public class FileLock {

  private static final String ENTRY_ID = "id";
  private static final String ENTRY_EXPIRY_DATE = "expiryDate";

  private static final int DEFAULT_EXPIRY_DAYS = 3;

  @Field(value = ENTRY_ID, order = 10)
  private ObjectId id;

  @Field(value = ENTRY_EXPIRY_DATE, order = 20)
  private LocalDateTime expiryDate;

  public FileLock() {
    this(DEFAULT_EXPIRY_DAYS);
  }

  public FileLock(final long plusDays) {
    id = new ObjectId();
    expiryDate = LocalDateTime.now().plusDays(plusDays);
  }

}
