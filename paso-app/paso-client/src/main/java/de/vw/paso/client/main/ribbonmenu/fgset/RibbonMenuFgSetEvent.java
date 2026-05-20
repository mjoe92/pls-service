package de.vw.paso.client.main.ribbonmenu.fgset;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuFgSetEvent extends AbstractRibbonMenuEvent<RibbonMenuFgSetListener> {

  public RibbonMenuFgSetEvent(final RibbonMenuFgSetListener listener, final String title) {
    super(listener, title);
  }
}
