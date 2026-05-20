package de.vw.paso.client.main.ribbonmenu.compare.config;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuCompareConfigEvent extends AbstractRibbonMenuEvent<RibbonMenuCompareConfigListener> {

  public RibbonMenuCompareConfigEvent(RibbonMenuCompareConfigListener listener, String title) {
    super(listener, title);
  }
}
