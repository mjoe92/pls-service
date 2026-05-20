package de.vw.paso.compare.fgset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class FgSetCompareResult extends AbstractCompareResult {

    private Map<String, FGSetCompareRow> rowsBySetKeyStr = new HashMap<>();

    private List<VehicleConfigDTO> vehicleConfigs;

    private VehicleConfigDTO reference;

    private List<FGSetCompareRow> rootRows = new ArrayList<>();

    public FgSetCompareResult(List<VehicleConfigDTO> configs, VehicleConfigDTO reference) {
        this.vehicleConfigs = configs;
        this.reference = reference;
    }

    public List<VehicleConfigDTO> getVehicleConfigs() {
        return vehicleConfigs;
    }

    public VehicleConfigDTO getReference() {
        return reference;
    }

    public FGSetCompareRow getRowForSet(String setKey) {
        return rowsBySetKeyStr.get(setKey);
    }

    public void addRow(FGSetCompareRow row) {
        rowsBySetKeyStr.put(row.getSetKeyStr(), row);
    }

    public FGSetCompareRow getRoot() {
        FGSetCompareRow root = new FGSetCompareRow();
        rootRows.sort((r1, r2) -> {
            if (r1.getSetKeyStr() == null) {
                return 1;
            } else if (r2.getSetKeyStr() == null) {
                return -1;
            } else {
                return r1.getSetKeyStr().compareTo(r2.getSetKeyStr());
            }
        });
        root.getChildren().addAll(rootRows);
        return root;
    }

    public void addRootNode(FGSetCompareRow row) {
        rootRows.add(row);
    }

}
