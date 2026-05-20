package de.vw.paso.client.control.ribbonmenu;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Mnemonic;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.main.ribbonmenu.CompareDisplayModesGroupListener;
import de.vw.paso.client.main.ribbonmenu.CompareGroupListener;
import de.vw.paso.client.main.ribbonmenu.NavigationGroupListener;
import de.vw.paso.client.main.ribbonmenu.NumberOfPartsGroupListener;
import de.vw.paso.client.main.ribbonmenu.ReportMenuGroupListener;
import de.vw.paso.client.main.ribbonmenu.SummaryGroupListener;
import de.vw.paso.client.main.ribbonmenu.ansicht.ViewModeEfsMenuButton;
import de.vw.paso.client.main.ribbonmenu.ansicht.ViewModeEfsPropertyListener;
import de.vw.paso.client.main.ribbonmenu.compare.CompareViewModeSelector;
import de.vw.paso.client.main.ribbonmenu.compare.RibbonMenuCompareListener;
import de.vw.paso.client.util.highlight.SelectionHighlightManagerUtil;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.FilterIcon;
import de.vw.paso.client.util.icon.Icon;
import de.vw.paso.client.util.icon.StuecklisteIcon;

public abstract class RibbonMenu extends Tab {

  private static final String BUNDLE_NAME = "ribbon-menu-bundle";
  private static final String STYLE_RIBBON_MENU_TAB = "ribbon-menu-tab";
  private static final String STYLE_RIBBON_MENU_GROUPS_BOX = "ribbon-menu-groups-box";
  private static final String PREFIX_RIBBON_MENU_BUTTON = "ribbonmenubutton.";
  private static final String RIBBON_MENU_GROUP_ANSICHT = "ribbonmenugroup.ansicht";
  private static final String RIBBON_MENU_GROUP_SUMMARY = "ribbonmenugroup.summary";
  private static final String RIBBON_MENU_GROUP_DISPLAY_MODES = "ribbonmenugroup.display.modes";
  private static final String RIBBON_MENU_GROUP_NAVIGATION = "ribbonmenugroup.navigation";
  private static final String RIBBON_MENU_GROUP_HIGHLIGHTING = "ribbonmenugroup.highlighting";
  private static final String RIBBON_MENU_GROUP_COMPARE = "ribbonmenugroup.compare";
  private static final String RIBBON_MENU_GROUP_REPORT = "ribbonmenugroup.report";

  private final HBox boxMenuGroups;

  protected RibbonMenu() {
    boxMenuGroups = new HBox();

    setClosable(false);
    setContent(boxMenuGroups);
    styleMenu();
  }

  protected void addMenuGroup(final RibbonMenuGroup group) {
    boxMenuGroups.getChildren().add(group);
  }

