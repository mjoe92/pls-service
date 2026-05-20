package de.vw.paso.compare.costgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class CostGroupCompareResult extends AbstractCompareResult {

    private Map<String, CostGroupCompareRow> rowsByCostGroupStr = new HashMap<>();

    private List<VehicleConfigDTO> vehicleConfigs;
    private VehicleConfigDTO reference;

    private List<CostGroupCompareRow> rootRows = new ArrayList<>();

    public CostGroupCompareResult(List<VehicleConfigDTO> configs, VehicleConfigDTO reference) {
        this.vehicleConfigs = configs;
        this.reference = reference;
    }

    public List<VehicleConfigDTO> getVehicleConfigs() {
        return vehicleConfigs;
    }

    @Override
    public VehicleConfigDTO getReference() {
        return reference;
    }

    public CostGroupCompareRow getRowForCostGroup(String costGroup) {
        return rowsByCostGroupStr.get(costGroup);
    }

    public void addRow(CostGroupCompareRow row) {
        rowsByCostGroupStr.put(row.getCostGroupStr(), row);
    }

    public CostGroupCompareRow getRoot() {
        CostGroupCompareRow root = new CostGroupCompareRow();
        rootRows.sort((r1, r2) -> {
            if (r1.getCostGroupStr() == null) {
                return 1;
            } else if (r2.getCostGroupStr() == null) {
                return -1;
            } else {
                return r1.getCostGroupStr().compareTo(r2.getCostGroupStr());
            }
        });
        root.getChildren().addAll(rootRows);
        return root;
    }

    public void addRootNode(CostGroupCompareRow row) {
        rootRows.add(row);
    }

}
