package de.vw.paso.logic.partlist;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.partlist.domain.PartListViewGroup;
import de.vw.paso.partlist.domain.PartListViewMode;
import de.vw.paso.repository.partlist.PartListViewGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PartListViewGroupManager {

    private final PartListViewGroupRepository partListViewGroupRepository;

    public List<PartListViewGroup> loadPartListViewGroups() {
        return partListViewGroupRepository.findAll();
    }

    public List<PartListViewGroup> loadPartListViewGroupsByPartListViewMode(PartListViewMode viewMode) {
        List<PartListViewGroup> partListViewGroups = new ArrayList<>();

        if (viewMode.equals(PartListViewMode.ENGINE_AND_GEARBOX)) {
            partListViewGroups.addAll(partListViewGroupRepository.findAllByPartListViewMode(PartListViewMode.ENGINE));
            partListViewGroups.addAll(partListViewGroupRepository.findAllByPartListViewMode(PartListViewMode.GEARBOX));
        }

        partListViewGroups.addAll(partListViewGroupRepository.findAllByPartListViewMode(viewMode));

        return partListViewGroups;
    }

}
