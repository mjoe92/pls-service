package de.vw.paso.service.partlist.efsedit;

import java.util.Collection;

public record SaveEfsElementListDTO(Collection<EfsElementDTO> changedElements) { }