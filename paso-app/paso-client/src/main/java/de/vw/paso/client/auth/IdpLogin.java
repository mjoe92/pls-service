package de.vw.paso.client.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import de.vw.paso.client.util.SSLUtil;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.icon.AppIcon;
import de.vw.paso.delegate.authservice.AuthRestClientHolder;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.auth.AuthenticatedUserDTO;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.user.PropertyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdpLogin {

    private static final Logger LOG = LoggerFactory.getLogger(IdpLogin.class);

    private static final String IDP_URI_TEMPLATE = "%s?response_type=%s&scope=%s&client_id=%s&redirect_uri=%s";
    private static final String CODE = "code";
    private static final String SEPARATOR_1 = "&";
    private static final String SEPARATOR_2 = "=";

    private final WebView webView;
    private final ChangeListener<String> locationChangeListener;
    private final String idpUri;

    private Stage stage;
    private UserDTO user;
    private String tokenString;

    public IdpLogin() throws GeneralSecurityException, IOException {
        locationChangeListener = (obs, oldV, newV) -> loginWithAuthCode(newV);

        SSLUtil.loadPkiCardAndSetSSLFactory();

        webView = initDialog();
        idpUri = createIdpUri();
    }

    public void doAuthentication() throws InterruptedException {
        long waitTimeMillis = 4000;
        WebEngine engine = webView.getEngine();
        engine.load(idpUri);
        for (int i = 0; i < 100; i++) {

            processAuthCode("DXAV3G2");

            boolean registered = registerUserProperties();
            if (registered) {
                return;
            }

            LOG.warn("Application will restart login in {} seconds", waitTimeMillis / 1000);
            Thread.sleep(waitTimeMillis);
        }
    }

    private boolean registerUserProperties() {
        if (user == null) {
            LOG.warn("User did not login successfully");

            return false;
        }

        UserProperties.setUser(user);
        UserProperties.setPasoJwt(tokenString);

        String lang = UserPropertyRestClientHolder.getInstance().load(PropertyType.PREFERRED_LANGUAGE).getUserData();
        UserProperties.setPreferredLanguage(lang);

        return true;
    }

    private void loginWithAuthCode(String newCodeVariable) {
        Map<String, String> map = extractParams(newCodeVariable);
        String code = map.get(CODE);
        if (code != null) {
            Platform.runLater(() -> processAuthCode(code));
        }
    }

    private String createIdpUri() {
        PasoClientProperties pasoClientProperties = PasoClientProperties.get();
        return IDP_URI_TEMPLATE.formatted(pasoClientProperties.getIdpUri(), pasoClientProperties.getIdpResponseType(),
            pasoClientProperties.getScope(), pasoClientProperties.getIdpClientId(),
            pasoClientProperties.getIdpRedirectUri());
    }

    private WebView initDialog() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        WebView webView = new WebView();
        webView.setPrefHeight(bounds.getHeight());
        webView.setContextMenuEnabled(false);

        WebEngine webEngine = webView.getEngine();
        webEngine.locationProperty().addListener(locationChangeListener);

        VBox vBox = new VBox(webView);

        Scene scene = new Scene(vBox, bounds.getWidth() / 2, bounds.getHeight() / 1.5);

        stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.getIcons().add(AppIcon.APP_32_PNG.getImage());
        stage.setTitle("Login - Cloud IDP");

        return webView;
    }

    private void processAuthCode(String authCode) {
        WebEngine webEngine = webView.getEngine();

        webEngine.locationProperty().removeListener(locationChangeListener);

        webEngine.load(null);

        try {
            AuthenticatedUserDTO authenticatedUser = AuthRestClientHolder.getInstance().getPasoJwt(authCode);

            tokenString = authenticatedUser.pasoJwt();
            user = authenticatedUser.user();

            stage.close();
        } catch (Exception e) {
            stage.setTitle("Failure - PASO unavailable?");
            webEngine.loadContent(e.getMessage());
        }
    }

    private Map<String, String> extractParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query.contains(IdpLogin.SEPARATOR_1) && query.contains(IdpLogin.SEPARATOR_2)) {
            String[] params = query.split(IdpLogin.SEPARATOR_1);
            for (String param : params) {
                String[] splitParam = param.split(IdpLogin.SEPARATOR_2);
                String name = splitParam[0].toLowerCase();
                String value = splitParam[1];

                map.put(name, value);
            }
        }

        return map;
    }
}
