package de.vw.paso.service.idplogin;

import de.vw.paso.client.i18n.AvailableLanguages;
import de.vw.paso.user.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class IdpService {

    private final IdpConfig config;
    private final RestTemplate restTemplate;

    public IdpService(IdpConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    public String getIdpAccessToken(String code) throws JSONException {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2ParameterNames.CODE, code);
        body.add(OAuth2ParameterNames.GRANT_TYPE, config.grantType());
        body.add(OAuth2ParameterNames.CLIENT_ID, config.clientId());
        body.add(OAuth2ParameterNames.CLIENT_SECRET, config.clientSecret());
        body.add(OAuth2ParameterNames.REDIRECT_URI, config.redirectUri());

        JSONObject jsonObject = postForEntity(body, config.accessTokenUri());
        return jsonObject.getString(OAuth2ParameterNames.ACCESS_TOKEN);
    }

    public User getUserInfo(String idpAccessToken) throws JSONException {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2ParameterNames.ACCESS_TOKEN, idpAccessToken);

        JSONObject jsonObject = postForEntity(body, config.userInfoUri());

        User user = new User();
        user.setId(jsonObject.getString("preferred_username").toUpperCase());
        user.setFirstName(extractWithDefault(jsonObject, "given_name", StringUtils.EMPTY));
        user.setLastName(extractWithDefault(jsonObject, "family_name", StringUtils.EMPTY));
        user.setEmail(extractWithDefault(jsonObject, "email", StringUtils.EMPTY));
        user.setCostCenter(extractWithDefault(jsonObject, "cost_center", StringUtils.EMPTY));

        String preferredLanguage = extractWithDefault(jsonObject, "preferred_language", "EN");
        if (!AvailableLanguages.isAvailable(preferredLanguage)) {
            preferredLanguage = "EN";
        }

        user.setPreferredLanguage(preferredLanguage);

        return user;
    }

    private String extractWithDefault(JSONObject jsonObject, String key, String defaultValue) {
        return jsonObject.has(key) ? jsonObject.getString(key) : defaultValue;
    }

    private JSONObject postForEntity(MultiValueMap<String, String> body, String endpoint) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);

        return new JSONObject(response.getBody());
    }
}