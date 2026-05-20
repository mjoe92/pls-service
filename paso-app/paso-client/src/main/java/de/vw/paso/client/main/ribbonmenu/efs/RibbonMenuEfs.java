package de.vw.paso.client.main.ribbonmenu.efs;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.StackPane;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuToggleButton;
import de.vw.paso.client.main.ribbonmenu.DropDownMenuItem;
import de.vw.paso.client.main.ribbonmenu.RibbonDropDownButton;
import de.vw.paso.client.stueckliste.efs.EfsTabController;
import de.vw.paso.client.stueckliste.efs.tree.InspectorItemCounter;
import de.vw.paso.client.util.highlight.SelectionHighlightManagerUtil;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.StuecklisteIcon;
import lombok.Getter;

public class RibbonMenuEfs extends RibbonMenu {

  private static final String RIBBON_MENU_GROUP_EDIT = "ribbonmenugroup.bearbeiten";
  private static final String RIBBON_MENU_GROUP_HISTORY = "ribbonmenugroup.historie";
  private static final String RIBBON_MENU_GROUP_ANSICHTANZEIGEN = "ribbonmenugroup.ansichtanzeigen";

  @Getter
  private final EfsTabController efsTabController;
  private boolean notifyListener = true;

  private javafx.beans.value.ChangeListener<InspectorItemCounter> itemCountListener;
  private final RibbonButton buttonSearch;

  public RibbonMenuEfs(final EfsTabController efsTabController, final String title) {
    this.efsTabController = efsTabController;

    setText(title);

    buttonSearch = createRibbonButton(ActionIcon.SEARCH_32X32, e -> efsTabController.handleActionShowSuche(),
      efsTabController.disablePropertyShowSuche());

    initialize();
  }

  private void initialize() {
    RibbonMenuGroup ansichtGroup = createGroupAnsicht(efsTabController);
    RibbonMenuGroupItemHBox itemBox = (RibbonMenuGroupItemHBox) ansichtGroup.getChildren().getFirst();
    createGroupViewMode(itemBox);
    this.addMenuGroup(createGroupAnsichtAnzeigen());
    this.addMenuGroup(ansichtGroup);
    this.addMenuGroup(createGroupEdit());
    this.addMenuGroup(createGroupNavigation(efsTabController));
    this.addMenuGroup(createGroupHistorie());
    this.addMenuGroup(createGroupCompare(efsTabController));
    this.addMenuGroup(createGroupReport(efsTabController));
  }

