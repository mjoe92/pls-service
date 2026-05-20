package de.vw.paso.pls;

import java.time.LocalDateTime;
import java.util.Set;

public record TiWhRequestQueueResponse(String productId, LocalDateTime requestSequence, Set<String> requesterIds,
                                       boolean requested, boolean processing) { }
