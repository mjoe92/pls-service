package de.vw.paso.pls.datarequest.filestorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureFileHandler implements FileHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AzureFileHandler.class);

    private static final String CONTAINER_NAME = "tiwh/";
    /** 32MiB to reserve more space when downloading files from Azure Blob Storage. */
    private static final int MIB32 = 33554432;

    private final String azureBlobServiceEndpoint;
    private final String azureStorageAccountKey;
    private final String azureStorageAccountName;

    public AzureFileHandler(String azureBlobServiceEndpoint, String azureStorageAccountKey,
        String azureStorageAccountName) {
        this.azureBlobServiceEndpoint = azureBlobServiceEndpoint;
        this.azureStorageAccountKey = azureStorageAccountKey;
        this.azureStorageAccountName = azureStorageAccountName;
    }

    /**
     * @param fileName,
     *     the name of the file
     * @return the content of the file as a byte array
     */
    @Override
    public byte[] readFileFromStorage(ObjectId fileName) {
        BlobContainerClient blobContainerClient = buildContainerClient();
        LOG.info("Reading from azure file storage, file id: {}", fileName);

        ByteArrayOutputStream stream = new ByteArrayOutputStream(MIB32);
        try (stream) {
            blobContainerClient.getBlobClient(fileName.toHexString()).downloadStream(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return stream.toByteArray();
    }

    /**
     * @param data,
     *     the data to be written in Azure storage
     * @return UUID of the file stored in Azure file storage
     * <p>
     * writes file into the given containerName as a random UUID and returns this value to be stored in a database
     */
    @Override
    public ObjectId writeFileToStorage(byte[] data) {
        ObjectId fileId = new ObjectId();
        BlobContainerClient blobContainerClient = buildContainerClient();
        LOG.info("Writing to azure file storage, file id: {}", fileId);

        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            blobContainerClient.getBlobClient(fileId.toHexString()).upload(stream, data.length);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return fileId;
    }

    @Override
    public void deleteFileFromStorage(ObjectId fileName) {
        BlobContainerClient blobContainerClient = buildContainerClient();

        LOG.info("Deleting from azure file storage, file id: {}", fileName);
        blobContainerClient.getBlobClient(fileName.toHexString()).deleteIfExists();
    }

    private BlobContainerClient buildContainerClient() {
        LOG.info("Creating blob service to: {}", azureBlobServiceEndpoint);
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(azureBlobServiceEndpoint)
            .credential(new StorageSharedKeyCredential(azureStorageAccountName, azureStorageAccountKey)).buildClient();
        return blobServiceClient.createBlobContainerIfNotExists(CONTAINER_NAME);
    }
}
