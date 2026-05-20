package de.vw.paso.client.main.ribbonmenu.start;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuStartEvent extends AbstractRibbonMenuEvent<RibbonMenuStartListener> {

  public RibbonMenuStartEvent(RibbonMenuStartListener listener) {
    super(listener);
  }
}
