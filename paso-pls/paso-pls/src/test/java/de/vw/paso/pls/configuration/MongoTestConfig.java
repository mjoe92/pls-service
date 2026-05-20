package de.vw.paso.pls.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@Configuration
public class MongoTestConfig {

  @Value("${spring.data.mongodb.database}")
  private String DATABASE_NAME;

  public MongoTestConfig() {
    // Java ignores http.proxyUser. Here come's the workaround.
    Authenticator.setDefault(new Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        if (getRequestorType() == RequestorType.PROXY) {
          final String prot = getRequestingProtocol().toLowerCase();
          final String host = System.getProperty(prot + ".proxyHost", "");
          final String port = System.getProperty(prot + ".proxyPort", "80");
          final String user = System.getProperty(prot + ".proxyUser", "");
          final String password = System.getProperty(prot + ".proxyPassword", "");

          if (getRequestingHost().equalsIgnoreCase(host)) {
            if (Integer.parseInt(port) == getRequestingPort()) {
              return new PasswordAuthentication(user, password.toCharArray());
            }
          }
        }

        return null;
      }

    });
  }

}
