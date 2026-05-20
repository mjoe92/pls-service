package de.vw.paso.client.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.vw.paso.delegate.partgroup.PartGroupRestClientHolder;
import de.vw.paso.delegate.stammdaten.prnumber.PrNumberRestClientHolder;
import de.vw.paso.delegate.stammdaten.vehicleprojct.VehicleProjectRestClientHolder;
import de.vw.paso.delegate.stueckliste.costgroup.CostGroupRestClientHolder;
import de.vw.paso.delegate.stueckliste.setkey.SetKeyRestClientHolder;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;
import de.vw.paso.utility.StringConstant;

public final class CacheManager {

    private CacheManager() {
        throw new IllegalArgumentException("Util class");
    }

    public static CostGroupDTO getCostGroupByIdAndName(Long costGroupVersionId, String parentCostGroupName) {
        return Cache.getInstance().getCostGroupByIdAndName(costGroupVersionId, parentCostGroupName);
    }

    public static List<CostGroupDTO> getCostGroups(Long costGroupsVersion) {
        if (costGroupsVersion == null) {
            return List.of();
        }

        List<CostGroupDTO> costGroups = Cache.getInstance().getCostGroups(costGroupsVersion);
        if (costGroups == null) {
            costGroups = CostGroupRestClientHolder.getInstance().loadCostGroups(costGroupsVersion).costGroupDTOs();

            Cache.getInstance().setCostGroups(costGroupsVersion, costGroups);
        }

        return costGroups;
    }

    public static Collection<CostGroupDTO> getCostGroups() {
        Collection<CostGroupDTO> costGroups = Cache.getInstance().getCostGroups();

        if (costGroups.isEmpty()) {
            costGroups = CostGroupRestClientHolder.getInstance().loadCostGroups().costGroupDTOs();

            Cache.getInstance().setCostGroups(costGroups);
        }

        return costGroups;
    }

    public static List<String> getCostGroupsAsStrings(Collection<CostGroupDTO> costGroups) {
        return Objects.requireNonNull(costGroups).stream().map(costGroup -> costGroup.getDescription() != null ?
                costGroup.getCostGroupName() + StringConstant.SPACE_DASH_SPACE + costGroup.getDescription() :
                costGroup.getCostGroupName()).toList();
    }

    public static List<PartGroupDTO> getPartGroups() {
        List<PartGroupDTO> partGroups = Cache.getInstance().getPartGroups();

        if (partGroups.isEmpty()) {
            partGroups = PartGroupRestClientHolder.getInstance().loadPartGroups().partGroupDTOs();

            Cache.getInstance().setPartGroups(partGroups);
        }

        return partGroups;
    }

    public static PrNumberDTO getPrNumber(String prNumberStr) {
        ensurePrNumbers();

        PrNumberDTO prNumber = Cache.getInstance().getPrNumbers(prNumberStr);
        return prNumber == null ? Cache.getInstance().getPrNumbers().iterator().next() : prNumber;
    }

    public static List<PrNumberDTO> getPrNumbers(Collection<String> prNumberStr) {
        ensurePrNumbers();

        List<PrNumberDTO> result = new ArrayList<>(prNumberStr.size());
        for (String prStr : prNumberStr) {
            PrNumberDTO prNumber = Cache.getInstance().getPrNumbers(prStr);
            if (prNumber == null) {
                continue;
            }

            result.add(prNumber);
        }

        return result;
    }

    public static List<SetKeyDTO> getSetKeys(Long setVersionId) {
        if (setVersionId == null) {
            return List.of();
        }

        List<SetKeyDTO> setKeyDTOs = Cache.getInstance().getSetKeys(setVersionId);
        if (setKeyDTOs == null) {
            setKeyDTOs = SetKeyRestClientHolder.getInstance().loadSetKeys(setVersionId).setKeys();

            Cache.getInstance().setSetKeys(setVersionId, setKeyDTOs);
        }

        return setKeyDTOs;
    }

    public static List<SetKeyDTO> getSetKeys() {
        List<SetKeyDTO> setKeys = Cache.getInstance().getSetKeys();
        if (setKeys.isEmpty()) {
            setKeys = SetKeyRestClientHolder.getInstance().loadSetKeys().setKeys();

            Cache.getInstance().setSetKeys(setKeys);
        }

        return setKeys;
    }

    public static void updateSetKeys(List<SetKeyDTO> newSetKeys) {
        Cache.getInstance().updateSetKeys(newSetKeys);
    }

    public static void removeSetKey(SetKeyDTO toRemove) {
        Collection<SetKeyDTO> cached = getSetKeys(toRemove.getSetVersionId());
        cached.removeIf(element -> element.getSetKeyName().equals(toRemove.getSetKeyName()));

        Map<String, List<SetKeyDTO>> parentMap = cached.stream().filter(setKey -> setKey.getParentName() != null)
                .collect(Collectors.groupingBy(SetKeyDTO::getParentName));
        Collection<SetKeyDTO> listToRemove = collectSetKeyDescendants(toRemove.getSetKeyName(), parentMap);

        cached.removeAll(listToRemove);
    }

    private static Collection<SetKeyDTO> collectSetKeyDescendants(String nodeName,
            Map<String, List<SetKeyDTO>> parentMap) {
        Collection<SetKeyDTO> descendants = new ArrayList<>();
        Collection<SetKeyDTO> directChildren = parentMap.getOrDefault(nodeName, List.of());
        for (SetKeyDTO child : directChildren) {
            descendants.add(child);

            Collection<SetKeyDTO> childDescendants = collectSetKeyDescendants(child.getSetKeyName(), parentMap);
            descendants.addAll(childDescendants);
        }

        return descendants;
    }

    public static List<String> getSetKeysAsStrings(List<SetKeyDTO> setKeys) {
        return Objects.requireNonNull(setKeys).stream().map(setKey -> (setKey.getDescription() != null) ?
                setKey.getSetKeyName() + StringConstant.SPACE_DASH_SPACE + setKey.getDescription() :
                setKey.getSetKeyName()).toList();
    }

    public static List<VehicleProjectDTO> getVehicleProjects() {
        List<VehicleProjectDTO> vehicleProjects = Cache.getInstance().getVehicleProjects();

        if (vehicleProjects.isEmpty()) {
            vehicleProjects = VehicleProjectRestClientHolder.getInstance().loadVehicleProjects()
                    .vehicleProjectDTOList();

            Cache.getInstance().setVehicleProjects(vehicleProjects);
        }

        return vehicleProjects;
    }

    public static void initializeCache() {
        CacheManager.getSetKeys();
        CacheManager.getCostGroups();
        CacheManager.getPartGroups();
        CacheManager.getVehicleProjects();
        CacheManager.ensurePrNumbers();
    }

    private static void ensurePrNumbers() {
        if (Cache.getInstance().getPrNumbers().isEmpty()) {
            Collection<PrNumberDTO> newNumbers = PrNumberRestClientHolder.getInstance().loadAll().prNumberDTOList();
            Cache.getInstance().setPrNumbers(newNumbers);
        }
    }

    public static void invalidateCostGroups() {
        Cache.getInstance().invalidateCostGroups();
    }

    public static void invalidateSetKeys() {
        Cache.getInstance().invalidateSetKeys();
    }

    public static void invalidateVehicleProjects() {
        Cache.getInstance().invalidateVehicleProjects();
    }
}
