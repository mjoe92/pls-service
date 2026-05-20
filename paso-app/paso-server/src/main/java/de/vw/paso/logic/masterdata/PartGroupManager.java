package de.vw.paso.logic.masterdata;

import java.util.ArrayList;
import java.util.List;

import de.vw.paso.exception.CategoryCanNotBeDeletedException;
import de.vw.paso.masterdata.domain.PartGroup;
import de.vw.paso.repository.partgroup.PartGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class PartGroupManager {

    private PartGroupRepository partGroupRepository;

    @Transactional
    public List<PartGroup> findAll() {
        return partGroupRepository.findAll();
    }

    @Transactional
    public void delete(boolean isMgr, int mgr, boolean isUgr, int ugr) throws CategoryCanNotBeDeletedException {
        if (isMgr) {
            partGroupRepository.deleteMgr(mgr);
        } else if (isUgr) {
            partGroupRepository.deleteUgr(mgr, ugr);
        } else {
            throw new CategoryCanNotBeDeletedException("Cannot delete category");
        }
    }

    @Transactional
    public List<PartGroup> update(PartGroup pg) {
        List<PartGroup> result = new ArrayList<>();
        if (!pg.isMgr()) {
            result.add(partGroupRepository.save(pg));
        } else {
            PartGroup oldPartGroup = partGroupRepository.findById(pg.getId()).orElseThrow();
            List<PartGroup> partGroupsToUpdate = partGroupRepository.loadUgrByMgr(oldPartGroup.getMgr());
            for (PartGroup partGroup : partGroupsToUpdate) {
                partGroup.setMgr(pg.getMgr());
            }
            result.add(partGroupRepository.save(pg));
            result.addAll(partGroupRepository.saveAll(partGroupsToUpdate));
        }
        return result;
    }

    @Transactional
    public PartGroup addPartGroup(PartGroup partGroup) {
        if (partGroup.isUgr()) {
            PartGroup parentMgrGroup = partGroupRepository.loadMgrByMgr(partGroup.getCategory(), partGroup.getMgr());
            if (parentMgrGroup == null) {
                parentMgrGroup = new PartGroup();
                parentMgrGroup.setCategory(partGroup.getCategory());
                parentMgrGroup.setMgr(partGroup.getMgr());
                partGroupRepository.save(parentMgrGroup);
            }
        }
        return partGroupRepository.save(partGroup);
    }
}
