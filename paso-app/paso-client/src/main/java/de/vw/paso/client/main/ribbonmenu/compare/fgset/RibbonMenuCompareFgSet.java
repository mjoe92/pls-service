package de.vw.paso.client.main.ribbonmenu.compare.fgset;

import de.vw.paso.client.control.ribbonmenu.RibbonMenu;

public class RibbonMenuCompareFgSet extends RibbonMenu {

  private final RibbonMenuCompareFgSetListener listener;

  public RibbonMenuCompareFgSet(final RibbonMenuCompareFgSetListener listener, final String title) {
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
