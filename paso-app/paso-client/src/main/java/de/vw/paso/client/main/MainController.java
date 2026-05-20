package de.vw.paso.client.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.cache.CacheManager;
import de.vw.paso.client.main.notification.NotificationController;
import de.vw.paso.client.main.ribbonmenu.RibbonMenuController;
import de.vw.paso.client.main.statusbar.MainStatusBarController;
import de.vw.paso.client.main.tab.MainTabPaneController;
import de.vw.paso.client.util.UserProperties;

@FXController(name = "main")
public class MainController extends BaseController<Pane> {

  @FXML
  private BorderPane borderPane;

  private MainTabPaneController mainTabPaneController;

  private MainStatusBarController mainStatusBarController;

  private RibbonMenuController ribbonMenuController;
  private NotificationController notificationController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    super.initialize(location, resources);

    ribbonMenuController = new RibbonMenuController();
    notificationController = new NotificationController();

    mainStatusBarController = BaseController.load(MainStatusBarController.class);
    mainTabPaneController = BaseController.load(MainTabPaneController.class);

    borderPane.setTop(ribbonMenuController.getControl());
    borderPane.setBottom(mainStatusBarController.getControl());
    borderPane.setCenter(mainTabPaneController.getControl());

    registerSubController(mainTabPaneController);
    registerSubController(ribbonMenuController);
    registerSubController(notificationController);
  }

  @Override
  public Pane getControl() {
    return borderPane;
  }

  @Override
  public Parent getStyleableParent() {
    return getControl();
  }

  @Override
  public void start() {
    notificationController.start(UserProperties.getUser());
    doAsync(CacheManager::initializeCache);
  }

  public RibbonMenuController getRibbonMenuController() {
    return ribbonMenuController;
  }

  public MainStatusBarController getMainStatusBarController() {
    return mainStatusBarController;
  }
}
