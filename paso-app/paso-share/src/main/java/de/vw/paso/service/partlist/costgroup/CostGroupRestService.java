package de.vw.paso.service.partlist.costgroup;

public interface CostGroupRestService {

    String URL = "/api/cost-group";

    CostGroupListDTO loadCostGroups();

    CostGroupListDTO loadCostGroups(Long costGroupVersion);

    CostGroupsDTO saveCostGroup(CostGroupsDTO costGroupsDTO);

    CostGroupDTO updateCostGroup(UpdateCostGroupDTO updateCostGroupDTO);

    void removeCostGroup(Long version, String costGroupName);
}
