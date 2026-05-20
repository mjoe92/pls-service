package de.vw.paso.client.main.ribbonmenu.compare.partlist;


import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMeunComparePartlisEvent extends AbstractRibbonMenuEvent<RibbonMenuComparePartlistListener> {

  public RibbonMeunComparePartlisEvent(RibbonMenuComparePartlistListener listener, String title) {
    super(listener, title);
  }
}
