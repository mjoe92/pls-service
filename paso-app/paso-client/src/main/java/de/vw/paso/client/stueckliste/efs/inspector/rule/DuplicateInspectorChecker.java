package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;

import com.google.common.collect.ListMultimap;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class DuplicateInspectorChecker extends AbstractInspectorChecker {

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
        VehicleConfigDTO vehicleConfig) {
        for (EfsElementDTO element : efsElements) {
            if (element.isDeleted() || element.getDuplicateId() == null || !isIncluded(element,
                InspectorEntryType.DUPLICATE)) {
                continue;
            }

            InspectorEntry entry = new InspectorEntry(element, InspectorEntryType.DUPLICATE, element.getDuplicateId());
            resultMap.put(entry.getType(), entry);
        }
    }
}
