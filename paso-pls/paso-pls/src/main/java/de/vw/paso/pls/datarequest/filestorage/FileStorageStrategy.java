package de.vw.paso.pls.datarequest.filestorage;

import de.vw.paso.pls.datarequest.DataRequestInitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageStrategy {

  private static final String LOCAL = "LOCAL";

  @Value("${stage:}")
  private String stage;

  @Value("${azure.blob.service-endpoint:endpoint}")
  private String azureBlobServiceEndpoint;
  @Value("${spring.cloud.azure.storage.blob.account-key:key}")
  private String azureStorageAccountKey;
  @Value("${spring.cloud.azure.storage.blob.account-name:name}")
  private String azureStorageAccountName;

  public FileHandler createFileHandler() throws DataRequestInitException {
    return stage.equalsIgnoreCase(LOCAL) ? new LocalFileHandler()
      : new AzureFileHandler(azureBlobServiceEndpoint, azureStorageAccountKey, azureStorageAccountName);
  }
}
