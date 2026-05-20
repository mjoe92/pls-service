package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;

public class CostGroupInspectorChecker extends AbstractInspectorChecker {

    public CostGroupInspectorChecker() {
    }

    @Override
    public void checkElements(final Collection<EfsElementDTO> efsElements,
            final ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
            VehicleConfigDTO vehicleConfig) {
        efsElements.forEach(efsElement -> {
            if (!efsElement.isDeleted() && StringUtils.isEmpty(efsElement.getCostGroup())) {
                if (isIncluded(efsElement, InspectorEntryType.MISSING_COST_GROUP)) {
                    final InspectorEntry entry = new InspectorEntry(efsElement, InspectorEntryType.MISSING_COST_GROUP,
                            InspectorEntryType.MISSING_COST_GROUP.name());

                    resultMap.put(entry.getType(), entry);
                }
            } else {
                if (!efsElement.isDeleted() && !isKnownCostGroup(vehicleConfig.getCostGroupVersion(),
                        efsElement.getCostGroup()) && isIncluded(efsElement, InspectorEntryType.UNKNOWN_COST_GROUP)) {
                    final InspectorEntry entry = new InspectorEntry(efsElement, InspectorEntryType.UNKNOWN_COST_GROUP,
                            efsElement.getCostGroup());

                    resultMap.put(entry.getType(), entry);
                }
            }
        });
    }

    private boolean isKnownCostGroup(final Long costGroupVersion, final String costGroup) {
        return CacheManager.getCostGroups(costGroupVersion).stream()
                .anyMatch(e -> e.getCostGroupName().equals(costGroup));
    }

}
