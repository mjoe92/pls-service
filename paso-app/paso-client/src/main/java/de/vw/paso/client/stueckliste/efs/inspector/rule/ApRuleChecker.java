package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;

import com.google.common.collect.ListMultimap;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.AP;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class ApRuleChecker extends AbstractInspectorChecker {

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
        VehicleConfigDTO vehicleConfig) {
        for (EfsElementDTO element : efsElements) {
            if (element == null || element.isDeleted()) {
                continue;
            }

            AP ap = AP.getApByAbbreviation(element.getAp());
            if (ap == null && element.getAp() != null) {
                InspectorEntry entry = new InspectorEntry(element, InspectorEntryType.UNKNOWN_AP, element.getAp());
                resultMap.put(InspectorEntryType.UNKNOWN_AP, entry);
            }
        }
    }
}
