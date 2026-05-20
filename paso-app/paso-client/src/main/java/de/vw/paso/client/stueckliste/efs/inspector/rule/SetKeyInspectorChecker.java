package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;

public class SetKeyInspectorChecker extends AbstractInspectorChecker {

    private static final String NOT_RELEVANT_SET_KEY = "I1";

    public SetKeyInspectorChecker() {
    }

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
            ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
            VehicleConfigDTO vehicleConfig) {
        for (EfsElementDTO efsElement : efsElements) {
            if (!efsElement.isDeleted() && StringUtils.isEmpty(efsElement.getSetKey())) {
                if (isIncluded(efsElement, InspectorEntryType.MISSING_SET_KEY)) {
                    InspectorEntry entry = new InspectorEntry(efsElement, InspectorEntryType.MISSING_SET_KEY,
                            InspectorEntryType.MISSING_SET_KEY.name());

                    resultMap.put(entry.getType(), entry);
                }
            } else {
                if (!efsElement.isDeleted() && !efsElement.getSetKey().equals(NOT_RELEVANT_SET_KEY) && (!isKnownSetKey(
                        vehicleConfig.getSetVersionId(), efsElement.getSetKey())) && isIncluded(efsElement,
                        InspectorEntryType.UNKNOWN_SET_KEY)) {
                    InspectorEntry entry = new InspectorEntry(efsElement, InspectorEntryType.UNKNOWN_SET_KEY,
                            efsElement.getSetKey());

                    resultMap.put(entry.getType(), entry);
                }
            }
        }
    }

    private boolean isKnownSetKey(Long setKeyVersion, String toCheck) {
        return CacheManager.getSetKeys(setKeyVersion).stream()
                .anyMatch(setKey -> setKey.getSetKeyName().equals(toCheck));
    }

}
