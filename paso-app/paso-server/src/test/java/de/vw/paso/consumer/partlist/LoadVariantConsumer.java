package de.vw.paso.consumer.partlist;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.service.partlist.ILoadVariantConsumer;
import de.vw.paso.service.partlist.efsriss.AlternativePartsForGapListDTO;
import de.vw.paso.service.partlist.efsriss.EfsRissRestService;
import org.springframework.stereotype.Component;

@Component
public class LoadVariantConsumer extends AbstractTestConsumer<AlternativePartsForGapListDTO>
        implements ILoadVariantConsumer {

    private final EfsRissRestService service;

    public LoadVariantConsumer(EfsRissRestService service) {
        this.service = service;
    }

    @Override
    public void loadVariants(String nodeId, Long vehicleConfigId) {
        run(() -> service.getAlternativePartsForGap(nodeId, vehicleConfigId));
    }
}
