package de.vw.paso.service.pls;

import java.util.Collection;

import de.vw.paso.exception.PlsServiceException;
import de.vw.paso.logic.pls.PlsServiceManager;
import de.vw.paso.pls.EfsAggregateInformationDTO;
import de.vw.paso.pls.ProductDataDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.dto.PartListRequestDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PlsRestService.URL)
public class PlsRestController implements PlsRestService {

    private final PlsServiceManager plsServiceManager;

    public PlsRestController(PlsServiceManager plsServiceManager) {
        this.plsServiceManager = plsServiceManager;
    }

    @Override
    @PostMapping
    public VehicleConfigDTO createPartList(@RequestParam Long vehicleConfigId) throws PlsServiceException {
        return plsServiceManager.createPartList(vehicleConfigId);
    }

    @Override
    @PostMapping(path = SUB_PART_LIST)
    public ImportedEfsElementsDTO createSubPartList(@RequestBody CreateSubPartListDTO createSubPartListDTO) {
        return plsServiceManager.createSubPartList(createSubPartListDTO);
    }

    @Override
    @GetMapping(path = GET_AGGREGATE_INFORMATION)
    public EfsAggregateInformationDTO getAggregateInformation(@RequestParam Collection<String> efsElementIds,
            @RequestParam Collection<String> productIds) {
        return plsServiceManager.getAggregateInformation(efsElementIds, productIds);
    }

    @Override
    @GetMapping(path = GET_REQUEST_QUEUE)
    public TiWhRequestQueueListDTO getTiWhRequestQueue() {
        return plsServiceManager.getTiWhRequestQueue();
    }

    @Override
    @PutMapping
    public PlsRequestResultDTO requestPartList(@RequestBody PartListRequestDTO partListRequest) {
        return plsServiceManager.requestPartList(partListRequest);
    }

    @Override
    @PutMapping(path = SUB_PART_LIST)
    public ProductDataDTO requestSubPartList(@RequestBody SubPartListRequestDTO subPartListRequest) {
        return plsServiceManager.requestPartList(subPartListRequest);
    }
}
