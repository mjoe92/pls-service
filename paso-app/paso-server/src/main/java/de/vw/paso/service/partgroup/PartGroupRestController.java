package de.vw.paso.service.partgroup;

import java.util.List;

import de.vw.paso.exception.CategoryCanNotBeDeletedException;
import de.vw.paso.logic.masterdata.PartGroupManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.masterdata.domain.PartGroup;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.masterdata.partgroup.PartGroupListDTO;
import de.vw.paso.service.masterdata.partgroup.PartGroupRestService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = PartGroupRestService.URL)
public class PartGroupRestController implements PartGroupRestService {

    private final PartGroupManager partGroupManager;
    private final UserManager userManager;

    public PartGroupRestController(PartGroupManager partGroupManager, UserManager userManager) {
        this.partGroupManager = partGroupManager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    public PartGroupListDTO loadPartGroups() {
        return new PartGroupListDTO(partGroupManager.findAll().stream().map(this::convertToDTO).toList());
    }

    @Override
    @DeleteMapping
    public void delete(@RequestParam boolean isMgr, @RequestParam int mgr, @RequestParam boolean isUgr,
            @RequestParam int ugr) throws CategoryCanNotBeDeletedException {
        userManager.requireAdminUser();

        partGroupManager.delete(isMgr, mgr, isUgr, ugr);
    }

    @Override
    @PostMapping
    public PartGroupDTO addPartGroup(@RequestBody PartGroupDTO partGroupDTO) {
        userManager.requireAdminUser();

        return convertToDTO(partGroupManager.addPartGroup(new PartGroup(partGroupDTO)));
    }

    @Override
    @PutMapping
    public PartGroupListDTO update(@RequestBody PartGroupDTO pg) {
        userManager.requireAdminUser();

        List<PartGroupDTO> partGroupDTOs = partGroupManager.update(new PartGroup(pg)).stream().map(this::convertToDTO)
                .toList();
        return new PartGroupListDTO(partGroupDTOs);
    }

    private PartGroupDTO convertToDTO(PartGroup partGroup) {
        return new PartGroupDTO(partGroup.getId(), partGroup.getCategory(), partGroup.getMgr(), partGroup.getMgrEnd(),
                partGroup.getUgr(), partGroup.getDescription());
    }
}