package de.vw.paso.client.main.ribbonmenu.compare.partgroup;

import de.vw.paso.client.control.ribbonmenu.RibbonMenu;

public class RibbonMenuComparePartGroup extends RibbonMenu {

  private final RibbonMenuComparePartGroupListener listener;

  public RibbonMenuComparePartGroup(final RibbonMenuComparePartGroupListener listener, final String title) {
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
