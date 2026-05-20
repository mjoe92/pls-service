package de.vw.paso.pls.datarequest.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.vw.paso.pls.datarequest.DataRequestInitException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileHandler implements FileHandler {

  private static final Logger LOG = LoggerFactory.getLogger(LocalFileHandler.class);

  private final Path localTempDirectory;

  public LocalFileHandler() throws DataRequestInitException {
    String localTempDir = System.getProperty("java.io.tmpdir");
    localTempDirectory = Paths.get(localTempDir, "paso", "productDataFiles");

    File file = localTempDirectory.toFile();
    if (!file.exists()) { 
      LOG.info("Create local temp folder: {}", localTempDirectory);

      if (!file.mkdirs()) {
        throw new DataRequestInitException("Could not create local temp directory");
      }
    }
  }

  @Override
  public byte[] readFileFromStorage(ObjectId fileName) {
    Path filePath = localTempDirectory.resolve(fileName.toHexString());

    try {
      LOG.info("Reading from local file storage, file id: {}", fileName);

      return Files.readAllBytes(filePath);
    } catch (IOException e) {
      throw new FileSystemNotFoundException(e.getMessage());
    }
  }

  @Override
  public ObjectId writeFileToStorage(byte[] data) {
    ObjectId fileName = new ObjectId();
    Path filePath = localTempDirectory.resolve(fileName.toHexString());

    try {
      Files.write(filePath, data);
      LOG.info("Writing to local file storage, file id: {}", fileName);

      return fileName;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void deleteFileFromStorage(ObjectId fileName) {
    Path filePath = localTempDirectory.resolve(fileName.toHexString());

    LOG.info("Deleting from local file storage, file id: {}", fileName);
    try {
      Files.delete(filePath);
    } catch (IOException e) {
      throw new FileSystemNotFoundException(e.getMessage());
    }
  }
}
