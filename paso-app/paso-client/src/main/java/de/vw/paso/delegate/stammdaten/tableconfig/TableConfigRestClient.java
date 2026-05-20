package de.vw.paso.delegate.stammdaten.tableconfig;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import de.vw.paso.service.tableconfig.TableConfigListDTO;
import de.vw.paso.service.tableconfig.TableConfigRestService;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class TableConfigRestClient implements TableConfigRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public TableConfigRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public TableConfigListDTO getConfigurationsForUser() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getTableConfigRequest = new HttpGet(pasoServerHost + TableConfigRestService.URL);

        return httpClient.execute(getTableConfigRequest, TableConfigListDTO.class);
    }

    @Override
    public TableConfigDTO saveConfiguration(TableConfigDTO config) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(config);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + TableConfigRestService.URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, TableConfigDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteConfiguration(Long id) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + TableConfigRestService.URL + "/" + id);

        httpClient.execute(httpDelete);

    }
}
