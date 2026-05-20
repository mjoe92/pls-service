package de.vw.paso.pls.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.*;

@Slf4j
public final class GridFSDBFile {

  private static final String BUCKET_SUFFIX_FILES = ".files";
  private static final String BUCKET_SUFFIX_CHUNKS = ".chunks";

  private final com.mongodb.client.gridfs.model.GridFSFile gridFSFile;
  private final GridFSBucket gridFSBucket;
  private final com.mongodb.client.MongoCollection<Document> chunksCollection;

  public GridFSDBFile(GridFSFile gridFSFile, GridFSBucket gridFSBucket, MongoDatabase mongoDatabase) {
    this.gridFSFile = gridFSFile;
    this.gridFSBucket = gridFSBucket;

    MongoCollection<GridFSFile> filesCollection =
      mongoDatabase.getCollection(gridFSBucket.getBucketName() + BUCKET_SUFFIX_FILES,
        com.mongodb.client.gridfs.model.GridFSFile.class);

    chunksCollection = mongoDatabase.getCollection(gridFSBucket.getBucketName() + BUCKET_SUFFIX_CHUNKS);

    /* Ensure standard indexes as long as collections are small */
    try {
      if (filesCollection.countDocuments() < 1000) {
        filesCollection.createIndex(new BasicDBObject("filename", 1).append("uploadDate", 1));
      }

      if (chunksCollection.countDocuments() < 1000) {
        chunksCollection.createIndex(new BasicDBObject("files_id", 1).append("n", 1), new IndexOptions().unique(true));
      }
    } catch (final MongoException e) {
      log.error("Error creating GridFSDBFile", e);
    }
  }

  public InputStream getInputStream() {
    return new GridFSInputStream();
  }
//
//  public long writeTo(final String fileName) throws IOException {
//    return writeTo(new File(fileName));
//  }

  public long writeTo(final File file) throws IOException {
    try (final FileOutputStream out = new FileOutputStream(file)) {
      return writeTo(out);
    }
  }

  public long writeTo(final OutputStream outputStream) throws IOException {
    final int chunkNumber = numChunks();

    for (int index = 0; index < chunkNumber; index++) {
      outputStream.write(getChunk(index));
    }

    return gridFSFile.getLength();
  }

  private byte[] getChunk(final int chunkNumber) {
    if (gridFSBucket == null) {
      throw new IllegalStateException("No GridFS instance defined!");
    }

    FindIterable<Document> documents = chunksCollection.find(new BasicDBObject("files_id", gridFSFile.getId()).append("n", chunkNumber));
    Document chunk = documents.first();
    if (chunk == null) {
      throw new MongoException("Can't find a chunk!  file id: " + gridFSFile.getId() + " chunk: " + chunkNumber);
    }
    return chunk.get("data", Binary.class).getData();
  }

  public int numChunks() {
    double fileLength = gridFSFile.getLength();

    fileLength = fileLength / gridFSFile.getChunkSize();

    return (int) Math.ceil(fileLength);
  }
//
//  void remove() {
//    filesCollection.deleteOne(new BasicDBObject("_id", gridFSFile.getId()));
//    chunksCollection.deleteOne(new BasicDBObject("files_id", gridFSFile.getId()));
//  }

  private class GridFSInputStream extends InputStream {

    private final int numberOfChunks;

    private int currentChunkId = -1;
    private int offset = 0;
    private byte[] buffer = null;

    GridFSInputStream() {
      this.numberOfChunks = numChunks();
    }

    @Override
    public int available() {
      return (buffer == null) ? 0 : (buffer.length - offset);
    }

    @Override
    public int read() {
      final byte[] b = new byte[1];
      final int res = read(b);

      if (res < 0) {
        return -1;
      }

      return b[0] & 0xFF;
    }

    @Override
    public int read(final byte[] b) {
      return read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int length) {
      if ((buffer == null) || (offset >= buffer.length)) {
        if ((currentChunkId + 1) >= numberOfChunks) {
          return -1;
        }

        buffer = getChunk(++currentChunkId);
        offset = 0;
      }

      int r = Math.min(length, buffer.length - offset);

      System.arraycopy(buffer, offset, b, off, r);

      offset += r;

      return r;
    }

    @Override
    public long skip(final long bytesToSkip) {
      if (bytesToSkip <= 0) {
        return 0;
      }

      if (currentChunkId == numberOfChunks) {
        /* We're actually skipping over the back end of the file, short-circuit here
           Don't count those extra bytes to skip in with the return value */
        return 0;
      }

      /* Offset in the whole file */
      long offsetInFile = 0;

      if (currentChunkId >= 0) {
        offsetInFile = currentChunkId * gridFSFile.getChunkSize() + offset;
      }

      if (bytesToSkip + offsetInFile >= gridFSFile.getLength()) {
        currentChunkId = numberOfChunks;
        buffer = null;

        return gridFSFile.getLength() - offsetInFile;
      }

      final int temp = currentChunkId;

      currentChunkId = (int) ((bytesToSkip + offsetInFile) / gridFSFile.getChunkSize());

      if (temp != currentChunkId) {
        buffer = getChunk(currentChunkId);
      }

      offset = (int) ((bytesToSkip + offsetInFile) % gridFSFile.getChunkSize());

      return bytesToSkip;
    }
  }
}
