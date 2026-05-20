package de.vw.paso.pls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public abstract class AbstractRestControllerTest extends PlsApplicationTests {

  private static final String BASE_URL = "http://localhost";

  @Value("${server.servlet.context-path}")
  private String basePath;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  protected abstract String getControllerMapping();

  public <RESPONSE_TYPE> ResponseEntity<RESPONSE_TYPE> getRequest(final String methodMapping,
    final Class<RESPONSE_TYPE> responseType, final String... getParameters) {
    return getRequest(methodMapping, responseType, null, getParameters);
  }

  public <RESPONSE_TYPE> ResponseEntity<RESPONSE_TYPE> getRequest(final String methodMapping,
    final Class<RESPONSE_TYPE> responseType, final Map<String, Object> pathVariables, final String... getParameters) {
    return restTemplate.getForEntity(getUriComponents(methodMapping, pathVariables, getParameters).toUriString(),
      responseType);
  }

  public <RESPONSE_TYPE> ResponseEntity<RESPONSE_TYPE> postRequest(final Object body, final String methodMapping,
    final Class<RESPONSE_TYPE> responseType, final String... getParameters) {
    return postRequest(body, methodMapping, responseType, null, getParameters);
  }

  public <RESPONSE_TYPE> ResponseEntity<RESPONSE_TYPE> postRequest(final Object body, final String methodMapping,
    final Class<RESPONSE_TYPE> responseType, final Map<String, Object> pathVariables, final String... getParameters) {
    HttpEntity<Object> request = new HttpEntity<>(body);
    return restTemplate.postForEntity(getUriComponents(methodMapping, pathVariables, getParameters).toUriString(),
      request, responseType);
  }

  public <RESPONSE_TYPE> ResponseEntity<RESPONSE_TYPE> deleteRequest(final String methodMapping,
    final Class<RESPONSE_TYPE> responseType, final String... getParameters) {
    return deleteRequest(methodMapping, responseType, null, getParameters);
  }

  public <RESPONSE_TYPE> ResponseEntity<RESPONSE_TYPE> deleteRequest(final String methodMapping,
    final Class<RESPONSE_TYPE> responseType, final Map<String, Object> pathVariables, final String... getParameters) {
    return restTemplate.exchange(getUriComponents(methodMapping, pathVariables, getParameters).toUriString(),
      HttpMethod.DELETE, null, responseType);
  }

  public <RESPONSE_TYPE> ResponseEntity<List<RESPONSE_TYPE>> requestList(final String methodMapping,
    final ParameterizedTypeReference<List<RESPONSE_TYPE>> responseType, final String... getParameters) {
    return requestList(methodMapping, responseType, null, getParameters);
  }

  public <RESPONSE_TYPE> ResponseEntity<List<RESPONSE_TYPE>> requestList(final String methodMapping,
    final ParameterizedTypeReference<List<RESPONSE_TYPE>> responseType, final Map<String, Object> pathVariables,
    final String... getParameters) {
    return restTemplate.exchange(getUriComponents(methodMapping, pathVariables, getParameters).toUriString(),
      HttpMethod.GET, null, responseType);
  }

  private UriComponents getUriComponents(final String methodMapping, final Map<String, Object> pathVariables,
    final String... getParameters) {
    final String mm = methodMapping.startsWith("/") ? methodMapping : "/" + methodMapping;
    final String controllerMapping =
      getControllerMapping().startsWith("/") ? getControllerMapping() : "/" + getControllerMapping();
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
      BASE_URL + ":" + port + basePath + controllerMapping + mm);

    for (int i = 0; i < getParameters.length; i += 2) {
      builder.queryParam(getParameters[i], getParameters[i + 1]);
    }

    return ((pathVariables != null) && (!pathVariables.isEmpty())) ? builder.buildAndExpand(pathVariables)
      : builder.build();
  }

}
