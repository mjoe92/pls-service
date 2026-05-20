package de.vw.paso.delegate.stueckliste.pst;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.pst.PstDTO;
import de.vw.paso.service.masterdata.pst.PstListDTO;
import de.vw.paso.service.masterdata.pst.PstRestService;
import lombok.AllArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

@AllArgsConstructor
public class PstRestClient implements PstRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    @Override
    public PstListDTO getPsts() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet httpGetRequest = new HttpGet(pasoServerHost + URL);

        return httpClient.execute(httpGetRequest, PstListDTO.class);
    }

    @Override
    public void deletePst(Long id) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpDelete httpDelete = new HttpDelete(pasoServerHost + URL + DELETE + "/" + id);

        httpClient.execute(httpDelete);
    }

    @Override
    public PstDTO editPst(PstDTO pstDTO) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPatch httpPatch = new HttpPatch(pasoServerHost + URL + EDIT);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(pstDTO);
            httpPatch.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPatch, PstDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PstDTO addPst(PstDTO pstDTO) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpPost httpPost = new HttpPost(pasoServerHost + URL + ADD);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(pstDTO);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, PstDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
