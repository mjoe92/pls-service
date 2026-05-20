package de.vw.paso.client.main.ribbonmenu.compare.fgset;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuCompareFgSetEvent extends AbstractRibbonMenuEvent<RibbonMenuCompareFgSetListener> {

  public RibbonMenuCompareFgSetEvent(RibbonMenuCompareFgSetListener listener, String title) {
    super(listener, title);
  }
}
