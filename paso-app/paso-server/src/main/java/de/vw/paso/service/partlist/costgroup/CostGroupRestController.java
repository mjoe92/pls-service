package de.vw.paso.service.partlist.costgroup;

import java.util.HashSet;
import java.util.List;

import de.vw.paso.logic.partlist.CostGroupManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.partlist.domain.CostGroup;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = CostGroupRestService.URL)
public class CostGroupRestController implements CostGroupRestService {

    private final CostGroupManager manager;
    private final UserManager userManager;

    public CostGroupRestController(CostGroupManager manager, UserManager userManager) {
        this.manager = manager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public CostGroupListDTO loadCostGroups() {
        List<CostGroupDTO> costGroups = manager.loadCostGroups().stream().map(this::convertToDTO).toList();
        return new CostGroupListDTO(costGroups);
    }

    @Override
    @GetMapping(path = "/{costGroupVersion}")
    public CostGroupListDTO loadCostGroups(@PathVariable Long costGroupVersion) {
        List<CostGroupDTO> costGroups = manager.loadCostGroups(costGroupVersion).stream().map(this::convertToDTO)
                .toList();
        return new CostGroupListDTO(costGroups);
    }

    @Override
    @PostMapping
    public CostGroupsDTO saveCostGroup(@RequestBody CostGroupsDTO newCostGroups) {
        userManager.requireAdminUser();

        HashSet<CostGroupDTO> result = new HashSet<>(newCostGroups.costGroups().size());
        for (CostGroupDTO costGroupDTO : newCostGroups.costGroups()) {
            CostGroup costGroupToSave = new CostGroup(costGroupDTO);
            CostGroup costGroup = manager.saveCostGroup(costGroupToSave);
            CostGroupDTO converted = convertToDTO(costGroup);

            result.add(converted);
        }

        return new CostGroupsDTO(result);
    }

    @Override
    @PutMapping
    public CostGroupDTO updateCostGroup(@RequestBody UpdateCostGroupDTO updateCostGroupDTO) {
        userManager.requireAdminUser();

        CostGroup oldCostGroup = new CostGroup(updateCostGroupDTO.oldCostGroup());
        CostGroup newCostGroup = new CostGroup(updateCostGroupDTO.newCostGroup());
        CostGroup costGroup = manager.updateCostGroup(oldCostGroup, newCostGroup);
        return convertToDTO(costGroup);
    }

    @Override
    @DeleteMapping
    public void removeCostGroup(@RequestParam Long version, @RequestParam String costGroupName) {
        userManager.requireAdminUser();

        manager.removeCostGroup(version, costGroupName);
    }

    private CostGroupDTO convertToDTO(CostGroup costGroup) {
        return new CostGroupDTO(costGroup.getCostGroup(), costGroup.getDescription(), costGroup.getParentCostGroup(),
                costGroup.getVersion());
    }
}
