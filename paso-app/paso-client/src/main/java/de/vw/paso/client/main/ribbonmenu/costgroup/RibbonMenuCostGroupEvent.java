package de.vw.paso.client.main.ribbonmenu.costgroup;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuCostGroupEvent extends AbstractRibbonMenuEvent<RibbonMenuCostGroupListener> {

  public RibbonMenuCostGroupEvent(final RibbonMenuCostGroupListener listener, final String title) {
    super(listener, title);
  }
}
