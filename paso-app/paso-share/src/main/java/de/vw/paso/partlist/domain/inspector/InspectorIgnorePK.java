package de.vw.paso.partlist.domain.inspector;

import java.io.Serializable;

public record InspectorIgnorePK(InspectorEntryType type, Long efsElementId) implements Serializable { }
