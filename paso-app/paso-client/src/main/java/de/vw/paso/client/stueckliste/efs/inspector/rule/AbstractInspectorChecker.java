package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public abstract class AbstractInspectorChecker {

    private static final Collection<InspectorEntryType> EXCLUDED_PART_NUMBERS = InspectorEntryType.valuesExcept(
            InspectorEntryType.GAP);

    private static final String TYPE_NAME = "Z_MIO";
    private static final Collection<InspectorEntryType> EXCLUDED_TYPES = List.of(InspectorEntryType.UNKNOWN_SET_KEY,
            InspectorEntryType.MISSING_SET_KEY, InspectorEntryType.UNKNOWN_COST_GROUP,
            InspectorEntryType.MISSING_COST_GROUP, InspectorEntryType.WEIGHT_BUT_NO_UNIT,
            InspectorEntryType.WEIGHT_NOT_SET, InspectorEntryType.GWS_INCORRECT,
            InspectorEntryType.GWS_INCORRECT_NO_WEIGHT);

    public abstract void checkElements(Collection<EfsElementDTO> efsElements,
            ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
            VehicleConfigDTO vehicleConfig);

    protected boolean isIncluded(EfsElementDTO efsElement, InspectorEntryType inspectorEntryType) {
        String partNumber = efsElement.getPartNumber();
        if (partNumber.startsWith(SpecialPartNumberType.GAP.getLabel())) {
            return !EXCLUDED_PART_NUMBERS.contains(inspectorEntryType);
        }

        String nodeType = efsElement.getNodeType();
        if (TYPE_NAME.equals(nodeType)) {
            return !EXCLUDED_TYPES.contains(inspectorEntryType);
        }

        return true;
    }
}