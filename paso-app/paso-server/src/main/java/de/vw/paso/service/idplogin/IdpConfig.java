package de.vw.paso.service.idplogin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdpConfig {

  @Value("${idp.client-id}")
  private String clientId;

  @Value("${idp.client-secret}")
  private String clientSecret;

  @Value("${idp.access-token.uri}")
  private String accessTokenUri;

  @Value("${idp.user-info.uri}")
  private String userInfoUri;

  @Value("${idp.redirect.uri}")
  private String redirectUri;

  @Value("${idp.grant-type}")
  private String grantType;

  String clientId() {
    return clientId;
  }

  String clientSecret() {
    return clientSecret;
  }

  String accessTokenUri() {
    return accessTokenUri;
  }

  String userInfoUri() {
    return userInfoUri;
  }

  String redirectUri() {
    return redirectUri;
  }

  String grantType() {
    return grantType;
  }

}
