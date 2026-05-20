package de.vw.paso.delegate.stueckliste.smartfix;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;
import de.vw.paso.service.partlist.smartfix.SmartFixListDTO;
import de.vw.paso.service.partlist.smartfix.SmartFixRestService;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SmartFixRestClient implements SmartFixRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public SmartFixRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public SmartFixListDTO loadAll() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSmartFixesRequest = new HttpGet(pasoServerHost + SmartFixRestService.URL);

        return httpClient.execute(getSmartFixesRequest, SmartFixListDTO.class);
    }

    @Override
    public SmartFixDTO save(SmartFixDTO fix) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(fix);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + SmartFixRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, SmartFixDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + SmartFixRestService.URL + StringConstant.SLASH + id);

        httpClient.execute(httpDelete);
    }

    @Override
    public SmartFixListDTO loadByFields(Collection<String> fields) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getSmartFixesRequest = new HttpGet(
                pasoServerHost + SmartFixRestService.URL + StringConstant.SLASH + String.join(StringConstant.COMMA,
                        fields));

        return httpClient.execute(getSmartFixesRequest, SmartFixListDTO.class);
    }
}
