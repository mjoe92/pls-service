package de.vw.paso.pls;

import java.util.Collection;
import java.util.Map;

import de.vw.paso.partlist.dto.EfsElementAggregateMappingDTO;

public record EfsAggregateInformationDTO(Collection<ProductDataDTO> productData, Map<String, Integer> requestPositions,
                                         Collection<EfsElementAggregateMappingDTO> aggregateMappings) { }
