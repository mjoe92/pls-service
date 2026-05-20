package de.vw.paso.delegate.stueckliste.inspector;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.partlist.dto.EfsElementAggregateMappingListDTO;
import de.vw.paso.service.partlist.inspector.InspectorIgnoresDTO;
import de.vw.paso.service.partlist.inspector.InspectorRestService;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class InspectorRestClient implements InspectorRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public InspectorRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public InspectorIgnoresDTO loadIgnoreEntries(Long partListId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(pasoServerHost + URL + StringConstant.SLASH + partListId);

        return httpClient.execute(httpGet, InspectorIgnoresDTO.class);
    }

    @Override
    public void deleteIgnores(InspectorIgnoresDTO toDelete) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(toDelete);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpDelete httpDelete = new HttpDelete(pasoServerHost + URL + DELETE_LIST);
            httpDelete.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpDelete);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveIgnoreEntries(InspectorIgnoresDTO toSave) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(toSave);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + URL + SAVE_LIST);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPost);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EfsElementAggregateMappingListDTO loadAggregateMapping(Long vehiclePartListId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGet = new HttpGet(
                pasoServerHost + URL + LOAD_AGGREGATE_MAPPING + StringConstant.SLASH + vehiclePartListId);

        return httpClient.execute(httpGet, EfsElementAggregateMappingListDTO.class);
    }
}
