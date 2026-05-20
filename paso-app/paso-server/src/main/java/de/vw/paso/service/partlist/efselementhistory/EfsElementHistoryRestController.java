package de.vw.paso.service.partlist.efselementhistory;

import java.util.Collection;

import de.vw.paso.logic.partlist.EfsElementHistoryManager;
import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.partlist.domain.EfsElement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EfsElementHistoryRestService.URL)
public class EfsElementHistoryRestController implements EfsElementHistoryRestService {

    private final EfsElementHistoryManager efsElementHistoryManager;

    public EfsElementHistoryRestController(EfsElementHistoryManager efsElementHistoryManager) {
        this.efsElementHistoryManager = efsElementHistoryManager;
    }

    @Override
    @GetMapping(path = "/{efsElementId}")
    @Transactional
    public EfsElementAndMaraAndHistoryListDTO loadHistoryList(@PathVariable Long efsElementId) {
        return efsElementHistoryManager.loadHistorie(efsElementId);
    }

    @Override
    @GetMapping(path = LOAD_REVISIONS + "{vehiclePartListId}")
    @Transactional
    public EfsElementAndMaraAndHistoryListDTO loadRevisions(@PathVariable Long vehiclePartListId) {
        return efsElementHistoryManager.loadRevisions(vehiclePartListId);
    }

    @Override
    @PutMapping(path = REVERT_TO_REVISION)
    @Transactional
    public EfsElementCollection revertToRevision(@RequestBody RevertToRevisionDTO revertToRevisionDTO) {
        Collection<EfsElement> efsElements = efsElementHistoryManager.revertToRevision(revertToRevisionDTO.partListId(),
                revertToRevisionDTO.revision());
        return new EfsElementCollection(efsElements.stream().map(EfsElementMapper::toDto).toList());
    }
}
