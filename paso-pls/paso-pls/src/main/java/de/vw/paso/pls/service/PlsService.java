package de.vw.paso.pls.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.vw.paso.pll.creation.PartListCreationConfiguration;
import de.vw.paso.pll.creation.PartListCreationResult;
import de.vw.paso.pll.creation.PartListCreator;
import de.vw.paso.pll.model.FilteredOutPart;
import de.vw.paso.pls.exception.ErrorCode;
import de.vw.paso.pls.exception.PlsException;
import de.vw.paso.pls.model.ImportStatus;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.model.dto.ImportStatusDto;
import de.vw.paso.pls.model.dto.PlsPartListDto;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlsService {

  private static final Logger LOG = LoggerFactory.getLogger(PlsService.class);

  private final ProductDataService productDataService;
  private final TiWhImportService tiWhImportService;

  public PlsService(ProductDataService productDataService, TiWhImportService tiWhImportService) {
    this.productDataService = productDataService;
    this.tiWhImportService = tiWhImportService;
  }

  public PlsPartListDto createSingleVehiclePartList(ObjectId productDataId, String prNumber, LocalDate validDate) {
    ProductData productData = productDataService.findById(productDataId)
      .orElseThrow(() -> new PlsException(ErrorCode.PART_LIST_NOT_FOUND));
    if (!ImportStatus.READY.equals(productData.getImportStatus())) {
      throw new PlsException(ErrorCode.PART_LIST_NOT_READY);
    }

    LOG.info("Create part list for product data id: {}, product id: {}", productDataId, productData.getProductId());
    try {
      PlsPartListDto partListResponse = createPartListResponse(productData, prNumber, validDate);
      if (partListResponse.rootElement() == null) {
        LOG.error("No root element found in part list");
        saveProductDataError(productData);
        return null;
      }

      return partListResponse;
    } catch (Exception e) {
      LOG.error("Error occurred while processing part list", e);
      saveProductDataError(productData);
      return null;
    }
  }

  private void saveProductDataError(ProductData productData) {
    productData.setImportStatus(ImportStatus.ERROR);
    productDataService.save(productData);
  }

  public ImportStatusDto getProductDataStatus(ObjectId productDataId) {
    Optional<ProductData> productDataOptional = productDataService.findById(productDataId);
    if (productDataOptional.isEmpty()) {
      LOG.info("No product data found for id: {}", productDataId);
      return new ImportStatusDto(productDataId, ImportStatus.UNKNOWN);
    }

    ProductData productData = productDataOptional.get();
    LOG.info("Product data found for id: {}, product id: {} with status: {}", productDataId,
            productData.getProductId(), productData.getImportStatus());
    return new ImportStatusDto(productDataId, productData.getImportStatus());
  }

  public ImportStatusDto requestPartList(final String productId, final LocalDate importDate, final String requester) {
    LOG.info("Part list requested for product id: {}, import date: {}", productId, importDate);
    ProductData productData = productDataService.findByProductIdAndImportDate(productId, importDate);

    if (productData == null) {
      LOG.info("No product data for request found. Create a new and request from TiWh");
      productData = requestNewProductData(productId, requester);
    } else {
      LOG.info("Product data exists with id: {}, product id: {}, import date: {}, status: {}", productData.getId(),
        productId, importDate, productData.getImportStatus());
      if (productData.getImportStatus().equals(ImportStatus.PENDING)) {
        LOG.info("Product data is still requested. Add: {} as new requester", requester);
        tiWhImportService.addTiWhRequest(productId, requester);
      } else if (!productData.getImportStatus().equals(ImportStatus.READY)) {
        LOG.info("Product data status is {}. Will request product data again", productData.getImportStatus());
        productData = requestProductDataAgain(productData, requester);
      }
    }

    return new ImportStatusDto(productData.getId(), productData.getImportStatus());
  }

  private List<FilteredOutPart> collectFilteredOutParts(PartListCreationResult result) {
    return result.getFilteredOutEfsElements();
  }

  private Date convertToDate(LocalDate dateToConvert) {
    return Date.from(dateToConvert.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private PlsPartListDto createPartListResponse(ProductData productData, String prNumbers, LocalDate validDate) {
    if (productData.getFileId() == null) {
      throw new IllegalArgumentException("No file found for ProductData id: " + productData.getId());
    }

    byte[] input = productDataService.loadProductDataFile(productData.getFileId());

    Iterator<String> lines;
    try (ByteArrayInputStream in = new ByteArrayInputStream(input); ZipInputStream zipIn = new ZipInputStream(in)) {
      ZipEntry nextEntry = zipIn.getNextEntry();
      if (nextEntry == null) {
        LOG.error("Incoming zip has no entry");
        throw new PlsException(ErrorCode.UNKNOWN);
      }
      try (InputStreamReader stream = new InputStreamReader(zipIn);
        BufferedReader reader = new BufferedReader(stream)) {
        lines = reader.lines().toList().iterator();
      }
    } catch (IOException e) {
      LOG.error("Could not read bytes", e);
      throw new PlsException(ErrorCode.UNKNOWN);
    }
    PartListCreationResult result = processLines(prNumbers, validDate, lines);

    return new PlsPartListDto(productData.getId().toString(), convertToDate(productData.getImportDate()),
      result.getRootElement(), collectFilteredOutParts(result));
  }

  private PartListCreationResult processLines(String prNumbers, LocalDate validDate, Iterator<String> lines) {
    PartListCreationConfiguration config = new PartListCreationConfiguration(lines, prNumbers, validDate);
    return new PartListCreator().createPartList(config);
  }

  private ProductData requestProductDataAgain(ProductData productData, String requester) {
    productDataService.deleteProductData(productData);

    return requestNewProductData(productData.getProductId(), requester);
  }

  private ProductData requestNewProductData(String productId, String requester) {
    ProductData newProductData = productDataService.saveProductData(productId);
    tiWhImportService.requestTiWhData(newProductData.getProductId(), requester);

    return newProductData;
  }
}
