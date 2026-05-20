package de.vw.paso.client.main.ribbonmenu.adminarea;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuAdminAreaEvent extends AbstractRibbonMenuEvent<RibbonMenuAdminAreaListener> {

  public RibbonMenuAdminAreaEvent(final RibbonMenuAdminAreaListener listener) {
    super(listener);
  }
}
