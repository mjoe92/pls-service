package de.vw.paso.consumer.masterdata.salesregion;

import java.util.List;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.service.masterdata.salesregion.ILoadSalesRegionsConsumer;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadSalesRegionsConsumer extends AbstractTestConsumer<List<SalesRegionDTO>>
        implements ILoadSalesRegionsConsumer {

    @Autowired
    private SalesRegionRestService service;

    @Override
    public void loadSalesRegions() {
        run(() -> (service.loadSalesRegions().salesRegionDTOList()));
    }
}