  protected RibbonMenuGroup createGroupAnsicht(final ViewModeEfsPropertyListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_ANSICHT));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final ViewModeEfsMenuButton ansichtEfsMenuButton = new ViewModeEfsMenuButton(listener);

    itemBox.addItemBox(ansichtEfsMenuButton);
    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createGroupNavigation(final NavigationGroupListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_NAVIGATION));
    final RibbonMenuGroupItemHBox itemBoxTop = new RibbonMenuGroupItemHBox();
    final RibbonMenuGroupItemHBox itemBoxBottom = new RibbonMenuGroupItemHBox();
    final RibbonMenuGroupItemHBox clearFilterItemBox = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonBack = createRibbonButton(ActionIcon.BACK_16x16, e -> listener.handleActionNavigateBack(),
      listener.disablePropertyNavigateBack());

    final RibbonButton buttonForward = createRibbonButton(ActionIcon.FORWARD_16x16,
      e -> listener.handleActionNavigateForward(), listener.disablePropertyNavigateForward());

    final RibbonButton buttonClearFilters = createRibbonButton(FilterIcon.CLEARFILTERS_16X16,
      e -> listener.handleActionClearFilters(), listener.disablePropertyClearFilters());

    final RibbonButton buttonResetSorting = createRibbonButton(ActionIcon.RESET_SORTING_16x16,
      e -> listener.handleActionResetSorting(), listener.disablePropertyResetSorting());

    final RibbonButton buttonCollapse = createRibbonButton(ActionIcon.COLLAPSE_16x16, e -> {
    }, listener.disablePropertyCollapseTree());
    buttonCollapse.addEventFilter(KeyEvent.ANY, event -> addKeyEventToButtonCollapse(listener, event));
    buttonCollapse.addEventFilter(MouseEvent.ANY, event -> addMouseEventToButtonCollapse(listener, event));
    buttonCollapse.setTooltip(new Tooltip(
      I18N.getString("ribbonmenu.collapseTooltip1") + "\n" + I18N.getBundle()
        .getString("ribbonmenu.collapseTooltip2")));

    KeyCombination collapseKeyComb = new KeyCodeCombination(KeyCode.UP, KeyCombination.ALT_DOWN);
    Mnemonic collapseShortcut = new Mnemonic(buttonCollapse, collapseKeyComb);
    KeyCombination collapseKeyComb2 = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);
    Mnemonic collapseShortcut2 = new Mnemonic(buttonCollapse, collapseKeyComb2);

    final RibbonButton buttonExpand = createRibbonButton(ActionIcon.EXPAND_16x16, e -> {
    }, listener.disablePropertyExpandTree());
    buttonExpand.addEventFilter(KeyEvent.ANY, event -> {
      addKeyEventToButtonExpand(listener, event);
    });
    buttonExpand.addEventFilter(MouseEvent.ANY, event -> {
      addMouseEventToButtonExpand(listener, event);
    });
    buttonExpand.setTooltip(new Tooltip(
      I18N.getString("ribbonmenu.expandTooltip1") + "\n" + I18N.getBundle()
        .getString("ribbonmenu.expandTooltip2")));

    KeyCombination expandKeyComb = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.ALT_DOWN);
    Mnemonic expandShortcut = new Mnemonic(buttonExpand, expandKeyComb);
    KeyCombination expandKeyComb2 = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);
    Mnemonic expandShortcut2 = new Mnemonic(buttonExpand, expandKeyComb2);

    final RibbonButton buttonCollapseAll = createRibbonButton(ActionIcon.COLLAPSEALL_16x16, e -> {
    }, listener.disablePropertyCollapseAllTree());
    buttonCollapseAll.addEventFilter(MouseEvent.MOUSE_RELEASED,
      event -> addMouseEventToButtonCollapseAll(listener, event));
    buttonCollapseAll.addEventFilter(KeyEvent.KEY_RELEASED, event -> addKeyEventToButtonCollapseAll(listener, event));
    buttonCollapseAll.setTooltip(new Tooltip(I18N.getString("ribbonmenu.collapseAllTooltip")));

    final RibbonButton buttonExpandAll = createRibbonButton(ActionIcon.EXPANDALL_16x16, e -> {
    }, listener.disablePropertyExpandAllTree());
    buttonExpandAll.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getEventType() == MouseEvent.MOUSE_RELEASED) {
        listener.handleActionExpandAllTree();
      }
    });
    buttonExpandAll.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
      if (event.isAltDown() && event.getCode() == KeyCode.PAGE_DOWN) {
        listener.handleActionExpandAllTree();
      }
    });
    buttonExpandAll.setTooltip(new Tooltip(I18N.getString("ribbonmenu.expandAllTooltip")));

    KeyCombination collapseAllKeyComb = new KeyCodeCombination(KeyCode.PAGE_UP, KeyCombination.ALT_DOWN);
    Mnemonic collapseAllShortcut = new Mnemonic(buttonCollapseAll, collapseAllKeyComb);
    KeyCombination expandAllKeyComb = new KeyCodeCombination(KeyCode.PAGE_DOWN, KeyCombination.ALT_DOWN);
    Mnemonic expandAllShortcut = new Mnemonic(buttonExpandAll, expandAllKeyComb);

    setSize(buttonBack, 85);
    setSize(buttonForward, 85);
    setSize(buttonCollapse, 100);
    setSize(buttonExpand, 100);

    itemBoxTop.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (itemBoxTop.getScene() != null) {
        itemBoxTop.getScene().getMnemonics().clear();

        itemBoxTop.getScene().addMnemonic(collapseShortcut);
        itemBoxTop.getScene().addMnemonic(collapseShortcut2);
        itemBoxTop.getScene().addMnemonic(collapseAllShortcut);
      }
    });
    itemBoxBottom.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (itemBoxBottom.getScene() != null && !itemBoxBottom.getScene().getMnemonics().containsKey(expandKeyComb)
        && !itemBoxBottom.getScene().getMnemonics().containsKey(expandKeyComb2) && !itemBoxBottom.getScene()
        .getMnemonics().containsKey(expandAllKeyComb)) {
        itemBoxBottom.getScene().addMnemonic(expandShortcut);
        itemBoxBottom.getScene().addMnemonic(expandShortcut2);
        itemBoxBottom.getScene().addMnemonic(expandAllShortcut);
      }
    });

    clearFilterItemBox.addButton(buttonClearFilters);
    clearFilterItemBox.addButton(buttonResetSorting);
    itemBoxTop.addButton(buttonBack);
    itemBoxTop.addButton(buttonCollapse);
    itemBoxTop.addButton(buttonCollapseAll);
    itemBoxBottom.addButton(buttonForward);
    itemBoxBottom.addButton(buttonExpand);
    itemBoxBottom.addButton(buttonExpandAll);

    group.addItemBox(clearFilterItemBox);
    group.addItemBox(itemBoxTop);
    group.addItemBox(itemBoxBottom);

    return group;
  }

  private static void addKeyEventToButtonCollapseAll(NavigationGroupListener listener, KeyEvent event) {
    if (event.isAltDown() && (event.getCode() == KeyCode.PAGE_UP)) {
      listener.handleActionCollapseAllTree();
    }
  }

  private static void addMouseEventToButtonCollapseAll(NavigationGroupListener listener, MouseEvent event) {
    if ((event.getButton() == MouseButton.PRIMARY) && (event.getEventType() == MouseEvent.MOUSE_RELEASED)) {
      listener.handleActionCollapseAllTree();
    }
  }

  private static void addMouseEventToButtonExpand(NavigationGroupListener listener, MouseEvent event) {
    if ((event.getButton() == MouseButton.PRIMARY) && (event.getEventType() == MouseEvent.MOUSE_RELEASED)) {
      listener.handleActionExpandTree();
    }
  }

  private static void addKeyEventToButtonExpand(NavigationGroupListener listener, KeyEvent event) {
    if (event.isAltDown() && event.getCode() == KeyCode.RIGHT) {
      listener.handleActionExpandTree();
    }
    if (event.isAltDown() && event.getCode() == KeyCode.DOWN) {
      listener.handleActionExpandTree();
    }
  }

  private static void addMouseEventToButtonCollapse(NavigationGroupListener listener, MouseEvent event) {
    if (event.getButton() == MouseButton.PRIMARY && (event.getEventType() == MouseEvent.MOUSE_RELEASED)) {
      listener.handleActionCollapseTree();
    }
  }

  private static void addKeyEventToButtonCollapse(NavigationGroupListener listener, KeyEvent event) {
    if (event.isAltDown() && event.getCode() == KeyCode.LEFT) {
      listener.handleActionCollapseTree();
    }
    if (event.isAltDown() && event.getCode() == KeyCode.UP) {
      listener.handleActionCollapseTree();
    }
  }

  protected RibbonMenuGroup createGroupNumberOfParts(final NumberOfPartsGroupListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_DISPLAY_MODES));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonMenuToggleButton buttonDisplayNumberOfParts = createRibbonToggleButton(
      StuecklisteIcon.NUMBER_OF_PARTS_32x32, listener.toggleDisplayNumberOfPartsProperty(), null);

    itemBox.addToggleButton(buttonDisplayNumberOfParts);
    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createGroupCompareDisplayModes(final CompareDisplayModesGroupListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_DISPLAY_MODES));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonMenuToggleButton buttonDisplayNumberOfParts = createRibbonToggleButton(
      StuecklisteIcon.NUMBER_OF_PARTS_32x32, listener.toggleDisplayNumberOfPartsProperty(), null);
    final RibbonMenuToggleButton buttonDisplayDeltaColumns = createRibbonToggleButton(
      StuecklisteIcon.DISPLAY_DELTA_32x32, listener.toggleDisplayDeltaColumnsProperty(), null);
    final CompareViewModeSelector compareViewMode = new CompareViewModeSelector(listener);

    itemBox.addToggleButton(buttonDisplayNumberOfParts);
    itemBox.addToggleButton(buttonDisplayDeltaColumns);
    itemBox.addItemBox(compareViewMode);
    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createGroupSummary(final SummaryGroupListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_SUMMARY));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonButton summaryCloseRibbonButton = createRibbonButton(ActionIcon.CLOSE_32X32,
      e -> listener.handleActionCloseSummary(), listener.disablePropertyCloseSummary());

    itemBox.addButton(summaryCloseRibbonButton);
    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createGroupHighlighting() {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_HIGHLIGHTING));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonCrossHighlightToggle = new RibbonButton(
      I18N.getString("ribbonmenubutton.crossHighlightToggle"),
      SelectionHighlightManagerUtil.getCrossHighlightImage());
    buttonCrossHighlightToggle.setOnAction(
      e -> SelectionHighlightManagerUtil.toggleHighlighting(buttonCrossHighlightToggle));

    itemBox.addButton(buttonCrossHighlightToggle);

    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createGroupCompare(CompareGroupListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_COMPARE));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonCompare = createRibbonButton(StuecklisteIcon.COMPARE_32x32,
      e -> listener.handleActionShowCompareDialog(), listener.disablePropertyCompare());

    itemBox.addButton(buttonCompare);

    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createGroupReport(ReportMenuGroupListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString(RIBBON_MENU_GROUP_REPORT));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

    final RibbonButton buttonExport = createRibbonButton(ActionIcon.EXCEL_32x32,
      e -> listener.handleActionExcelExport(), listener.disablePropertyExcelExport());

    itemBox.addButton(buttonExport);

    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonButton createRibbonButton(Icon icon, EventHandler<ActionEvent> event,
    ObservableValue<? extends Boolean> observable) {
    final RibbonButton button = new RibbonButton(
      I18N.getString(PREFIX_RIBBON_MENU_BUTTON + icon.getIconName()), icon.getImage());

    button.setOnAction(event);
    if (observable != null) {
      button.disableProperty().bind(observable);
    }
    return button;
  }

  protected RibbonMenuToggleButton createRibbonToggleButton(Icon icon, Property<Boolean> toggleState,
    ObservableValue<? extends Boolean> observable) {
    //TODO Change this: the text is linked to the icon name. Bad idea!
    final RibbonMenuToggleButton toggleButton = new RibbonMenuToggleButton(
      I18N.getString(PREFIX_RIBBON_MENU_BUTTON + icon.getIconName()), icon.getImage());
    toggleButton.selectedProperty().bindBidirectional(toggleState);
    if (observable != null) {
      toggleButton.disableProperty().bind(observable);
    }
    return toggleButton;
  }

  protected void setSize(final RibbonButton button, final double width) {
    button.setPrefWidth(width);
  }

  private void styleMenu() {
    getStyleClass().add(STYLE_RIBBON_MENU_TAB);

    boxMenuGroups.getStyleClass().add(STYLE_RIBBON_MENU_GROUPS_BOX);
  }

  protected RibbonMenuGroup createGroupCompareTab(RibbonMenuCompareListener listener) {
    RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.compare.tab"));
    RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonButton buttonExport = createRibbonButton(ActionIcon.COMPARE_RELOAD_32x32,
      e -> listener.actionReopenCompareTabs(), null);

    itemBox.addButton(buttonExport);

    group.addItemBox(itemBox);

    return group;
  }

}
