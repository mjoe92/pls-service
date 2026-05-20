package de.vw.paso.client.main.ribbonmenu.efs;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;
import de.vw.paso.client.stueckliste.efs.EfsTabController;

public class RibbonMenuEfsEvent extends AbstractRibbonMenuEvent<EfsTabController> {

  public RibbonMenuEfsEvent(EfsTabController listener, String title) {
    super(listener, title);
  }
}
