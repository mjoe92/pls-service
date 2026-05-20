package de.vw.paso.client.main.ribbonmenu.partgroup;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuPartGroupEvent extends AbstractRibbonMenuEvent<RibbonMenuPartGroupListener> {

  public RibbonMenuPartGroupEvent(RibbonMenuPartGroupListener listener, String title) {
    super(listener, title);
  }
}
