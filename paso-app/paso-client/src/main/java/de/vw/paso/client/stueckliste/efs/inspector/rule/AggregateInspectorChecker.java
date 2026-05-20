package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ListMultimap;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.delegate.stueckliste.inspector.InspectorRestClientHolder;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingDTO;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingListDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class AggregateInspectorChecker extends AbstractInspectorChecker {

  @Override
  public void checkElements(Collection<EfsElementDTO> efsElements,
                            ListMultimap<InspectorEntryType, InspectorEntry> resultMap,
                            EfsElementDTO rootEfsElement, VehicleConfigDTO vehicleConfig) {

    Map<Long, List<EfsElementAggregateMappingDTO>> mappingMap = new HashMap<>();
    for (EfsElementDTO efsElement : efsElements) {
      if (efsElement.isGetriebe()) {
        EfsElementAggregateMappingDTO mapping = loadAggregateMapping(efsElement, mappingMap);
        if (mapping == null) {
          resultMap.put(InspectorEntryType.MISSING_AGGREGATE_GEARBOX, new InspectorEntry(efsElement, InspectorEntryType.MISSING_AGGREGATE_GEARBOX));
        }
      }
      if (efsElement.isMotor()) {
        EfsElementAggregateMappingDTO mapping = loadAggregateMapping(efsElement, mappingMap);
        if (mapping == null) {
          resultMap.put(InspectorEntryType.MISSING_AGGREGATE_ENGINE, new InspectorEntry(efsElement, InspectorEntryType.MISSING_AGGREGATE_ENGINE));
        }
      }
    }
  }

  private EfsElementAggregateMappingDTO loadAggregateMapping(EfsElementDTO element, Map<Long, List<EfsElementAggregateMappingDTO>> loadedMap) {
    if (!loadedMap.containsKey(element.getVehiclePartListId())) {
      EfsElementAggregateMappingListDTO efsElementAggregateMappings = InspectorRestClientHolder.getInstance().loadAggregateMapping(element.getVehiclePartListId());
      loadedMap.put(element.getVehiclePartListId(), Objects.nonNull(efsElementAggregateMappings) ? efsElementAggregateMappings.efsElementAggregateMappingDTOList() : null);
    }
    List<EfsElementAggregateMappingDTO> forPartList = loadedMap.get(element.getVehiclePartListId());
    if (Objects.nonNull(forPartList)) {
      for (EfsElementAggregateMappingDTO mapping : forPartList) {
        if (mapping.efsElementId().equals(element.getId())) {
          return mapping;
        }
      }
    }
    return null;
  }
}
