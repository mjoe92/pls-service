package de.vw.paso.client;

import java.net.CookieHandler;
import java.net.CookieManager;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import de.vw.paso.client.auth.IdpLogin;
import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.dialog.DialogUtil;
import de.vw.paso.client.main.MainController;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.desktop.DesktopLinkCreator;
import de.vw.paso.client.util.icon.IconUtil;
import de.vw.paso.delegate.buildinfo.BuildInfoRestClientHolder;
import de.vw.paso.login.client.PasoClientProperties;
import de.vw.paso.service.buildinfo.ServerBuildInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasoApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PasoApplication.class);
    private static PasoApplication instance;

    private static MainController mainController;
    private Stage stage;

    private PasoApplication() {
        // Singleton
    }

    public Stage getStage() {
        return stage;
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static PasoApplication getInstance() {
        if (instance == null) {
            instance = new PasoApplication();
        }

        return instance;
    }

    public void start(Stage primaryStage) throws PasoApplicationException {
        this.stage = primaryStage;

        CookieHandler.setDefault(new CookieManager());
        IconUtil.setIcon(primaryStage);
        primaryStage.setTitle("PASO " + PasoClientProperties.get().getStage());

        try {
            IdpLogin idpLogin = new IdpLogin();
            idpLogin.doAuthentication();

            // Login aborted or failed, we just return here without ever opening a JavaFX window.
            // The client will therefore terminate.
            if (UserProperties.getUser() == null) {
                LOG.warn("Could not get user from login");
                return;
            }

            checkUserActiveStatus();

            mainController = BaseController.load(MainController.class);
            mainController.start();
            setBuildInfo(mainController);

            Pane root = mainController.getControl();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

            DesktopLinkCreator.checkFirstTimeStart();
        } catch (Exception exception) {
            throw new PasoApplicationException(exception, PasoApplicationException.START_PASO);
        }
    }

    private void checkUserActiveStatus() {
        LOG.info("Checking if the user is Active");
        if (UserProperties.getUser().getActive()) {
            return;
        }

        LOG.info("User: {} is disabled.", UserProperties.getUserId());

        DialogUtil.showWarnDialog(I18N.getString("warning"), I18N.getString("user.disabled"),
                I18N.getString("warning.contact.joost"));
        System.exit(2);
    }

    private void setBuildInfo(MainController mainController) {
        ServerBuildInfoDTO buildInfo = BuildInfoRestClientHolder.getInstance().getBuildInfo();
        mainController.getMainStatusBarController().setBuildInfo(buildInfo);
    }
}
