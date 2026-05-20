package de.vw.paso.pls.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class GridFsRepository {

  private final GridFSBucket gridFSBucket;
  private final GridFSBucket gridFSErrorBucket;

  public GridFsRepository(@Qualifier("bucket") GridFSBucket gridFSBucket,
                          @Qualifier("errorBucket") GridFSBucket errorBucket) {
    this.gridFSBucket = gridFSBucket;
    this.gridFSErrorBucket = errorBucket;
  }

  public ObjectId saveError(final String fileName, final InputStream inputStream) {
    return gridFSErrorBucket.uploadFromStream(fileName, inputStream);
  }

  public List<ObjectId> saveError(final List<File> files) throws FileNotFoundException {
    List<ObjectId> objectIds = new ArrayList<>();

    for (final File file : files) {
      objectIds.add(saveError(file.getName(), new FileInputStream(file)));
    }

    return objectIds;
  }

  //TODO: this method is only used in tests, we can get rid of this after refactoring tests
  public ObjectId save(final String fileName, final InputStream inputStream) {
    return gridFSBucket.uploadFromStream(fileName, inputStream);
  }

  public void load(final BsonObjectId fileId, final OutputStream outputStream) {
    gridFSBucket.downloadToStream(fileId, outputStream);
    try {
      outputStream.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public GridFSFile findById(final ObjectId fileId) {
    return gridFSBucket.find(Filters.eq("_id", fileId)).first();
  }

  public Boolean isExist(final ObjectId fileId) {
    GridFSFile id = gridFSBucket.find(Filters.eq("_id", fileId)).first();
    return id != null;
  }

  public void delete(final ObjectId fileId) {
    if (isExist(fileId)) {
      gridFSBucket.delete(fileId);
    }
  }
}
