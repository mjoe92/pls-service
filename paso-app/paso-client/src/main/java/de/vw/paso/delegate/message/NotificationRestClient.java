package de.vw.paso.delegate.message;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.message.Notification;
import de.vw.paso.service.message.NotificationRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class NotificationRestClient implements NotificationRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public NotificationRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public Notification pollMessages(String userId) {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getRequest = new HttpGet(pasoServerHost + URL + "/" + userId);

        return httpClient.execute(getRequest, Notification.class);
    }

    @Override
    public void createUserMessage(String userMessage) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(userMessage);

            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost httpPost = new HttpPost(pasoServerHost + URL);
            httpPost.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            httpClient.execute(httpPost);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
