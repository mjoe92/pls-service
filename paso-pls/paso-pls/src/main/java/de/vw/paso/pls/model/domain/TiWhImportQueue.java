package de.vw.paso.pls.model.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = TiWhImportQueue.DOCUMENT_TI_WH_IMPORT_QUEUE)
public class TiWhImportQueue {

    private static final String REQUEST_SEQUENCE = "requestSequence";
    private static final String REQUESTED = "requested";
    private static final String PROCESSING = "processing";
    private static final String PENDING_START = "pendingStart";
    private static final String REQUESTER_IDS = "requesterIds";

    static final String DOCUMENT_TI_WH_IMPORT_QUEUE = "tiWhImportQueue";

    @Id
    private String productId;

    @Field(value = REQUEST_SEQUENCE, order = 20)
    private LocalDateTime requestSequence;

    @Field(value = REQUESTER_IDS, order = 40)
    private Set<String> requesterIds = new HashSet<>();

    @Field(value = REQUESTED, order = 50)
    private boolean requested;

    @Field(value = PROCESSING, order = 70)
    private boolean processing;

    @Field(value = PENDING_START, order = 60)
    private LocalDateTime pendingStart;

    public TiWhImportQueue(final String productId) {
        this.productId = productId;
        this.requestSequence = LocalDateTime.now();
    }

    public String getProductId() {
        return productId;
    }

    public LocalDateTime getRequestSequence() {
        return requestSequence;
    }

    public Set<String> getRequesterIds() {
        return requesterIds;
    }

    public boolean isRequested() {
        return requested;
    }

    public LocalDateTime getPendingStart() {
        return pendingStart;
    }

    public void setPendingStart(LocalDateTime pendingStart) {
        this.pendingStart = pendingStart;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public boolean isProcessing() {
        return processing;
    }

    @Override
    public String toString() {
        return "QueueItem- ProductID:" + productId + " requestSequence:" + requestSequence + " requested:" + requested
            + " requester:" + requesterIds;
    }
}
