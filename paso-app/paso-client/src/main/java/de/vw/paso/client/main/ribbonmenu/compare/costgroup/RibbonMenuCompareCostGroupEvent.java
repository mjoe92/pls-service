package de.vw.paso.client.main.ribbonmenu.compare.costgroup;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuCompareCostGroupEvent extends AbstractRibbonMenuEvent<RibbonMenuCompareCostGroupListener> {

  public RibbonMenuCompareCostGroupEvent(RibbonMenuCompareCostGroupListener listener, String title) {
    super(listener, title);
  }
}
