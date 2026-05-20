package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.SpecialPartNumberType;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;

public class NoMaraInspectorChecker extends AbstractInspectorChecker {

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
            ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
            VehicleConfigDTO vehicleConfig) {
        for (EfsElementDTO element : efsElements) {
            if ((!element.isDeleted() && element.getEfsElementMara() == null || StringUtils.isEmpty(
                    element.getEfsElementMara().getPartNumber()) || element.getEfsElementMara().getPartNumber()
                    .equalsIgnoreCase(SpecialPartNumberType.NO_MARA.getLabel())) && isIncluded(element,
                    InspectorEntryType.NO_MARA)) {
                InspectorEntry entry = new InspectorEntry(element, InspectorEntryType.NO_MARA,
                        element.getEfsElementMara() != null ? element.getEfsElementMara().getPartNumber() : null);
                resultMap.put(entry.getType(), entry);
            }
        }
    }
}
