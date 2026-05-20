package de.vw.paso.delegate.partgroup;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.exception.CategoryCanNotBeDeletedException;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;
import de.vw.paso.service.masterdata.partgroup.PartGroupListDTO;
import de.vw.paso.service.masterdata.partgroup.PartGroupRestService;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class PartGroupRestClient implements PartGroupRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public PartGroupRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public PartGroupListDTO loadPartGroups() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getPartGroupRequest = new HttpGet(pasoServerHost + PartGroupRestService.URL);

        return httpClient.execute(getPartGroupRequest, PartGroupListDTO.class);
    }

    @Override
    public void delete(boolean isMgr, int mgr, boolean isUgr, int ugr) throws CategoryCanNotBeDeletedException {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(
                pasoServerHost + PartGroupRestService.URL + "?isMgr=" + isMgr + "&mgr=" + mgr + "&isUgr=" + isUgr
                        + "&ugr=" + ugr);

        httpClient.execute(httpDelete);
    }

    @Override
    public PartGroupDTO addPartGroup(PartGroupDTO partGroupDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(partGroupDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + PartGroupRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, PartGroupDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartGroupListDTO update(PartGroupDTO partGroupDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(partGroupDTO);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPut httpPut = new HttpPut(pasoServerHost + PartGroupRestService.URL);
            httpPut.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPut, PartGroupListDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
