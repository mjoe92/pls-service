package de.vw.paso.client.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;

final class Cache {

    private final Map<Long, List<SetKeyDTO>> setKeys;
    private final Map<Long, List<CostGroupDTO>> costGroups;
    private final List<PartGroupDTO> partGroups;
    private final List<VehicleProjectDTO> vehicleProjects;
    private final Map<String, PrNumberDTO> prNumbers;

    private Cache() {
        setKeys = Collections.synchronizedMap(new HashMap<>());
        costGroups = Collections.synchronizedMap(new HashMap<>());
        vehicleProjects = Collections.synchronizedList(new ArrayList<>());
        partGroups = Collections.synchronizedList(new ArrayList<>());
        prNumbers = Collections.synchronizedMap(new HashMap<>());
    }

    public void invalidateCostGroups() {
        costGroups.clear();
    }

    public void invalidateSetKeys() {
        setKeys.clear();
    }

    public void invalidateVehicleProjects() {
        vehicleProjects.clear();
    }

    public void setCostGroups(Collection<CostGroupDTO> costGroups) {
        Map<Long, List<CostGroupDTO>> idToSetKeys = costGroups.stream()
            .collect(Collectors.toMap(CostGroupDTO::getVersion, List::of, this::mergeList));

        for (Entry<Long, List<CostGroupDTO>> entry : idToSetKeys.entrySet()) {
            setCostGroups(entry.getKey(), entry.getValue());
        }
    }

    void setPrNumbers(Collection<PrNumberDTO> prNumbers) {
        for (PrNumberDTO dto : prNumbers) {
            this.prNumbers.put(dto.name(), dto);
        }
    }

    CostGroupDTO getCostGroupByIdAndName(long costGroupVersion, String name) {
        return costGroups.get(costGroupVersion).stream()
            .filter(costGroupDTO -> costGroupDTO.getCostGroupName().equals(name)).findFirst().orElse(null);
    }

    List<CostGroupDTO> getCostGroups(long costGroupsVersion) {
        return costGroups.get(costGroupsVersion);
    }

    List<CostGroupDTO> getCostGroups() {
        return costGroups.values().stream().flatMap(Collection::stream).toList();
    }

    static Cache getInstance() {
        return CacheHolder.INSTANCE;
    }

    List<PartGroupDTO> getPartGroups() {
        return partGroups;
    }

    PrNumberDTO getPrNumbers(String prNr) {
        return prNumbers.get(prNr);
    }

    public Collection<PrNumberDTO> getPrNumbers() {
        return prNumbers.values();
    }

    List<SetKeyDTO> getSetKeys(long setKeysVersion) {
        return setKeys.get(setKeysVersion);
    }

    List<SetKeyDTO> getSetKeys() {
        return setKeys.values().stream().flatMap(Collection::stream).toList();
    }

    List<VehicleProjectDTO> getVehicleProjects() {
        return vehicleProjects;
    }

    void setCostGroups(long costGroupsVersion, List<CostGroupDTO> newCostGroups) {
        if (!costGroups.containsKey(costGroupsVersion)) {
            costGroups.put(costGroupsVersion, new ArrayList<>());
        }

        List<CostGroupDTO> storedCostGroups = costGroups.get(costGroupsVersion);

        storedCostGroups.clear();
        storedCostGroups.addAll(newCostGroups);
    }

    void setPartGroups(List<PartGroupDTO> newPartGroups) {
        partGroups.clear();
        partGroups.addAll(newPartGroups);
    }

    void setSetKeys(List<SetKeyDTO> setKeys) {
        Map<Long, List<SetKeyDTO>> idToSetKeys = setKeys.stream()
            .collect(Collectors.toMap(SetKeyDTO::getSetVersionId, List::of, this::mergeList));

        for (Entry<Long, List<SetKeyDTO>> entry : idToSetKeys.entrySet()) {
            setSetKeys(entry.getKey(), entry.getValue());
        }
    }

    void setSetKeys(long setVersionId, List<SetKeyDTO> newSetKeys) {
        if (!setKeys.containsKey(setVersionId)) {
            setKeys.put(setVersionId, new ArrayList<>());
        }

        List<SetKeyDTO> storedSetKeys = setKeys.get(setVersionId);

        storedSetKeys.clear();
        storedSetKeys.addAll(newSetKeys);
    }

    void updateSetKeys(List<SetKeyDTO> newSetKeys) {
        setKeys.clear();
        setSetKeys(newSetKeys);
    }

    void setVehicleProjects(List<VehicleProjectDTO> newVehicleProjects) {
        vehicleProjects.clear();
        vehicleProjects.addAll(newVehicleProjects);
    }

    private <T> List<T> mergeList(List<T> first, List<T> second) {
        List<T> merged = new ArrayList<>(first.size() + second.size());
        merged.addAll(first);
        merged.addAll(second);

        return merged;
    }

    private static class CacheHolder {

        private static final Cache INSTANCE = new Cache();
    }
}
