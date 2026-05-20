package de.vw.paso.client.main.ribbonmenu.fzgkonfig;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuFzgKonfigEvent extends AbstractRibbonMenuEvent<RibbonMenuFzgKonfigListener> {

  public RibbonMenuFzgKonfigEvent(RibbonMenuFzgKonfigListener listener, String title) {
    super(listener, title);
  }
}
