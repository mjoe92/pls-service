package de.vw.paso.client.main.ribbonmenu.compare.config;

import javafx.scene.control.ToggleGroup;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuToggleButton;
import de.vw.paso.client.util.icon.StuecklisteIcon;

public class RibbonMenuCompareConfig extends RibbonMenu {

  private RibbonMenuCompareConfigListener listener;

  public RibbonMenuCompareConfig(final RibbonMenuCompareConfigListener listener, final String title) {
    this.listener = listener;

    setText(title);

    initialize();
  }

  private void initialize() {
    addMenuGroup(createGroupNavigation(listener));
    addMenuGroup(createGroupSummary(listener));
    addMenuGroup(createHighlightGroup(listener));
    addMenuGroup(createGroupHighlighting());
    addMenuGroup(createGroupCompareTab(listener));
  }

  private RibbonMenuGroup createHighlightGroup(RibbonMenuCompareConfigListener listener) {
    RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.highlight"));
    RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    ToggleGroup toggleGroup = new ToggleGroup();

    RibbonMenuToggleButton displayAllButton =
      createRibbonToggleButton(StuecklisteIcon.DISPLAY_ALL_32x32, listener.toggleDisplayAllProperty(), null);

    RibbonMenuToggleButton filterDiffButton =
      createRibbonToggleButton(StuecklisteIcon.HIGHLIGHT_DIFFERENCES_32x32, listener.toggleFilterDiffProperty(), null);

    RibbonMenuToggleButton filterCommButton =
      createRibbonToggleButton(StuecklisteIcon.HIGHLIGHT_COMMON_32x32, listener.toggleFilterCommProperty(), null);

    RibbonMenuToggleButton toggleGroupPrNumberButton =
      createRibbonToggleButton(StuecklisteIcon.GROUP_PRNUMBER_32x32, listener.toggleGroupPrNumbersProperty(), null);

    toggleGroup.getToggles().addAll(displayAllButton, filterCommButton, filterDiffButton);
    toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        displayAllButton.setSelected(true);
      }
    });

    itemBox.addToggleButton(displayAllButton);
    itemBox.addToggleButton(filterDiffButton);
    itemBox.addToggleButton(filterCommButton);
    itemBox.addToggleButton(toggleGroupPrNumberButton);

    group.addItemBox(itemBox);

    return group;
  }

}
