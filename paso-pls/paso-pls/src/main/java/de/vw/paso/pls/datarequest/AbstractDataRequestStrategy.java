package de.vw.paso.pls.datarequest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contract for a strategy to send and get data from a location that is defined by the concrete subclass.
 */
public abstract class AbstractDataRequestStrategy {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractDataRequestStrategy.class);

  private Path localResultTempDirectory;

  public abstract void deleteTiWhFiles() throws DataRequestException;

  public abstract List<String> getFileNamesFromInbox() throws DataRequestException;

  public abstract List<File> getFilesFromInbox() throws DataRequestException;

  public final void init() throws DataRequestInitException {
    String localTempDir = System.getProperty("java.io.tmpdir");
    localResultTempDirectory = Paths.get(localTempDir, "paso", "tiWhResultData");
    LOG.info("Check local temp folders");

    if (!localResultTempDirectory.toFile().exists()) {
      LOG.info("Create local temp folder: {}", localResultTempDirectory);
      boolean success = localResultTempDirectory.toFile().mkdirs();
      if (!success) {
        throw new DataRequestInitException("Could not create local temp directory");
      }
    }

    LOG.info("Delete existing files in temp folder: {}", localResultTempDirectory);
    clearLocalResultTempDirectory();

    onInit();
  }

  public abstract void moveFileFromArchive(String productId) throws DataRequestException;

  public abstract void sendTiWhRequest(String productId) throws DataRequestException;

  protected final void clearLocalResultTempDirectory() {
    File[] subFiles = localResultTempDirectory.toFile().listFiles();
    if (subFiles != null) {
      for (File subFile : subFiles) {
        subFile.delete();
      }
    }
  }

  protected final Path getLocalTempDir() {
    return localResultTempDirectory;
  }

  protected abstract void onInit() throws DataRequestInitException;
}
