package de.vw.paso.client.main.ribbonmenu.compare.costgroup;

import de.vw.paso.client.control.ribbonmenu.RibbonMenu;

public class RibbonMenuCompareCostGroup extends RibbonMenu {

  private final RibbonMenuCompareCostGroupListener listener;

  public RibbonMenuCompareCostGroup(final RibbonMenuCompareCostGroupListener listener, final String title) {
    this.listener = listener;

    setText(title);

    initialize();
  }

  private void initialize() {
    addMenuGroup(createGroupNavigation(listener));
    addMenuGroup(createGroupCompareDisplayModes(listener));
    addMenuGroup(createGroupSummary(listener));
    addMenuGroup(createGroupHighlighting());
    addMenuGroup(createGroupReport(listener));
    addMenuGroup(createGroupCompareTab(listener));
  }
}
