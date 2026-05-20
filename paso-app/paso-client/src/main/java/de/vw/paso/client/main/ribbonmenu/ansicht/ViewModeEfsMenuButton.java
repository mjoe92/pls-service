package de.vw.paso.client.main.ribbonmenu.ansicht;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuButton;
import de.vw.paso.client.util.icon.FahrzeugIcon;
import de.vw.paso.partlist.domain.PartListViewMode;
import org.apache.commons.lang3.StringUtils;

public class ViewModeEfsMenuButton extends RibbonMenuButton {

  private static final String RIBBONMENUCHOICEBOX_VIEW_MODE_VEHICLE = "ribbonmenuchoicebox.ansicht.fahrzeug";
  private static final String RIBBONMENUCHOICEBOX_VIEW_MODE_VEHICLE_ALL = "ribbonmenuchoicebox.ansicht.gesamtfahrzeug";
  private static final String RIBBONMENUCHOICEBOX_VIEW_MODE_ENGINE = "ribbonmenuchoicebox.ansicht.motor";
  private static final String RIBBONMENUCHOICEBOX_VIEW_MODE_GEARBOX = "ribbonmenuchoicebox.ansicht.getriebe";
  private static final String RIBBONMENUCHOICEBOX_VIEW_MODE_ENGINE_AND_GEARBOX = "ribbonmenuchoicebox.ansicht.motor.und.getriebe";

  private final ViewModeEfsPropertyListener listener;

  private ObjectProperty<PartListViewMode> viewModeEfsProperty = new SimpleObjectProperty<>();
  private ToggleGroup toggleGroup;
  private RadioMenuItem menuItemVehicle;
  private RadioMenuItem menuItemVehicleAll;
  private RadioMenuItem menuItemEngine;
  private RadioMenuItem menuItemGearbox;
  private RadioMenuItem menuItemEngineAndGearbox;

  public ViewModeEfsMenuButton(final ViewModeEfsPropertyListener listener) {
    super(StringUtils.EMPTY);

    this.listener = listener;

    initializeViewModeEfsMenuButtons();
  }

  private void initializeViewModeEfsMenuButtons() {
    getStyleClass().remove(STYLE_RIBBON_BUTTON);
    getStyleClass().add(STYLE_RIBBON_MENU_BUTTON);
    setPrefWidth(168);

    createMenu();
    addToggleChangeListener();
    viewModeEfsProperty.bindBidirectional(listener.viewModeEfsProperty());
    handleEfsViewModeChange(viewModeEfsProperty.get());
  }

  private void addToggleChangeListener() {
    getToggleGroup().selectedToggleProperty().addListener((o, oldValue, newValue) -> handleEfsViewModeChange(newValue));
  }

  private void handleEfsViewModeChange(Toggle newValue) {
    if (newValue == getMenuItemVehicleAll()) {
      handleEfsViewModeChange(PartListViewMode.VEHICLE_ALL);
    } else if (newValue == getMenuItemVehicle()) {
      handleEfsViewModeChange(PartListViewMode.VEHICLE);
    } else if (newValue == getMenuItemEngine()) {
      handleEfsViewModeChange(PartListViewMode.ENGINE);
    } else if (newValue == getMenuItemGearbox()) {
      handleEfsViewModeChange(PartListViewMode.GEARBOX);
    } else if (newValue == getMenuItemEngineAndGearbox()) {
      handleEfsViewModeChange(PartListViewMode.ENGINE_AND_GEARBOX);
    }
  }

  private void handleEfsViewModeChange(PartListViewMode partListViewMode) {
    if (partListViewMode == null) {
      setViewModeEfs(createIconVehicle(), getMenuItemVehicleAll().getText(), PartListViewMode.VEHICLE_ALL);
      getMenuItemVehicleAll().setSelected(true);
      return;
    }
    switch (partListViewMode) {
      case VEHICLE_ALL:
        setViewModeEfs(createIconVehicle(), getMenuItemVehicleAll().getText(), PartListViewMode.VEHICLE_ALL);
        selectMenuItem(getMenuItemVehicleAll());
        break;
      case VEHICLE:
        setViewModeEfs(createIconVehicle(), getMenuItemVehicle().getText(), PartListViewMode.VEHICLE);
        selectMenuItem(getMenuItemVehicle());
        break;
      case ENGINE:
        setViewModeEfs(createIconEngine(), getMenuItemEngine().getText(), PartListViewMode.ENGINE);
        selectMenuItem(getMenuItemEngine());
        break;
      case GEARBOX:
        setViewModeEfs(createIconGearbox(), getMenuItemGearbox().getText(), PartListViewMode.GEARBOX);
        selectMenuItem(getMenuItemGearbox());
        break;
      case ENGINE_AND_GEARBOX:
        setViewModeEfs(createIconEngineAndGearbox(), getMenuItemEngineAndGearbox().getText(),
          PartListViewMode.ENGINE_AND_GEARBOX);
        selectMenuItem(getMenuItemEngineAndGearbox());
        break;
    }

    // CSS wird anscheinend nicht aktualisiert, so dass trotz CSS-Angabe nicht zentriert formatiert wird
    setAlignment(Pos.TOP_CENTER);
  }

