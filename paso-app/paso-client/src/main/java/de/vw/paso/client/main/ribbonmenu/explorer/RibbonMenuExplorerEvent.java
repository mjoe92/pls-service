package de.vw.paso.client.main.ribbonmenu.explorer;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuExplorerEvent extends AbstractRibbonMenuEvent<RibbonMenuExplorerListener> {

  public RibbonMenuExplorerEvent(RibbonMenuExplorerListener listener) {
    super(listener);
  }
}