  private RibbonMenuGroup createGroupEdit() {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_EDIT));
    final RibbonMenuGroupItemHBox itemBoxTop = new RibbonMenuGroupItemHBox();
    final RibbonMenuGroupItemHBox itemBoxCenter = new RibbonMenuGroupItemHBox();
    final RibbonMenuGroupItemHBox itemBoxBottom = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonNew = createRibbonButton(ActionIcon.NEW_16X16,
      e -> efsTabController.handleActionNewEfsElement(), efsTabController.disablePropertyNewEfsElement());

    final RibbonButton buttonDelete = createRibbonButton(ActionIcon.DELETE_16X16,
      e -> efsTabController.handleActionDeleteEfsElemente(), efsTabController.disablePropertyDeleteEfsElemente());

    final RibbonButton buttonCopy = createRibbonButton(ActionIcon.COPY_16X16,
      e -> efsTabController.handleActionCopyEfsElemente(), efsTabController.disablePropertyCopyEfsElemente());

    final RibbonButton buttonCut = createRibbonButton(ActionIcon.CUT_16X16,
      e -> efsTabController.handleActionCutEfsElemente(), efsTabController.disablePropertyCutEfsElemente());

    final RibbonButton buttonPaste = createRibbonButton(ActionIcon.PASTE_16X16,
      e -> efsTabController.handleActionPasteEfsElemente(), efsTabController.disablePropertyPasteEfsElemente());

    setSize(buttonNew, 100);
    setSize(buttonCopy, 100);
    setSize(buttonPaste, 100);

    itemBoxTop.addButton(buttonNew);
    itemBoxTop.addButton(buttonDelete);
    itemBoxCenter.addButton(buttonCopy);
    itemBoxCenter.addButton(buttonCut);
    itemBoxBottom.addButton(buttonPaste);

    group.addItemBox(itemBoxTop);
    group.addItemBox(itemBoxCenter);
    group.addItemBox(itemBoxBottom);

    return group;
  }

  private RibbonMenuGroup createGroupHistorie() {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_HISTORY));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonHistory = createRibbonButton(StuecklisteIcon.HISTORIE_32X32,
      e -> efsTabController.handleActionShowHistorie(), efsTabController.disablePropertyShowHistory());

    final RibbonButton buttonRevision = createRibbonButton(StuecklisteIcon.REVISIONEN_32X32,
      e -> efsTabController.handleActionShowRevisionen(), efsTabController.disablePropertyShowRevision());

    final RibbonMenuToggleButton buttonAenderungsansicht = createRibbonToggleButton(
      StuecklisteIcon.AENDERUNGSANSICHT_32X32, efsTabController.toggleAenderungsansichtProperty(),
      efsTabController.disablePropertyShowChanges());

    itemBox.addButton(buttonHistory);
    itemBox.addButton(buttonRevision);
    itemBox.addToggleButton(buttonAenderungsansicht);

    group.addItemBox(itemBox);

    return group;
  }

  private RibbonMenuGroup createGroupAnsichtAnzeigen() {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_ANSICHTANZEIGEN));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonInspector = createRibbonButton(StuecklisteIcon.EFS_INSPECTOR_32X32,
      e -> efsTabController.handleActionShowInspector(), efsTabController.disablePropertyShowInspector());

    RibbonButton buttonReplacePart = createRibbonButton(StuecklisteIcon.EFS_REPLACE_AGGREGATE_32X32,
      e -> efsTabController.handlerActionShowReplaceAggregat(), null);

    setIcons(buttonInspector, buttonReplacePart);
    itemCountListener = (observableValue, inspectorItemCounter, t1) -> setIcons(buttonInspector, buttonReplacePart);
    efsTabController.inspectorItemCountProperty().addListener(itemCountListener);

    final RibbonButton buttonPartProperties = createRibbonButton(StuecklisteIcon.EFS_PART_PROPERTIES_32X32,
      e -> efsTabController.handleActionShowPartPropertiesView(), efsTabController.disablePropertyShowPartProperties());

    itemBox.addButton(buttonSearch);
    itemBox.addButton(buttonInspector);
    itemBox.addButton(buttonReplacePart);
    itemBox.addButton(buttonPartProperties);

    KeyCombination inspectorKeyComb = new KeyCodeCombination(KeyCode.I, KeyCombination.ALT_DOWN);
    KeyCombination replacePartKeyComb = new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN);
    KeyCombination searchKeyComb = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
    itemBox.sceneProperty().addListener((obs, ov, nv) -> {
      if (Objects.nonNull(nv)) {
        nv.getAccelerators().put(replacePartKeyComb, efsTabController::handlerActionShowReplaceAggregat);
        nv.getAccelerators().put(searchKeyComb, efsTabController::handleActionShowSuche);
        nv.getAccelerators().put(inspectorKeyComb, efsTabController::handleActionShowInspector);
      }
    });

    group.addItemBox(itemBox);

    return group;
  }

  private void setIcons(RibbonButton buttonInspector, RibbonButton buttonReplace) {
    buttonInspector.setGraphic(getInspectorIcon());
    buttonInspector.setTooltip(getInspectorTooltip());

    buttonReplace.setGraphic(getAggregateIcon());
    buttonReplace.setTooltip(getAggregateIconTooltip());

    buttonSearch.setTooltip(getSearchIconTooltip());
  }

  private Node getNotificationIcon(StuecklisteIcon icon, Supplier<Integer> itemCountSupplier) {
    int newValue = itemCountSupplier.get();
    StackPane sp = new StackPane();
    sp.setAlignment(Pos.TOP_RIGHT);
    ImageView imageView = new ImageView(icon.getImage());
    sp.getChildren().add(imageView);
    if (newValue > 0) {
      NumberCircle numberCircle = new NumberCircle(newValue);
      numberCircle.setCircleRadius(11.3);
      StackPane.setMargin(numberCircle, new Insets(3, 0, 0, 0));
      sp.getChildren().add(numberCircle);
    }
    return sp;
  }

  private Tooltip getNotificationTooltip(String tooltipKey, Supplier<Integer> itemCountSupplier) {
    Tooltip tooltip;
    int itemCount = itemCountSupplier.get();
    if (itemCount > 0) {
      tooltip = new Tooltip(I18N.getString(tooltipKey) + "\n(" + itemCount + ")");
    } else {
      tooltip = new Tooltip(I18N.getString(tooltipKey));
    }
    return tooltip;
  }

  private Node getInspectorIcon() {
    return getNotificationIcon(StuecklisteIcon.EFS_INSPECTOR_32X32,
      () -> efsTabController.inspectorItemCountProperty().get().getCompleteCount());
  }

  private Tooltip getInspectorTooltip() {
    return getNotificationTooltip("ribbonmenu.efsInspektorTooltip",
      () -> efsTabController.inspectorItemCountProperty().get().getCompleteCount());
  }

  private Node getAggregateIcon() {
    return getNotificationIcon(StuecklisteIcon.EFS_REPLACE_AGGREGATE_32X32,
      () -> efsTabController.inspectorItemCountProperty().get().getAggregateCount());
  }

  private Tooltip getAggregateIconTooltip() {
    return getNotificationTooltip("ribbonmenu.efsReplaceAggregateTooltip",
      () -> efsTabController.inspectorItemCountProperty().get().getAggregateCount());
  }

  private Tooltip getSearchIconTooltip() {
    return new Tooltip(I18N.getString("ribbonmenu.efsSearchTooltip"));
  }

  private void createGroupViewMode(RibbonMenuGroupItemHBox itemBox) {
    final RibbonButton buttonCrossHighlightToggle = new RibbonButton(
      I18N.getString("ribbonmenubutton.crossHighlightToggle"), getCrossHighlightImage());
    buttonCrossHighlightToggle.setOnAction(
      e -> SelectionHighlightManagerUtil.toggleHighlighting(buttonCrossHighlightToggle));
    buttonCrossHighlightToggle.setTooltip(
      new Tooltip(I18N.getString("ribbonmenu.crossHighlightTooltip")));
    KeyCombination highlightKeyComb = new KeyCodeCombination(KeyCode.H, KeyCombination.ALT_ANY);
    Mnemonic highlightShortcut = new Mnemonic(buttonCrossHighlightToggle, highlightKeyComb);
    itemBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (itemBox.getScene() != null) {
        itemBox.getScene().addMnemonic(highlightShortcut);
      }
    });

    DropDownMenuItem treeItem = new DropDownMenuItem(I18N.getString("ribbonmenubutton.tree"),
      () -> new ImageView(StuecklisteIcon.TREE_VIEW_32x32.getImage()));
    DropDownMenuItem listItem = new DropDownMenuItem(I18N.getString("ribbonmenubutton.table"),
      () -> new ImageView(StuecklisteIcon.TABLE_32X32.getImage()));
    DropDownMenuItem groupItem = new DropDownMenuItem(I18N.getString("ribbonmenubutton.groupview"),
      () -> new ImageView(StuecklisteIcon.GROUP_VIEW_32x32.getImage()));

    RibbonDropDownButton displayMenu = new RibbonDropDownButton("");
    displayMenu.addMenuItem(treeItem);
    displayMenu.addMenuItem(listItem);
    displayMenu.addMenuItem(groupItem);
    displayMenu.setSelectedItem(treeItem);
    groupItem.setDisable(true);

    efsTabController.availableDisplayModesProperty().addListener(
      (observableValue, oldModes, newModes) -> groupItem.setDisable(!newModes.contains(DisplayMode.GROUP)));

    displayMenu.setOnSelectionChanged(item -> {
      if (notifyListener) {
        if (item.equals(treeItem)) {
          efsTabController.selectedDisplayModeproperty().set(DisplayMode.TREE);
        } else if (item.equals(listItem)) {
          efsTabController.selectedDisplayModeproperty().set(DisplayMode.LIST);
        } else {
          efsTabController.selectedDisplayModeproperty().set(DisplayMode.GROUP);
        }
      }
      displayMenu.hide();
      displayMenu.requestLayout();
    });

    efsTabController.selectedDisplayModeproperty()
      .addListener((obs, ov, nv) -> updateDisplayMode(displayMenu, nv, treeItem, listItem, groupItem));

    DisplayMode alreadySelectedDm = efsTabController.selectedDisplayModeproperty().get();
    if (alreadySelectedDm != null) {
      updateDisplayMode(displayMenu, alreadySelectedDm, treeItem, listItem, groupItem);
    }

    itemBox.addItemBox(displayMenu);
    itemBox.addButton(buttonCrossHighlightToggle);
  }

  private void updateDisplayMode(RibbonDropDownButton displayMenu, DisplayMode selectedDisplayMode,
    DropDownMenuItem treeItem, DropDownMenuItem listItem, DropDownMenuItem groupItem) {
    notifyListener = false;
    if (selectedDisplayMode == DisplayMode.TREE) {
      displayMenu.setSelectedItem(treeItem);
    } else if (selectedDisplayMode == DisplayMode.LIST) {
      displayMenu.setSelectedItem(listItem);
    } else if (selectedDisplayMode == DisplayMode.GROUP) {
      displayMenu.setSelectedItem(groupItem);
    }
    notifyListener = true;
  }

  private Image getCrossHighlightImage() {
    if (SelectionHighlightManagerUtil.isDohighLight()) {
      return StuecklisteIcon.CROSSHIGHLIGHT_32X32.getImage();
    } else {
      return StuecklisteIcon.SINGLEHIGHLIGHT_32X32.getImage();
    }
  }

  public void stop() {
    efsTabController.inspectorItemCountProperty().removeListener(itemCountListener);
  }
}
