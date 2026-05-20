package de.vw.paso.delegate.stammdaten.salesregion;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionIssuesDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionListDTO;
import de.vw.paso.service.masterdata.salesregion.SalesRegionRestService;
import de.vw.paso.service.masterdata.salesregion.SalesRegionUpdateDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SalesRegionRestClient implements SalesRegionRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public SalesRegionRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public SalesRegionListDTO loadSalesRegions() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSalesRegionsRequest = new HttpGet(pasoServerHost + SalesRegionRestService.URL);

        return httpClient.execute(getSalesRegionsRequest, SalesRegionListDTO.class);
    }

    @Override
    public void updateRelevance(Collection<String> salesRegionIds, Integer relevant) {

        try {
            String requestBodyJson = objectMapper.writeValueAsString(salesRegionIds);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPutRequest = new HttpPut(
                    pasoServerHost + SalesRegionRestService.URL + SalesRegionRestService.UPDATE_RELEVANCE + relevant);
            httpPutRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPutRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalesRegionDTO addSalesRegion(SalesRegionDTO salesRegion) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(salesRegion);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPostRequest = new HttpPost(pasoServerHost + SalesRegionRestService.URL);
            httpPostRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPostRequest, SalesRegionDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalesRegionDTO updateSalesRegion(SalesRegionUpdateDTO salesRegionUpdateDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(salesRegionUpdateDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPutRequest = new HttpPut(
                    pasoServerHost + SalesRegionRestService.URL + SalesRegionRestService.UPDATE_SALES_REGION);
            httpPutRequest.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPutRequest, SalesRegionDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSalesRegions(Collection<String> regions) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDeleteRequest = new HttpDelete(
                pasoServerHost + SalesRegionRestService.URL + "?regions=" + String.join(StringConstant.COMMA, regions));

        httpClient.execute(httpDeleteRequest);
    }

    @Override
    public SalesRegionIssuesDTO countConstrainIssues(Collection<String> ids) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(
                pasoServerHost + SalesRegionRestService.URL + SalesRegionRestService.COUNT_ISSUES + "?ids="
                        + String.join(StringConstant.COMMA, ids));

        return httpClient.execute(httpGetRequest, SalesRegionIssuesDTO.class);
    }
}
