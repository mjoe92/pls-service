package de.vw.paso.service.partlist.partlistviewgroup;

import java.util.List;

import de.vw.paso.logic.partlist.PartListViewGroupManager;
import de.vw.paso.partlist.domain.PartListViewGroup;
import de.vw.paso.partlist.domain.PartListViewMode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PartListViewGroupRestService.URL)
public class PartListViewGroupRestController implements PartListViewGroupRestService {

    private final PartListViewGroupManager partListViewGroupManager;

    public PartListViewGroupRestController(PartListViewGroupManager partListViewGroupManager) {
        this.partListViewGroupManager = partListViewGroupManager;
    }

    @Override
    @GetMapping(path = "/{viewMode}")
    public PartListViewGroupListDTO loadPartListViewGroupsByPartListViewMode(@PathVariable PartListViewMode viewMode) {
        List<PartListViewGroupDTO> partListViewGroupDTOS = partListViewGroupManager.loadPartListViewGroupsByPartListViewMode(
                viewMode).stream().map(this::convertToDTO).toList();
        return new PartListViewGroupListDTO(partListViewGroupDTOS);
    }

    private PartListViewGroupDTO convertToDTO(PartListViewGroup partListViewGroup) {
        return new PartListViewGroupDTO(partListViewGroup.getId(), partListViewGroup.getName(),
                partListViewGroup.getRuleDescription(), partListViewGroup.getCostGroup(),
                partListViewGroup.getPartGroups(), partListViewGroup.getPartListViewMode());
    }
}
