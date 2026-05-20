package de.vw.paso.pls.service;

import static java.util.Objects.isNull;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.vw.paso.pll.RawPartListProcessor;
import de.vw.paso.pll.preprocessing.TiWhFileType;
import de.vw.paso.pls.StaticProductIdReader;
import de.vw.paso.pls.datarequest.AbstractDataRequestStrategy;
import de.vw.paso.pls.datarequest.DataRequestException;
import de.vw.paso.pls.datarequest.DataRequestStrategyFactory;
import de.vw.paso.pls.model.domain.ProductData;
import de.vw.paso.pls.model.domain.TiWhImportQueue;
import de.vw.paso.pls.model.dto.TiWhImportQueueDTO;
import de.vw.paso.pls.repository.TiWhImportQueueRepository;

@Service
public class TiWhImportService {

    private static final Logger LOG = LoggerFactory.getLogger(TiWhImportService.class);

    private final CorruptedImportService corruptedImportService;
    private final ProductDataService productDataService;
    private final TiWhImportQueueRepository tiWhImportQueueRepository;

    @Value("${tiwh.request.timeout:30}")
    private int requestTimeout;

    private AbstractDataRequestStrategy requestStrategy;

    public TiWhImportService(CorruptedImportService corruptedImportService, ProductDataService productDataService,
        TiWhImportQueueRepository tiWhImportQueueRepository, DataRequestStrategyFactory requestStrategyFactory) {
        this.corruptedImportService = corruptedImportService;
        this.productDataService = productDataService;
        this.tiWhImportQueueRepository = tiWhImportQueueRepository;

        try {
            this.requestStrategy = requestStrategyFactory.getStrategy();
        } catch (DataRequestException e) {
            LOG.error("Could not initialize request strategy", e);
        }
    }

    public void addTiWhRequest(String productId, String requesterId) {
        TiWhImportQueue entry = tiWhImportQueueRepository.findByProductId(productId);

        if (entry == null) {
            LOG.info("No entry for product id: {} found", productId);
            entry = new TiWhImportQueue(productId);
        } else {
            LOG.info("Queue entry found for product id: {}", entry.getProductId());
        }

        if (entry.getRequesterIds().add(requesterId)) {
            tiWhImportQueueRepository.save(entry);
        }
    }

    public void checkForNewData() throws DataRequestException {
        if (requestStrategy == null) {
            return;
        }

        LOG.info("Check for requested request");
        TiWhImportQueue pendingRequest = tiWhImportQueueRepository.findByRequestedTrue();
        if (pendingRequest == null) {
            requestTiWh();

            return;
        }

        if (pendingRequest.isProcessing()) {
            LOG.info("Request is still processing");
            return;
        }

        LOG.info("Pending request found for product id: {}", pendingRequest.getProductId());
        if (checkTiWhRequestTimedOut(pendingRequest)) {
            LOG.warn("Pending request timed out for product id: {}", pendingRequest.getProductId());
            handleTimedOutRequest(pendingRequest);
        } else {
            requestStrategy.moveFileFromArchive(pendingRequest.getProductId());
            handlePendingTiWhRequest(pendingRequest);
        }
    }

    public void clearRequestQueue() {
        LOG.info("Clear Request Queue...");
        List<TiWhImportQueue> currRequestQueue = tiWhImportQueueRepository.findAll();
        for (TiWhImportQueue request : currRequestQueue) {
            productDataService.saveTimeoutProductData(request.getProductId());
            tiWhImportQueueRepository.delete(request);
        }
    }

    public void deleteTiWhFiles() {
        if (requestStrategy == null) {
            return;
        }

        try {
            this.requestStrategy.deleteTiWhFiles();
        } catch (DataRequestException exception) {
            LOG.warn("Could not delete TiWh files", exception);
        }
    }

    public TiWhImportQueue getNextTiWhRequest() {
        return tiWhImportQueueRepository.findByRequestedFalse().stream()
            .min(Comparator.comparing(TiWhImportQueue::getRequestSequence)).orElse(null);
    }

    public List<TiWhImportQueueDTO> getQueue() {
        return tiWhImportQueueRepository.findAll().stream().map(TiWhImportService::convertToDto).sorted(
            Comparator.comparing((TiWhImportQueueDTO tiWhImportQueue) -> !tiWhImportQueue.requested())
                .thenComparing(tiWhImportQueue -> !tiWhImportQueue.processing())
                .thenComparing(TiWhImportQueueDTO::requestSequence)).toList();
    }

