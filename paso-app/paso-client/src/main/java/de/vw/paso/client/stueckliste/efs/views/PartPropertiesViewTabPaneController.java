package de.vw.paso.client.stueckliste.efs.views;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.base.FXController;
import de.vw.paso.client.stueckliste.efs.views.properties.EfsPropertiesTabEvent;
import de.vw.paso.client.stueckliste.efs.views.properties.PartPropertiesTabController;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

@FXController(name = "part-properties-view-tab-pane")
public class PartPropertiesViewTabPaneController extends BaseController<TabPane> {

    private final ObjectProperty<EventHandler<EfsPropertiesTabEvent>> propertyEfsPropertiesTabAction = new SimpleObjectProperty<>(
        this, "EfsPropertiesTabAction");

    @FXML
    private TabPane partPropertiesTabPane;

    private Map<String, BaseController<Tab>> mapTabController = new HashMap<>();

    @Override
    public TabPane getControl() {
        return partPropertiesTabPane;
    }

    @Override
    public Parent getStyleableParent() {
        return getControl();
    }

    public void handleActionShowPartPropertiesView(EfsElementDTO efsElement) {
        PartPropertiesTabController propertiesTabController = (PartPropertiesTabController) getTab(
            PartPropertiesTabController.class);

        if (propertiesTabController == null) {
            propertiesTabController = load(PartPropertiesTabController.class);
            registerSubController(propertiesTabController);
            if (efsElement != null) {
                propertiesTabController.setEfsElementProperties(efsElement);
            }
            addHorizontalTab(propertiesTabController);
        } else {
            propertiesTabController.setEfsElementProperties(efsElement);

            partPropertiesTabPane.getSelectionModel().select(propertiesTabController.getControl());
        }

    }

    public void handleActionReloadPartPropertiesView(EfsElementDTO efsElement) {
        PartPropertiesTabController propertiesTabController = (PartPropertiesTabController) getTab(
            PartPropertiesTabController.class);

        if (propertiesTabController != null) {
            propertiesTabController.setEfsElementProperties(efsElement);
        }
    }

    private void addHorizontalTab(final BaseController<Tab> controller) {
        EfsPropertiesTabEvent propertiesViewTabEvent = new EfsPropertiesTabEvent(this,
            EfsPropertiesTabEvent.ADD_PROPERTIES_TAB, getTabCount() + 1);
        efsPropertiesTabActionProperty().get().handle(propertiesViewTabEvent);
        partPropertiesTabPane.getTabs().add(controller.getControl());
        controller.getControl().setOnClosed(event -> onHorizontalTabClosed(controller));

        cacheTabController(controller);

        partPropertiesTabPane.getSelectionModel().select(controller.getControl());
        partPropertiesTabPane.layout();

        EfsPropertiesTabEvent resizePropertiesViewTabEvent = new EfsPropertiesTabEvent(this,
            EfsPropertiesTabEvent.RESIZE_PROPERTIES_TAB, getTabCount());
        efsPropertiesTabActionProperty().get().handle(resizePropertiesViewTabEvent);
    }

    private void cacheTabController(final BaseController<Tab> controller) {
        Tab tab = controller.getControl();
        tab.setId("" + controller.hashCode());
        mapTabController.put(controller.getControl().getId(), controller);
    }

    private void onHorizontalTabClosed(final BaseController<Tab> controller) {
        controller.stop();
        unregisterSubController(controller);
        mapTabController.remove(controller.getControl().getId());
        EfsPropertiesTabEvent event = new EfsPropertiesTabEvent(this, EfsPropertiesTabEvent.REMOVE_PROPERTIES_TAB,
            getTabCount());
        efsPropertiesTabActionProperty().get().handle(event);
    }

    private int getTabCount() {
        return partPropertiesTabPane.getTabs().size();
    }

    private BaseController<Tab> getTab(final Class<?> clazz) {
        for (final BaseController<Tab> bc : mapTabController.values()) {
            if (clazz.isAssignableFrom(bc.getClass())) {
                return bc;
            }
        }

        return null;
    }

    public final ObjectProperty<EventHandler<EfsPropertiesTabEvent>> efsPropertiesTabActionProperty() { // NO_UCD (use private)
        return propertyEfsPropertiesTabAction;
    }

    public final void setEfsPropertiesTabAction(EventHandler<EfsPropertiesTabEvent> handler) {
        efsPropertiesTabActionProperty().set(handler);
    }

    public final EventHandler<EfsPropertiesTabEvent> getEfsPropertiesTabAction() {
        return efsPropertiesTabActionProperty().get();
    }

}
