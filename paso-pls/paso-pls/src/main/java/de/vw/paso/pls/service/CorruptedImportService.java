package de.vw.paso.pls.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.vw.paso.pls.model.domain.CorruptedImport;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.repository.CorruptedImportRepository;
import de.vw.paso.pls.repository.GridFsRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CorruptedImportService {

  private static final Logger LOG = LoggerFactory.getLogger(CorruptedImportService.class);

  private final GridFsRepository gridFsRepository;
  private final CorruptedImportRepository corruptedImportRepository;

  public CorruptedImportService(GridFsRepository gridFsRepository,
    CorruptedImportRepository corruptedImportRepository) {
    this.gridFsRepository = gridFsRepository;
    this.corruptedImportRepository = corruptedImportRepository;
  }

  public ObjectId saveCorruptedImport(ProductData productData, List<File> rawFiles, Throwable throwable) {
    List<ObjectId> savedRawFileIds = new ArrayList<>();

    try {
      savedRawFileIds.addAll(gridFsRepository.saveError(rawFiles));
    } catch (final FileNotFoundException exception) {
      LOG.error("Could not save errors", exception);
    }

    CorruptedImport corruptedImport = new CorruptedImport();

    corruptedImport.setProductId(productData.getProductId());
    corruptedImport.setRawFileIds(savedRawFileIds);
    corruptedImport.setErrorMessage(throwable.getMessage());

    StringBuilder stackTraceBuilder = new StringBuilder();

    Arrays.stream(throwable.getStackTrace()).forEach(stackTraceElement ->
      stackTraceBuilder.append(stackTraceElement).append(StringUtils.LF)
    );

    corruptedImport.setStackTrace(stackTraceBuilder.toString());

    LOG.info("Save corrupted Import");

    corruptedImport.setImportDate(productData.getImportDate());
    corruptedImport.setCompressedFileId(productData.getFileId());
    CorruptedImport savedData = corruptedImportRepository.save(corruptedImport);
    return savedData.getId();
  }
}
