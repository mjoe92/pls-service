package de.vw.paso.service.pls;

import java.util.Collection;
import java.util.Map;

/**
 * @param vehicleConfigId
 *         the id of the vehicle config
 * @param prFamilyIdToPrNumbersIdMap
 *         the map of PR-Family id to ids of PR-number with assignment
 */
public record CreatePartListDTO(Long vehicleConfigId, Map<Long, Collection<Long>> prFamilyIdToPrNumbersIdMap) { }