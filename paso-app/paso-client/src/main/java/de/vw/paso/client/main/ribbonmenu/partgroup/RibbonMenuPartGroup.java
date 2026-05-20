package de.vw.paso.client.main.ribbonmenu.partgroup;

import de.vw.paso.client.control.ribbonmenu.RibbonMenu;

public class RibbonMenuPartGroup extends RibbonMenu {

  public RibbonMenuPartGroup(final RibbonMenuPartGroupListener listener, final String title) {
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
