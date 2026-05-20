package de.vw.paso.logic.partlist;

import java.util.List;
import java.util.Objects;

import de.vw.paso.partlist.domain.CostGroup;
import de.vw.paso.repository.partlist.CostGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CostGroupManager {

    private final CostGroupRepository costGroupRepository;

    public List<CostGroup> loadCostGroups() {
        return costGroupRepository.findAll();
    }

    public List<CostGroup> loadCostGroups(final Long costGroupsVersion) {
        return (costGroupsVersion == null) ? costGroupRepository.findAllRelevant() :
                costGroupRepository.findAllByIdVersion(costGroupsVersion);
    }

    @Transactional
    public CostGroup saveCostGroup(final CostGroup newCostGroup) {
        if (costGroupRepository.existsById(newCostGroup.getId())) {
            return null;
        }

        return costGroupRepository.save(newCostGroup);
    }

    @Transactional
    public CostGroup updateCostGroup(final CostGroup oldCostGroup, final CostGroup newCostGroup) {
        if (!Objects.equals(oldCostGroup.getCostGroup(), newCostGroup.getCostGroup()) && (
                costGroupRepository.existsById(newCostGroup.getId())
                        || oldCostGroup.getCostGroup().length() < newCostGroup.getCostGroup().length())) {
            return null;
        }

        final List<CostGroup> costGroupsToUpdate = costGroupRepository.findAllChildren(oldCostGroup.getCostGroup(),
                oldCostGroup.getVersion());

        costGroupsToUpdate.forEach(costGroup -> {
            final String newCostGroupValue = costGroup.getCostGroup()
                    .replaceFirst(oldCostGroup.getCostGroup(), newCostGroup.getCostGroup());

            String newParentCostGroupValue = null;

            if (costGroup.getCostGroup().equals(oldCostGroup.getCostGroup())) {
                newParentCostGroupValue = newCostGroup.getParentCostGroup();
                costGroup.setDescription(newCostGroup.getDescription());
            } else if (costGroup.getParent() != null) {
                newParentCostGroupValue = costGroup.getParent().getCostGroup()
                        .replaceFirst(oldCostGroup.getCostGroup(), newCostGroup.getCostGroup());
            }

            costGroupRepository.updateCostGroups(newCostGroupValue, costGroup.getVersion(), costGroup.getDescription(),
                    newParentCostGroupValue, costGroup.getCostGroup());
        });

        return newCostGroup;
    }

    @Transactional
    public void removeCostGroup(Long version, String costGroupName) {
        costGroupRepository.removeCostGroups(costGroupName, version);
    }

}
