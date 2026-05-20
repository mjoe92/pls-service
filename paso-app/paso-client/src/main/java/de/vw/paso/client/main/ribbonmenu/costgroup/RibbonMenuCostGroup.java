package de.vw.paso.client.main.ribbonmenu.costgroup;

import de.vw.paso.client.control.ribbonmenu.RibbonMenu;

public class RibbonMenuCostGroup extends RibbonMenu {

  public RibbonMenuCostGroup(final RibbonMenuCostGroupListener listener, final String title) {
    setText(title);

    addMenuGroup(createGroupAnsicht(listener));
    addMenuGroup(createGroupNavigation(listener));
    addMenuGroup(createGroupCompare(listener));
    addMenuGroup(createGroupNumberOfParts(listener));
    addMenuGroup(createGroupSummary(listener));
    addMenuGroup(createGroupHighlighting());
    addMenuGroup(createGroupReport(listener));
  }

}
