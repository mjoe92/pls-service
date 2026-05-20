package de.vw.paso.delegate.stueckliste.product;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.delegate.util.PasoRestClient;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.service.masterdata.product.ProductDTOSet;
import de.vw.paso.service.masterdata.product.ProductRestService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class ProductRestClient implements ProductRestService {

    private final ObjectMapper objectMapper;
    private final PasoRestClient httpClient;

    public ProductRestClient(ObjectMapper objectMapper, PasoRestClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public ProductDTOSet getProducts() {
        String pasoServerHost = PasoClientProperties.get().getServerUrl();
        HttpGet getProducts = new HttpGet(pasoServerHost + ProductRestService.URL);

        return httpClient.execute(getProducts, ProductDTOSet.class);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(productDTO);
            String pasoServerHost = PasoClientProperties.get().getServerUrl();
            HttpPost postProduct = new HttpPost(pasoServerHost + ProductRestService.URL);
            postProduct.setEntity(new StringEntity(requestBodyJson, StandardCharsets.UTF_8));

            return httpClient.execute(postProduct, ProductDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
