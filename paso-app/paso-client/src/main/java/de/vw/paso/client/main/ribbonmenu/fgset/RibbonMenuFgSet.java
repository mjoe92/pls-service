package de.vw.paso.client.main.ribbonmenu.fgset;

import de.vw.paso.client.control.ribbonmenu.RibbonMenu;

public class RibbonMenuFgSet extends RibbonMenu {

  public RibbonMenuFgSet(final RibbonMenuFgSetListener listener, final String title) {
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
