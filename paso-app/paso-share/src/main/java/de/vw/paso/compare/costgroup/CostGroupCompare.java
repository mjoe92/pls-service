package de.vw.paso.compare.costgroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.EfsElementResolver;

public class CostGroupCompare {

    private Map<String, CostGroupDTO> costGroups;

    public CostGroupCompare(Map<String, CostGroupDTO> costGroups) {
        this.costGroups = costGroups;
    }

    public CostGroupCompareResult compare(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO reference) {
        CostGroupCompareResult result = new CostGroupCompareResult(vehicleConfigs, reference);
        for (VehicleConfigDTO config : vehicleConfigs) {
            Set<EfsElementDTO> elementsOfPartList = EfsElementResolver.getElementsInPartList(
                    config.getVehiclePartList());
            processElements(elementsOfPartList, config.getId(), result);
        }
        //Triggers calculation and caching of weights
        vehicleConfigs.forEach(e -> result.getRoot().getWeights(e.getId()));
        return result;
    }

    private void processElements(Set<EfsElementDTO> elementsOfPartList, Long vehicleConfigId,
            CostGroupCompareResult result) {
        for (EfsElementDTO element : elementsOfPartList) {
            if (element.getCostGroup() != null) {
                if (costGroups.containsKey(element.getCostGroup())) {
                    CostGroupCompareRow row = result.getRowForCostGroup(element.getCostGroup());

                    if (row == null) {
                        row = createRow(element.getCostGroup(), result);
                    }

                    row.addDataSet(element, vehicleConfigId);
                } else {
                    CostGroupCompareRow row = result.getRowForCostGroup(element.getCostGroup());
                    if (row == null) {
                        row = new CostGroupCompareRow(element.getCostGroup());
                        result.addRow(row);

                        CostGroupCompareRow unknownCostGroupRow = result.getRowForCostGroup(null);

                        if (unknownCostGroupRow == null) {
                            unknownCostGroupRow = new CostGroupCompareRow();
                            result.addRow(unknownCostGroupRow);
                            result.addRootNode(unknownCostGroupRow);
                        }
                        unknownCostGroupRow.addChildRow(row);
                    }
                    row.addDataSet(element, vehicleConfigId);
                }
            }
        }
    }

    private CostGroupCompareRow createRow(String costGroup, CostGroupCompareResult result) {
        CostGroupCompareRow row = new CostGroupCompareRow(costGroup);
        String parentCostGroup = getParentCostGroup(costGroup);

        if (parentCostGroup != null) {
            CostGroupCompareRow parentRow = result.getRowForCostGroup(parentCostGroup);

            if (parentRow == null) {
                parentRow = createRow(parentCostGroup, result);
            }

            parentRow.addChildRow(row);
        } else {
            result.addRootNode(row);
        }
        result.addRow(row);
        return row;
    }

    private String getParentCostGroup(String costGroup) {
        if (costGroup == null || costGroup.length() < 2) {
            return null;
        }
        return costGroup.substring(0, costGroup.length() - 1);
    }
}
