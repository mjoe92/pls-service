package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class WahlweiseInspectorChecker extends AbstractInspectorChecker {

    private static final Set<Integer> validWahlweiseNr = new HashSet<>(Arrays.asList(0, 1));

    @Override
    public void checkElements(final Collection<EfsElementDTO> efsElements,
            final ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
            VehicleConfigDTO vehicleConfig) {
        efsElements.forEach(e -> {
            if (wahlweiseValid(e) && isIncluded(e, InspectorEntryType.WAHLWEISENR_NOT_1)) {
                resultMap.put(InspectorEntryType.WAHLWEISENR_NOT_1,
                        new InspectorEntry(e, InspectorEntryType.WAHLWEISENR_NOT_1));
            }
        });
    }

    private boolean wahlweiseValid(final EfsElementDTO efsElement) {
        return (efsElement.getWahlweiseNr() != null) && !validWahlweiseNr.contains(efsElement.getWahlweiseNr());
    }

}
