package de.vw.paso.pls.datarequest.fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.vw.paso.pls.datarequest.AbstractDataRequestStrategy;
import de.vw.paso.pls.datarequest.DataRequestException;
import de.vw.paso.pls.datarequest.DataRequestInitException;

public class FileSystemRequestStrategy extends AbstractDataRequestStrategy {

    private static final String MOCKUP = "/tiwh/mockup.zip";
    private static final Map<String, String> PRODUCT_ID_TO_DATA_MAP = new HashMap<>();

    static {
        PRODUCT_ID_TO_DATA_MAP.put("3G0", "/tiwh/4240.zip");
        PRODUCT_ID_TO_DATA_MAP.put("5G0", "/tiwh/5g0.zip");
        PRODUCT_ID_TO_DATA_MAP.put("5H0", "/tiwh/veron-5H0-20200228.zip");
        PRODUCT_ID_TO_DATA_MAP.put("10A", "/tiwh/veron-10A-20200228.zip");

        PRODUCT_ID_TO_DATA_MAP.put("05LC", "/tiwh/veron-05LC-20200228.zip");
        PRODUCT_ID_TO_DATA_MAP.put("0EHA", "/tiwh/veron-0EHA-20200228.zip");

        PRODUCT_ID_TO_DATA_MAP.put("0C9A", "/tiwh/veron-0C9A-20200228.zip");
        PRODUCT_ID_TO_DATA_MAP.put("0C9C", "/tiwh/veron-0C9A-20200228.zip");
        PRODUCT_ID_TO_DATA_MAP.put("0MHA", "/tiwh/veron-0MHA-20200228.zip");
    }

    private Path requestFilePath;

    @Override
    public void onInit() throws DataRequestInitException {
        LOG.info("Check request directory");

        String localTempDir = System.getProperty("java.io.tmpdir");
        Path requestDirPath = Paths.get(localTempDir, "paso", "tiWhRequest");
        File folder = requestDirPath.toFile();

        if (folder.exists()) {
            LOG.info("Folder is valid: {}", folder.getAbsolutePath());
        } else {
            LOG.info("Folder does not exists. Will create new: {}", folder.getAbsolutePath());
            if (!folder.mkdirs()) {
                throw new DataRequestInitException("Could not create request directory");
            }
        }

        requestFilePath = requestDirPath.resolve("request");
        LOG.info("Request file will be {}", requestFilePath);
    }

    @Override
    public List<String> getFileNamesFromInbox() throws DataRequestException {
        try (Stream<Path> files = Files.walk(getLocalTempDir())) {
            return files.filter(Files::isRegularFile).map(e -> e.getFileName().toString()).toList();
        } catch (IOException e) {
            throw new DataRequestException("Could not list file names", e);
        }
    }

    @Override
    public List<File> getFilesFromInbox() throws DataRequestException {
        try (Stream<Path> files = Files.walk(getLocalTempDir())) {
            return files.filter(Files::isRegularFile).map(Path::toFile).toList();
        } catch (IOException e) {
            throw new DataRequestException("Could not get new TiWh files", e);
        }
    }

    @Override
    public void deleteTiWhFiles() throws DataRequestException {
        LOG.info("Delete TIWH request file: {}", requestFilePath);
        try {
            Files.delete(requestFilePath);
        } catch (NoSuchFileException e) {
            LOG.info("Request file does not exist. Nothing to delete");
        } catch (IOException e) {
            throw new DataRequestException("Could not delete request file", e);
        }
    }

    @Override
    public void moveFileFromArchive(String productId) {
        // noop
    }

    @Override
    public void sendTiWhRequest(String productId) throws DataRequestException {
        String productData = "/tiwh/5g0.zip";
        LOG.info("Next product id is {}. Will use {}", productId, productData);
        simulateTiWh(productData);

        File requestFile = requestFilePath.toFile();
        try (FileOutputStream outputStream = new FileOutputStream(requestFile)) {
            outputStream.write(productId.getBytes());
        } catch (IOException e) {
            throw new DataRequestException(e);
        }
    }

    private void simulateTiWh(String productData) {
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            LOG.info("Schedule simulation for 10 second");
            scheduler.schedule(() -> copyResource(productData), 5, TimeUnit.SECONDS);
        }
    }

    private void copyResource(String path) {
        synchronized (this) {
            try {
                LOG.info("TiWh simulate: copy resources from {}", path);
                InputStream in = getClass().getResourceAsStream(path);
                ZipInputStream zipIn = new ZipInputStream(in);
                ZipEntry nextEntry = zipIn.getNextEntry();

                while (nextEntry != null) {
                    Path resultPath = getLocalTempDir().resolve(nextEntry.getName());
                    Files.deleteIfExists(resultPath);

                    LOG.info("TiWh simulate: copy file: {}", resultPath);
                    Files.copy(zipIn, resultPath);
                    nextEntry = zipIn.getNextEntry();
                }

                zipIn.close();
            } catch (Exception e) {
                LOG.error("Could not copy resource for simulation", e);
            }
        }
    }
}
