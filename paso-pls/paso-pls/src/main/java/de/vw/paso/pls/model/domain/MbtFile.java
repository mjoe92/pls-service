package de.vw.paso.pls.model.domain;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mbtFile")
public class MbtFile {

  @Id
  private ObjectId id;
  private String fileName;
  private ObjectId fileId;
  private Instant fileCreatedTimeStamp;

  @CreatedDate
  private Instant createdDate;

  public MbtFile(String fileName, ObjectId fileId) {
    this.fileName = fileName;
    this.fileId = fileId;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public ObjectId getFileId() {
    return fileId;
  }

  public void setFileId(ObjectId fileId) {
    this.fileId = fileId;
  }

  public Instant getFileCreatedTimeStamp() {
    return fileCreatedTimeStamp;
  }

  public void setFileCreatedTimeStamp(Instant fileCreatedTimeStamp) {
    this.fileCreatedTimeStamp = fileCreatedTimeStamp;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
  }
}
