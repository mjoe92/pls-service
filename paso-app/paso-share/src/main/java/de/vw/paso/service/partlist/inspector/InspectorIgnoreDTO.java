package de.vw.paso.service.partlist.inspector;

import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

public record InspectorIgnoreDTO(InspectorEntryType type, Long efsElementId) {
}
