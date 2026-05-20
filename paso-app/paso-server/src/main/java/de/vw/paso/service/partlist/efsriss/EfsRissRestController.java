package de.vw.paso.service.partlist.efsriss;

import de.vw.paso.logic.partlist.EfsRissManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = EfsRissRestService.URL)
public class EfsRissRestController implements EfsRissRestService {

    private final EfsRissManager efsRissManager;

    public EfsRissRestController(EfsRissManager efsRissManager) {
        this.efsRissManager = efsRissManager;
    }

    @Override
    @GetMapping
    @Transactional
    public AlternativePartsForGapListDTO getAlternativePartsForGap(@RequestParam String nodeId,
            @RequestParam long vehicleConfigId) {
        return efsRissManager.getAlternativePartsForGap(nodeId, vehicleConfigId);
    }
}
