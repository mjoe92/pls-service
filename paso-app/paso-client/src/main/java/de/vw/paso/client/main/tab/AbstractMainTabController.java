package de.vw.paso.client.main.tab;

import javafx.scene.control.Tab;

import de.vw.paso.client.base.BaseController;
import de.vw.paso.client.main.ribbonmenu.AbstractRibbonMenuEvent;

public abstract class AbstractMainTabController extends BaseController<Tab> {

    public abstract AbstractRibbonMenuEvent<?> getRibbonMenuEvent();
}