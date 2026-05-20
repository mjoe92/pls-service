package de.vw.paso.service.partlist.efselementhistory;

import java.util.Collection;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public record EfsElementCollection(Collection<EfsElementDTO> efsElementList) { }
