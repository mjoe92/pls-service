package de.vw.paso.delegate.util;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.utility.StringConstant;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasoRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(PasoRestClient.class);

    private static PasoRestClient instance;

    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    private PasoRestClient(ObjectMapper objectMapper, CloseableHttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    public static PasoRestClient getInstance() {
        if (instance == null) {
            CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build();
            instance = new PasoRestClient(ObjectMapperHolder.getInstance(), client);
        }

        return instance;
    }

    public <T> T execute(ClassicHttpRequest request, Class<T> responseDataType) {
        setHeaders(request);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Cookies: {}", ((CookieManager) CookieHandler.getDefault()).getCookieStore().getCookies());
        }
        try {
            return httpClient.execute(request, response -> handleResponse(response, responseDataType));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T handleResponse(ClassicHttpResponse response, Class<T> responseDataType) {
        HttpEntity entity = response.getEntity();
        Header[] headers = response.getHeaders();

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response headers: {}", Arrays.toString(headers));
            }
            if (HttpStatus.SC_OK <= response.getCode() && response.getCode() < HttpStatus.SC_MULTIPLE_CHOICES) {
                return objectMapper.readValue(entity.getContent(), responseDataType);
            } else {
                handleExceptionForErrorCodes(response);
                return null;
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void execute(ClassicHttpRequest request) {
        setHeaders(request);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Execute: {}", request.getRequestUri());
        }

        try {
            httpClient.execute(request, this::handleResponse);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Void handleResponse(ClassicHttpResponse response) {
        try {
            int code = response.getCode();
            if (code < HttpStatus.SC_OK || code >= HttpStatus.SC_MULTIPLE_CHOICES) {
                handleExceptionForErrorCodes(response);
            }

            return null;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setHeaders(ClassicHttpRequest request) {
        if (UserProperties.getPasoJwt() != null) {
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + UserProperties.getPasoJwt());
        }

        request.setHeader(HttpHeaders.CONTENT_TYPE,
                ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8).toString());
    }

    private void handleExceptionForErrorCodes(ClassicHttpResponse response) throws IOException, ParseException {
        HttpEntity entity = response.getEntity();
        if (entity == null || entity.getContentType() == null || !entity.getContentType().equals("application/json")) {
            throw new RuntimeException("Error code: " + response.getCode());
        }

        JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(entity));
        String message = jsonNode.has("message") ? jsonNode.get("message").asText() : StringConstant.DASH;
        throw new RuntimeException("Error code: " + response.getCode() + ", message: " + message);
    }
}