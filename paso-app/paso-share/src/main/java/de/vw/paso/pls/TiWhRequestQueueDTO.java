package de.vw.paso.pls;

import java.util.Date;
import java.util.Set;

public record TiWhRequestQueueDTO(String productId, Date requestSequence, Set<String> requesterIds, boolean requested,
                                  boolean processing) { }