  private void selectMenuItem(RadioMenuItem item) {
    if (!item.isSelected()) {
      item.setSelected(true);
    }
  }

  private void setViewModeEfs(GridPane graphic, String text, PartListViewMode viewMode) {
    setGraphic(graphic);
    setText(text);
    viewModeEfsProperty.set(viewMode);
  }

  private void createMenu() {
    setText(getMenuItemVehicle().getText());
    this.getItems().add(getMenuItemVehicleAll());
    this.getItems().add(getMenuItemEngineAndGearbox());
    this.getItems().add(getMenuItemVehicle());
    this.getItems().add(getMenuItemEngine());
    this.getItems().add(getMenuItemGearbox());
  }

  private ToggleGroup getToggleGroup() {
    if (toggleGroup == null) {
      toggleGroup = new ToggleGroup();
    }
    return toggleGroup;
  }

  private RadioMenuItem getMenuItemVehicle() {
    if (menuItemVehicle == null) {
      menuItemVehicle = new RadioMenuItem(I18N.getString(RIBBONMENUCHOICEBOX_VIEW_MODE_VEHICLE), createIconVehicle());
      menuItemVehicle.setToggleGroup(getToggleGroup());
    }
    return menuItemVehicle;
  }

  private RadioMenuItem getMenuItemVehicleAll() {
    if (menuItemVehicleAll == null) {
      menuItemVehicleAll = new RadioMenuItem(I18N.getString(RIBBONMENUCHOICEBOX_VIEW_MODE_VEHICLE_ALL),
        createIconVehicle());
      menuItemVehicleAll.setToggleGroup(getToggleGroup());
    }
    return menuItemVehicleAll;
  }

  private RadioMenuItem getMenuItemEngine() {
    if (menuItemEngine == null) {
      menuItemEngine = new RadioMenuItem(I18N.getString(RIBBONMENUCHOICEBOX_VIEW_MODE_ENGINE), createIconEngine());
      menuItemEngine.setToggleGroup(getToggleGroup());
    }
    return menuItemEngine;
  }

  private RadioMenuItem getMenuItemGearbox() {
    if (menuItemGearbox == null) {
      menuItemGearbox = new RadioMenuItem(I18N.getString(RIBBONMENUCHOICEBOX_VIEW_MODE_GEARBOX), createIconGearbox());
      menuItemGearbox.setToggleGroup(getToggleGroup());
    }
    return menuItemGearbox;
  }

  private RadioMenuItem getMenuItemEngineAndGearbox() {
    if (menuItemEngineAndGearbox == null) {
      menuItemEngineAndGearbox = new RadioMenuItem(I18N.getString(RIBBONMENUCHOICEBOX_VIEW_MODE_ENGINE_AND_GEARBOX),
        createIconEngineAndGearbox());
      menuItemEngineAndGearbox.setToggleGroup(getToggleGroup());
    }
    return menuItemEngineAndGearbox;
  }

  private GridPane createIconVehicle() {
    return createMenuIcon("#e0e8b6", new ImageView(FahrzeugIcon.FZG_24X24.getImage()));
  }

  private GridPane createIconEngine() {
    return createMenuIcon("#ffc000", new ImageView(FahrzeugIcon.MOTOR_24X24.getImage()));
  }

  private GridPane createIconGearbox() {
    return createMenuIcon("#bfbfbf", new ImageView(FahrzeugIcon.GETRIEBE_24X24.getImage()));
  }

  private GridPane createIconEngineAndGearbox() {
    return createMenuIcon("#c3dbec", new ImageView(FahrzeugIcon.ENGINE_GEARBOX_24x24.getImage()));
  }

  private GridPane createMenuIcon(String color, ImageView iv) {
    Pane pane = new Pane();
    pane.setStyle("-fx-background-color: " + color + ";");
    pane.setBorder(Border.EMPTY);
    pane.setPrefWidth(32);
    pane.setPrefHeight(8);

    GridPane gridPane = new GridPane();
    gridPane.setStyle("-fx-text-alignment: center; -fx-border-color: black;");

    gridPane.add(pane, 0, 0);
    gridPane.add(iv, 0, 1);

    GridPane.setHalignment(iv, HPos.CENTER);

    gridPane.setMinSize(32, 32);
    gridPane.setMaxSize(32, 32);

    return gridPane;
  }

}
