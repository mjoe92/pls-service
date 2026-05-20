package de.vw.paso.configuration;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vw.paso.exception.PlsError;
import de.vw.paso.exception.PlsRestException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * The {@link RestTemplate} is used to connect to the PASO PLS Service.
 * Any error from there should be a {@link PlsError}.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.errorHandler(customErrorHandler()).build();
    }

    private ResponseErrorHandler customErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus httpStatus = HttpStatus.resolve(response.getStatusCode().value());
                return (httpStatus != null && isError(httpStatus));
            }

            private boolean isError(HttpStatus httpStatus) {
                return Series.CLIENT_ERROR.equals(httpStatus.series()) || Series.SERVER_ERROR.equals(
                        httpStatus.series());
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                ObjectMapper mapper = new ObjectMapper();
                PlsError plsError = mapper.readValue(response.getBody(), PlsError.class);
                throw new PlsRestException(plsError);
            }
        };
    }
}
