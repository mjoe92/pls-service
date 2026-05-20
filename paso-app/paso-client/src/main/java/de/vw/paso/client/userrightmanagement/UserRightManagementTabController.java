package de.vw.paso.client.userrightmanagement;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import de.vw.paso.client.base.FXController;
import de.vw.paso.client.control.textfield.PasoCustomTextFieldClearable;
import de.vw.paso.client.main.ribbonmenu.usermanagement.RibbonMenuUserManagementEvent;
import de.vw.paso.client.main.ribbonmenu.usermanagement.RibbonMenuUserManagementListener;
import de.vw.paso.client.main.tab.AbstractMainTabController;
import de.vw.paso.client.userrightmanagement.rightmanagement.RightManagementTabController;
import de.vw.paso.client.userrightmanagement.usergroupmanagement.UserGroupManagementController;
import de.vw.paso.client.userrightmanagement.usermanagement.UserManagementTabController;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.icon.UserManagementIcon;

@FXController(name = "user-right-management-tab")
public class UserRightManagementTabController extends AbstractMainTabController
        implements Initializable, RibbonMenuUserManagementListener {

    @FXML
    private Tab userRightManagementTab;
    @FXML
    private PasoCustomTextFieldClearable searchTxt;
    @FXML
    private ListView<Categories> treeViewUserRight;
    @FXML
    private BorderPane userRightBorderPane;

    private BooleanProperty disablePropertyRefresh;
    private BooleanProperty disablePropertyResetFilters;
    private PasoWildCardPattern patternSearchTerm;
    private AbstractUserRightManagementController<?> currentUserRightController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        userRightManagementTab.setGraphic(new ImageView(UserManagementIcon.USER_MANAGEMENT_16x16.getImage()));
        treeViewUserRight.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> handleCategorySelected(newValue));
        searchTxt.textProperty().addListener((observable, oldValue, newValue) -> setPatternSearchTerm());
    }

    @Override
    public RibbonMenuUserManagementEvent getRibbonMenuEvent() {
        return new RibbonMenuUserManagementEvent(this);
    }

    @Override
    public void start() {
        ObservableList<Categories> items = FXCollections.observableArrayList();
        for (Categories category : Categories.values()) {
            if (patternSearchTerm == null || patternSearchTerm.matches(category.toString()) != null) {
                items.add(category);
            }
        }

        treeViewUserRight.setItems(items);
        treeViewUserRight.getSelectionModel().select(0);
    }

    @Override
    public Tab getControl() {
        return userRightManagementTab;
    }

    @Override
    public Parent getStyleableParent() {
        return treeViewUserRight;
    }

    @Override
    public void handleActionRefresh() {
        currentUserRightController.handleActionRefresh();
    }

    @Override
    public void handleActionClearFilters() {
        currentUserRightController.handleActionClearFilters();
    }

    @Override
    public BooleanProperty disablePropertyRefresh() {
        if (disablePropertyRefresh == null) {
            disablePropertyRefresh = new SimpleBooleanProperty(true);
        }

        return disablePropertyRefresh;
    }

    @Override
    public BooleanProperty disablePropertyClearFilters() {
        if (disablePropertyResetFilters == null) {
            disablePropertyResetFilters = new SimpleBooleanProperty(true);
        }

        return disablePropertyResetFilters;
    }

    private void setPatternSearchTerm() {
        try {
            patternSearchTerm = new PasoWildCardPattern(searchTxt.getText());
        } catch (Exception exception) {
            handleException(exception);
        }

        start();
    }

    private void handleCategorySelected(Categories category) {
        if (category == null) {
            clearWorkArea();
            return;
        }

        switch (category) {
            case USER -> loadSelectedController(UserManagementTabController.class);
            case RIGHT -> loadSelectedController(RightManagementTabController.class);
            case GROUP -> loadSelectedController(UserGroupManagementController.class);
        }
    }

    private void clearWorkArea() {
        userRightBorderPane.setCenter(null);
        bindDisablePropertyRibbonMenu(null);
        currentUserRightController = null;
    }

    private void loadSelectedController(Class<? extends AbstractUserRightManagementController<?>> controller) {
        AbstractUserRightManagementController<?> userRightController = load(controller);
        userRightController.start();
        bindDisablePropertyRibbonMenu(userRightController);
        userRightBorderPane.setCenter(userRightController.getControl());
        currentUserRightController = userRightController;
    }

    private void bindDisablePropertyRibbonMenu(AbstractUserRightManagementController<?> currentController) {
        disablePropertyRefresh().unbind();
        disablePropertyClearFilters().unbind();

        if (currentController == null) {
            disablePropertyRefresh().set(true);
            disablePropertyClearFilters().set(true);
        } else {
            disablePropertyRefresh().bind(currentController.disablePropertyRefresh());
            disablePropertyClearFilters().bind(currentController.disablePropertyClearFilters());
        }
    }
}