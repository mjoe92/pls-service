package de.vw.paso.login.client;

import javafx.application.Application;
import javafx.stage.Stage;

import de.vw.paso.client.PasoApplication;
import de.vw.paso.client.main.MainController;
import de.vw.paso.client.main.ribbonmenu.efs.RibbonMenuEfs;
import de.vw.paso.client.stueckliste.efs.EfsTabController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasoJavaFXApplication extends Application {

  private static final Logger LOG = LoggerFactory.getLogger(PasoJavaFXApplication.class);

  @Override
  public void start(Stage primaryStage) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Using Java version: {}", System.getProperty("java.runtime.version"));
      LOG.info("Using JavaFX version: {}", System.getProperty("javafx.runtime.version"));
      LOG.info("Start PASO Window");
    }

    try {
      PasoApplication.getInstance().start(primaryStage);
    } catch (Exception e) {
      LOG.error("Could not start application", e);
    }
  }

  @Override
  public void stop() throws Exception {
    MainController mainController = PasoApplication.getMainController();
    if (mainController != null) {
      // Here we can make sure that any unsaved data can be sent to the backend before we close the application
      mainController.getRibbonMenuController().getRibbonMenuBar().getTabs().stream()
        .filter(tab -> tab.getClass().equals(RibbonMenuEfs.class))
        .map(tab -> ((RibbonMenuEfs) tab).getEfsTabController())
        .forEach(EfsTabController::saveSingleVehiclePartListControllerCurrentState);
    }
    super.stop();
  }
}
