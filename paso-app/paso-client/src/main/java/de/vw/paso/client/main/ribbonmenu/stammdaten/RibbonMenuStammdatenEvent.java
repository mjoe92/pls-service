package de.vw.paso.client.main.ribbonmenu.stammdaten;

import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public class RibbonMenuStammdatenEvent extends AbstractRibbonMenuEvent<RibbonMenuMasterDataListener> {

    public RibbonMenuStammdatenEvent(RibbonMenuMasterDataListener listener) {
        super(listener);
    }
}