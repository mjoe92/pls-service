package de.vw.paso.delegate.pls;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.pls.EfsAggregateInformationDTO;
import de.vw.paso.pls.ProductDataDTO;
import de.vw.paso.service.pls.CreateSubPartListDTO;
import de.vw.paso.service.pls.ImportedEfsElementsDTO;
import de.vw.paso.service.pls.PlsRequestResultDTO;
import de.vw.paso.service.pls.PlsRestService;
import de.vw.paso.service.pls.SubPartListRequestDTO;
import de.vw.paso.service.pls.TiWhRequestQueueListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.dto.PartListRequestDTO;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class PlsRestClient implements PlsRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public PlsRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public PlsRequestResultDTO requestPartList(PartListRequestDTO partListRequest) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(partListRequest);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPutRequest = new HttpPut(pasoServerHost + URL);
            httpPutRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPutRequest, PlsRequestResultDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VehicleConfigDTO createPartList(Long vehicleConfigId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPost httpPostRequest = new HttpPost(pasoServerHost + URL + "?vehicleConfigId=" + vehicleConfigId);

        return httpClient.execute(httpPostRequest, VehicleConfigDTO.class);
    }

    @Override
    public ProductDataDTO requestSubPartList(SubPartListRequestDTO subPartListRequest) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(subPartListRequest);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPutRequest = new HttpPut(pasoServerHost + URL + SUB_PART_LIST);
            httpPutRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPutRequest, ProductDataDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ImportedEfsElementsDTO createSubPartList(CreateSubPartListDTO createSubPartListDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(createSubPartListDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPostRequest = new HttpPost(pasoServerHost + URL + SUB_PART_LIST);
            httpPostRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPostRequest, ImportedEfsElementsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TiWhRequestQueueListDTO getTiWhRequestQueue() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL + GET_REQUEST_QUEUE);

        return httpClient.execute(httpGetRequest, TiWhRequestQueueListDTO.class);
    }

    @Override
    public EfsAggregateInformationDTO getAggregateInformation(Collection<String> efsElementIds,
            Collection<String> productIds) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(
                pasoServerHost + URL + GET_AGGREGATE_INFORMATION + "?efsElementIds=" + String.join(StringConstant.COMMA,
                        efsElementIds) + "&productIds=" + String.join(StringConstant.COMMA, productIds));

        return httpClient.execute(httpGetRequest, EfsAggregateInformationDTO.class);
    }
}
