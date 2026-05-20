package de.vw.paso.service.pls;

import java.util.Collection;

import de.vw.paso.exception.PlsServiceException;
import de.vw.paso.pls.EfsAggregateInformationDTO;
import de.vw.paso.pls.ProductDataDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.dto.PartListRequestDTO;

public interface PlsRestService {

    String URL = "/api/pls";
    String SUB_PART_LIST = "/sub-part-list";
    String GET_REQUEST_QUEUE = "/request-queue";
    String GET_AGGREGATE_INFORMATION = "/aggregate-information";

    /**
     * Requests a part list.
     * <br>
     * Due to the nature of that process, we only get back a status and not a part list,
     * since it needs to be requested and created/processed in the meantime.
     *
     * @param partListRequest
     *         the request
     * @return the result
     */
    PlsRequestResultDTO requestPartList(PartListRequestDTO partListRequest);

    /**
     * Creates a part list. This is only working when a part list was requested before with
     * {@link #requestPartList(PartListRequestDTO)} and the status is ready.
     *
     * @param vehicleConfigId
     *         the id of the vehicle configuration
     * @return the vehicle with the part list
     * @throws PlsServiceException
     *         if there is something wrong
     */
    VehicleConfigDTO createPartList(Long vehicleConfigId) throws PlsServiceException;

    /**
     * Requests a subpart list.
     * This is used to request a part of the list of a particular part
     * from a full part list (with {@link #createPartList}).
     * This is used for parts like Motor or Getriebe.
     * <br>
     * Due to the nature of that process, we only get back a status and not a part list,
     * since it needs to be requested and created/processed in the meantime.
     *
     * @param subPartListRequest
     *         the request
     * @return the result
     */
    ProductDataDTO requestSubPartList(SubPartListRequestDTO subPartListRequest);

    /**
     * Creates a subpart list. This is only working when a subpart list was requested before with
     * {@link #requestSubPartList(SubPartListRequestDTO)} and the status is ready.
     *
     * @param createSubPartListDTO
     *         the request
     * @return the vehicle with the part list
     */
    ImportedEfsElementsDTO createSubPartList(CreateSubPartListDTO createSubPartListDTO);

    TiWhRequestQueueListDTO getTiWhRequestQueue();

    EfsAggregateInformationDTO getAggregateInformation(Collection<String> efsElementIds, Collection<String> productIds);
}
