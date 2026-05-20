package de.vw.paso.delegate.stueckliste.costgroup;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupListDTO;
import de.vw.paso.service.partlist.costgroup.CostGroupRestService;
import de.vw.paso.service.partlist.costgroup.CostGroupsDTO;
import de.vw.paso.service.partlist.costgroup.UpdateCostGroupDTO;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class CostGroupRestClient implements CostGroupRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public CostGroupRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public CostGroupListDTO loadCostGroups() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getCostGroupRequest = new HttpGet(pasoServerHost + CostGroupRestService.URL);

        return httpClient.execute(getCostGroupRequest, CostGroupListDTO.class);
    }

    @Override
    public CostGroupListDTO loadCostGroups(Long costGroupVersion) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getCostGroupRequest = new HttpGet(pasoServerHost + CostGroupRestService.URL + "/" + costGroupVersion);

        return httpClient.execute(getCostGroupRequest, CostGroupListDTO.class);
    }

    @Override
    public CostGroupsDTO saveCostGroup(CostGroupsDTO newCostGroup) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(newCostGroup);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + CostGroupRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, CostGroupsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CostGroupDTO updateCostGroup(UpdateCostGroupDTO updateCostGroupDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(updateCostGroupDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + CostGroupRestService.URL);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, CostGroupDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeCostGroup(Long version, String costGroupName) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(
                pasoServerHost + CostGroupRestService.URL + "?version=" + version + "&costGroupName=" + costGroupName);

        httpClient.execute(httpDelete);
    }
}
