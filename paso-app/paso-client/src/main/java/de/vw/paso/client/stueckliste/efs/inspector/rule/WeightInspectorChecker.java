package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;

import com.google.common.collect.ListMultimap;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.client.util.QuantityUnit;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class WeightInspectorChecker extends AbstractInspectorChecker {

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
        VehicleConfigDTO vehicleConfig) {

        for (EfsElementDTO element : efsElements) {
            QuantityUnit quantityUnit = QuantityUnit.getByShortName(element.getQuantityUnit());

            if (QuantityUnit.UNKNOWN == quantityUnit) {
                if (hasWeightSet(element) && isIncluded(element, InspectorEntryType.WEIGHT_BUT_NO_UNIT)) {
                    InspectorEntry entry = new InspectorEntry(element, InspectorEntryType.WEIGHT_BUT_NO_UNIT,
                        InspectorEntryType.WEIGHT_BUT_NO_UNIT.name());
                    resultMap.put(entry.getType(), entry);
                }
            } else if (QuantityUnit.MILLIMETER == quantityUnit) {
                if (hasWeightSet(element) && isIncluded(element,
                    InspectorEntryType.WEIGHT_DEVIATION_BASED_ON_THE_UNIT)) {
                    InspectorEntry entry = new InspectorEntry(element,
                        InspectorEntryType.WEIGHT_DEVIATION_BASED_ON_THE_UNIT, QuantityUnit.MILLIMETER.toString());

                    resultMap.put(entry.getType(), entry);
                }
            } else if (QuantityUnit.MILLILITER == quantityUnit) {
                if (hasWeightSet(element) && isIncluded(element,
                    InspectorEntryType.WEIGHT_DEVIATION_BASED_ON_THE_UNIT)) {
                    InspectorEntry entry = new InspectorEntry(element,
                        InspectorEntryType.WEIGHT_DEVIATION_BASED_ON_THE_UNIT, QuantityUnit.MILLILITER.toString());

                    resultMap.put(entry.getType(), entry);
                }
            } else if (QuantityUnit.GRAMM == quantityUnit) {
                if (hasNoWeightSet(element) && isIncluded(element, InspectorEntryType.UNIT_GRAMM_WITHOUT_WEIGHT)) {
                    InspectorEntry entry = new InspectorEntry(element, InspectorEntryType.UNIT_GRAMM_WITHOUT_WEIGHT,
                        InspectorEntryType.UNIT_GRAMM_WITHOUT_WEIGHT.name());

                    resultMap.put(entry.getType(), entry);
                }

                if (hasWeightSet(element) && isIncluded(element,
                    InspectorEntryType.WEIGHT_DEVIATION_BASED_ON_THE_UNIT)) {
                    InspectorEntry entry = new InspectorEntry(element,
                        InspectorEntryType.WEIGHT_DEVIATION_BASED_ON_THE_UNIT, QuantityUnit.GRAMM.toString());

                    resultMap.put(entry.getType(), entry);
                }
            }
        }
    }

    private boolean hasNoWeightSet(EfsElementDTO element) {
        EfsElementMaraDTO mara = element.getEfsElementMara();

        return isEmpty(mara.getWeightCalculatedTe()) && isEmpty(mara.getWeightEstimatedTe()) && isEmpty(
            mara.getWeightWeightedProd()) && isEmpty(mara.getWeightWeightedTe());
    }

    private boolean hasWeightSet(EfsElementDTO element) {
        EfsElementMaraDTO mara = element.getEfsElementMara();

        return !isEmpty(mara.getWeightCalculatedTe()) || !isEmpty(mara.getWeightEstimatedTe()) || !isEmpty(
            mara.getWeightWeightedProd()) || !isEmpty(mara.getWeightWeightedTe());
    }

    private boolean isEmpty(Number number) {
        return number == null || number.doubleValue() == 0d;
    }
}
