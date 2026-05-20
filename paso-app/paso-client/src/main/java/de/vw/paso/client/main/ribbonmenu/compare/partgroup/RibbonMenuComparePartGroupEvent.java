package de.vw.paso.client.main.ribbonmenu.compare.partgroup;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuComparePartGroupEvent extends AbstractRibbonMenuEvent<RibbonMenuComparePartGroupListener> {

  public RibbonMenuComparePartGroupEvent(RibbonMenuComparePartGroupListener listener, String title) {
    super(listener, title);
  }
}
