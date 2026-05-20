package de.vw.paso.client.util.desktop;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.base.dialog.PasoAlert;
import de.vw.paso.client.util.preference.PreferenceHandler;
import de.vw.paso.client.util.preference.PreferenceKeys;
import de.vw.paso.login.client.PasoClientProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesktopLinkCreator {

    private static final Logger logger = LoggerFactory.getLogger(DesktopLinkCreator.class);

    private static final String paso_icon_path = "/icons/application/Paso_Icon_64x64px.ico";
    private static String webstarterUrl;

    static {
        logger.debug("Reading webstarter url from property 'webstarter.url'");
        webstarterUrl = System.getProperty("webstarter.url");
        logger.debug("Found url: {}", webstarterUrl);
        if (StringUtils.isEmpty(webstarterUrl)) {
            logger.warn("No webstarter url found. Links will not work!");
        } else {
            logger.debug("Replace 'https' with 'vwstarts'");
            webstarterUrl = webstarterUrl.replaceFirst("https", "vwstarts");
            logger.debug("Using webstarter url {}", webstarterUrl);
        }
    }

    public static void createPasoDesktopLink() throws IOException {
        String linkName = "PASO" + PasoClientProperties.get().getStage().name();

        try {
            DesktopLink.createLink(webstarterUrl, paso_icon_path, linkName);
            showLinkCreatedMessage();
        } catch (FileExistsException fee) {
            showLinkExistsMessage(linkName);
        }
    }

    public static void checkFirstTimeStart() throws IOException {
        PreferenceHandler pref = PreferenceHandler.getInstance();
        if (!pref.get(PreferenceKeys.WAS_LINK_CREATED)) {
            String linkName = "PASO" + PasoClientProperties.get().getStage().name();
            logger.debug("First time start on this computer. Ask for desktop link.");
            if (askUserForCreation()) {
                logger.debug("User wants a desktop link");
                try {
                    DesktopLink.createLink(webstarterUrl, paso_icon_path, linkName);
                    showLinkCreatedMessage();
                } catch (FileExistsException fee) {
                    showLinkExistsMessage(linkName);
                }
                pref.set(PreferenceKeys.WAS_LINK_CREATED, true);
            } else {
                pref.set(PreferenceKeys.WAS_LINK_CREATED, true);
                logger.debug("User does not want a link. Exit link creation.");
            }
        }
    }

    private static boolean askUserForCreation() {
        Alert alert = new PasoAlert(AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle(I18N.getString("desktop.link.dialog.title"));
        alert.setHeaderText(null);
        alert.setContentText(I18N.getString("desktop.link.dialog.desc"));
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.NO) == ButtonType.YES;
    }

    private static void showLinkExistsMessage(String linkName) {
        Alert alert = new PasoAlert(AlertType.WARNING);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.CLOSE);
        alert.setTitle(I18N.getString("desktop.link.dialog.error.title"));
        alert.setHeaderText(null);
        String errorMessage = I18N.getString("desktop.link.dialog.error.desc");
        errorMessage = MessageFormat.format(errorMessage, linkName);
        alert.setContentText(errorMessage);
        alert.show();
    }

    private static void showLinkCreatedMessage() {
        Alert alert = new PasoAlert(AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.OK);
        alert.setTitle(I18N.getString("desktop.link.dialog.created.title"));
        alert.setHeaderText(null);
        alert.setContentText(I18N.getString("desktop.link.dialog.created.desc"));
        alert.show();
    }
}
