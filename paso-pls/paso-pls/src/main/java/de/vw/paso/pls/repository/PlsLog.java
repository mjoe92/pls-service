package de.vw.paso.pls.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = PlsLog.DOCUMENT_TI_WH_IMPORT_QUEUE)
public class PlsLog {

  private static final String fieldLogTime = "logTime";
  private static final String fieldText = "text";

  static final String DOCUMENT_TI_WH_IMPORT_QUEUE = "plsLog";

  @Id
  private ObjectId id;

  @Field(value = fieldLogTime, order = 20)
  private LocalDateTime logTime;

  @Field(value = fieldText, order = 40)
  private String text;
}
