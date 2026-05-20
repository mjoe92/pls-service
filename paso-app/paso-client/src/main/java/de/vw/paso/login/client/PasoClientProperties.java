package de.vw.paso.login.client;

import java.net.URL;
import java.util.Properties;

import de.vw.paso.stage.Stage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasoClientProperties {

    private static final Logger LOG = LoggerFactory.getLogger(PasoClientProperties.class);

    private static final String APPLICATION_ARGS_KEY_STAGE = "-stage";
    private static final String KEY_PASO_SERVER_URL = "paso.server.url";
    private static final String KEY_TRUST_STORE = "javax.net.ssl.trustStore";
    private static final String KEY_KEY_STORE = "javax.net.ssl.keyStore";
    private static final String PASO_IDP_URI = "paso.idp.uri";
    private static final String PASO_IDP_RESPONSE_TYPE = "paso.idp.response.type";
    private static final String PASO_IDP_SCOPE = "paso.idp.scope";
    private static final String PASO_CLIENT_ID = "paso.idp.client.id";
    private static final String PASO_IDP_REDIRECT_URI = "paso.idp.redirect.uri";

    private static PasoClientProperties instance;

    private String serverUrl;
    private Stage stage;

    @Getter
    private String idpUri;
    @Getter
    private String idpResponseType;
    @Getter
    private String scope;
    @Getter
    private String idpClientId;
    @Getter
    private String idpRedirectUri;

    private PasoClientProperties() {
    }

    public static void load(String[] args) throws PasoLoginPropertyException {
        try {
            instance = new PasoClientProperties();
            String stage = "local";
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith(APPLICATION_ARGS_KEY_STAGE) && i + 1 < args.length) {
                    stage = args[i + 1];
                }
            }

            LOG.info("Set stage: {}", stage);
            if (stage == null) {
                throw new PasoLoginPropertyException();
            }

            instance.stage = Stage.valueOf(stage.toUpperCase());
            Properties properties = new Properties();
            String propertiesFile = "login-" + stage.toLowerCase() + ".properties";

            LOG.info("Load properties: {}", propertiesFile);
            try (var is = ClassLoader.getSystemResourceAsStream(propertiesFile)) {
                properties.load(is);
            }

            for (Object key : properties.keySet()) {
                String keyStr = key.toString();
                String property = properties.getProperty(keyStr);

                LOG.info(keyStr, property);
                instance.setValue(keyStr, property);
            }
        } catch (Exception e) {
            throw new PasoLoginPropertyException(e);
        }
    }

    public static PasoClientProperties get() {
        return instance;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public Stage getStage() {
        return stage;
    }

    private void setValue(String key, String value) {
        LOG.trace("{}={}", key, value);

        if (KEY_PASO_SERVER_URL.equalsIgnoreCase(key)) {
            serverUrl = value;
        } else if (KEY_TRUST_STORE.equalsIgnoreCase(key)) {
            URL url = ClassLoader.getSystemResource(value);
            System.setProperty(key, url.getPath());
        } else if (KEY_KEY_STORE.equalsIgnoreCase(key)) {
            URL url = ClassLoader.getSystemResource(value);
            System.setProperty(key, url.getPath());
        } else if (PASO_IDP_URI.equalsIgnoreCase(key)) {
            idpUri = value;
        } else if (PASO_IDP_RESPONSE_TYPE.equalsIgnoreCase(key)) {
            idpResponseType = value;
        } else if (PASO_IDP_SCOPE.equalsIgnoreCase(key)) {
            scope = value;
        } else if (PASO_CLIENT_ID.equalsIgnoreCase(key)) {
            idpClientId = value;
        } else if (PASO_IDP_REDIRECT_URI.equalsIgnoreCase(key)) {
            idpRedirectUri = value;
        } else {
            System.setProperty(key, value);
        }

        Properties properties = System.getProperties();
        properties.forEach((propertyKey, propertyValue) -> LOG.debug(propertyKey + ": " + propertyValue));
    }
}