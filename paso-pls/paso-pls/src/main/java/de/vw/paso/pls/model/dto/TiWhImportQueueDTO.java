package de.vw.paso.pls.model.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record TiWhImportQueueDTO(String productId, LocalDateTime requestSequence, Set<String> requesterIds,
                                 LocalDateTime pendingStarted, boolean requested, boolean processing) {

}