    public List<String> getValidProductIds() {
        return StaticProductIdReader.read("static_product_ids.txt");
    }

    public void requestTiWhData(final String productId, final String requester) {
        addTiWhRequest(productId, requester);
    }

    private boolean checkTiWhRequestTimedOut(TiWhImportQueue request) {
        LocalDateTime pendingStart = request.getPendingStart();
        return isNull(pendingStart) || LocalDateTime.now().isAfter(pendingStart.plusMinutes(requestTimeout));
    }

    private static TiWhImportQueueDTO convertToDto(TiWhImportQueue queue) {
        return new TiWhImportQueueDTO(queue.getProductId(), queue.getRequestSequence(), queue.getRequesterIds(),
            queue.getPendingStart(), queue.isRequested(), queue.isProcessing());
    }

    private void deleteAndCleanup(TiWhImportQueue queueEntry) {
        deleteTiWhFiles();
        tiWhImportQueueRepository.delete(queueEntry);
    }

    private List<String> getFileNamesFromInbox() throws DataRequestException {
        if (requestStrategy == null) {
            return List.of();
        }

        return requestStrategy.getFileNamesFromInbox();
    }

    private List<File> getFilesFromInbox() throws DataRequestException {
        if (requestStrategy == null) {
            return List.of();
        }

        return requestStrategy.getFilesFromInbox();
    }

    private void handlePendingTiWhRequest(TiWhImportQueue pendingRequest) throws DataRequestException {
        LOG.info("Check if all files are available");

        List<String> newFileNames = getFileNamesFromInbox();
        if (newFileNames.isEmpty()) {
            LOG.info("No files available");
            return;
        }

        if (newFileNames.size() != TiWhFileType.FILE_TYPES.size()) {
            LOG.info("Expecting an exact amount of {} files. {} / {} files available", TiWhFileType.FILE_TYPES.size(),
                newFileNames.size(), TiWhFileType.FILE_TYPES.size());
            return;
        }

        List<File> files = getFilesFromInbox();
        if (files.size() != TiWhFileType.FILE_TYPES.size()) {
            LOG.info("Expecting an exact amount of {} files. {} / {} files available", TiWhFileType.FILE_TYPES.size(),
                files.size(), TiWhFileType.FILE_TYPES.size());
            return;
        }

        processTiWhData(pendingRequest, files);
    }

    private void handleTimedOutRequest(TiWhImportQueue queueEntry) {
        productDataService.saveTimeoutProductData(queueEntry.getProductId());

        deleteAndCleanup(queueEntry);
    }

    private void processTiWhData(TiWhImportQueue pendingRequest, List<File> files) {
        pendingRequest.setProcessing(true);
        tiWhImportQueueRepository.save(pendingRequest);

        try {
            LOG.info("Start processing raw part list files for product id: {}", pendingRequest.getProductId());
            byte[] compressedFile = RawPartListProcessor.processFiles(files);

            productDataService.saveProductData(pendingRequest.getProductId(), compressedFile);
            LOG.info("Saved processed raw part list files for product id: {}", pendingRequest.getProductId());
        } catch (final Exception exception) {
            LOG.error("Could not process raw part list files for product id: {}", pendingRequest.getProductId(),
                exception);
            ProductData productData = productDataService.saveProductDataError(pendingRequest.getProductId());
            ObjectId objectId = corruptedImportService.saveCorruptedImport(productData, files, exception);
            LOG.error("Failed import saved with id: {}", objectId, exception);
        } finally {
            deleteAndCleanup(pendingRequest);
        }
    }

    private void requestTiWh() {
        if (requestStrategy == null) {
            return;
        }

        LOG.info("Check Queue for next requests");
        TiWhImportQueue nextRequest = getNextTiWhRequest();
        if (nextRequest == null) {
            LOG.info("Queue is empty");
            return;
        }

        try {
            deleteTiWhFiles();

            nextRequest.setRequested(true);
            nextRequest.setPendingStart(LocalDateTime.now());
            tiWhImportQueueRepository.save(nextRequest);

            requestStrategy.sendTiWhRequest(nextRequest.getProductId());
        } catch (DataRequestException exception) {
            LOG.error("Could not send TiWh request", exception);
            deleteAndCleanup(nextRequest);
        }
    }
}
