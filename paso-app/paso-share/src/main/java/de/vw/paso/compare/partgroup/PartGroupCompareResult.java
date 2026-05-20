package de.vw.paso.compare.partgroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.vw.paso.compare.AbstractCompareResult;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class PartGroupCompareResult extends AbstractCompareResult {

    private final List<VehicleConfigDTO> vehicleConfigs;
    private final VehicleConfigDTO reference;

    private Map<String, PartGroupCompareRow> rowsByPartGroup = new HashMap<>();
    private List<PartGroupCompareRow> rootRows = new ArrayList<>();

    public PartGroupCompareResult(List<VehicleConfigDTO> vehicleConfigs, VehicleConfigDTO reference) {
        this.vehicleConfigs = vehicleConfigs;
        this.reference = reference;
    }

    @Override
    public List<VehicleConfigDTO> getVehicleConfigs() {
        return vehicleConfigs;
    }

    @Override
    public VehicleConfigDTO getReference() {
        return reference;
    }

    public PartGroupCompareRow getRowForPartGroup(String partGroupKey) {
        return rowsByPartGroup.get(partGroupKey);
    }

    public void addRow(PartGroupCompareRow row) {
        rowsByPartGroup.put(row.getPartGroupStr(), row);
    }

    public PartGroupCompareRow getRoot() {
        PartGroupCompareRow root = new PartGroupCompareRow();
        Set<PartGroupCompareRow> vnrRoots = new HashSet<>();

        rootRows.sort((r1, r2) -> {
            if (r1.getPartGroupStr() == null) {
                return 1;
            } else if (r2.getPartGroupStr() == null) {
                return -1;
            } else {
                if (r1.getPartGroup().getCategory() >= 100) {
                    vnrRoots.add(r1);
                }
                return r1.getPartGroupStr().compareTo(r2.getPartGroupStr());
            }
        });

        List<PartGroupCompareRow> sortedVnrRoots = new ArrayList<>(vnrRoots);
        sortedVnrRoots.sort(Comparator.comparing(PartGroupCompareRow::getPartGroupStr));

        rootRows.removeAll(sortedVnrRoots);
        rootRows.addAll(rootRows.size() - 1, sortedVnrRoots);

        root.getChildren().addAll(rootRows);

        return root;
    }

    public void addRootNode(PartGroupCompareRow row) {
        rootRows.add(row);
    }

}
