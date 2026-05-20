package de.vw.paso.compare.fgset;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;

public class FGSetCompare {

    private static final String NOT_RELEVANT_SET_KEY = "I1";

    private Map<String, SetKeyDTO> setKeys;

    public FGSetCompare(Map<String, SetKeyDTO> setKeys) {
        this.setKeys = setKeys;
    }

    public FgSetCompareResult compare(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO reference) {
        FgSetCompareResult result = new FgSetCompareResult(vehicleConfigs, reference);
        for (VehicleConfigDTO config : vehicleConfigs) {
            Set<EfsElementDTO> elementsOfPArtList = EfsElementResolver.getElementsInPartList(
                    config.getVehiclePartList());
            processElements(elementsOfPArtList, config.getId(), result);
        }
        //Triggers calculation and caching of weights
        vehicleConfigs.forEach(e -> result.getRoot().getWeights(e.getId()));
        return result;
    }

    private void processElements(Set<EfsElementDTO> elementsOfPArtList, Long vehicleConfigId,
            FgSetCompareResult result) {
        for (EfsElementDTO element : elementsOfPArtList) {
            if (element.getSetKey() != null && !element.getSetKey().equals(NOT_RELEVANT_SET_KEY)) {
                if (setKeys.containsKey(element.getSetKey())) {
                    FGSetCompareRow row = result.getRowForSet(element.getSetKey());
                    if (row == null) {
                        row = createRow(setKeys.get(element.getSetKey()), result);
                    }
                    row.addDataSet(element, vehicleConfigId);
                } else {
                    FGSetCompareRow row = result.getRowForSet(element.getSetKey());
                    if (row == null) {
                        row = new FGSetCompareRow(element.getSetKey());
                        result.addRow(row);
                        FGSetCompareRow unknownSetKeyRow = result.getRowForSet(null);
                        if (unknownSetKeyRow == null) {
                            unknownSetKeyRow = new FGSetCompareRow();
                            result.addRow(unknownSetKeyRow);
                            result.addRootNode(unknownSetKeyRow);
                        }
                        unknownSetKeyRow.addChildRow(row);
                    }

                    row.addDataSet(element, vehicleConfigId);
                }
            }
        }
    }

    private FGSetCompareRow createRow(SetKeyDTO setKey, FgSetCompareResult result) {
        FGSetCompareRow row = new FGSetCompareRow(setKey.getSetKeyName());
        String parentSetKey = getParentSetKey(setKey);
        if (parentSetKey != null) {
            FGSetCompareRow parentRow = result.getRowForSet(parentSetKey);
            if (parentRow == null) {
                parentRow = createRow(setKeys.get(parentSetKey), result);
            }
            parentRow.addChildRow(row);
        } else {
            result.addRootNode(row);
        }
        result.addRow(row);
        return row;
    }

    private String getParentSetKey(SetKeyDTO setKey) {
        if (setKey == null) {
            return null;
        }
        return setKey.getParentName();
    }

}
