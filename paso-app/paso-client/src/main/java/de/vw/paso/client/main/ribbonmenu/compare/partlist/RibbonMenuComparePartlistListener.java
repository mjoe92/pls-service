package de.vw.paso.client.main.ribbonmenu.compare.partlist;

import javafx.beans.property.BooleanProperty;

import de.vw.paso.client.main.ribbonmenu.NavigationGroupListener;
import de.vw.paso.client.main.ribbonmenu.compare.RibbonMenuCompareListener;

public interface RibbonMenuComparePartlistListener extends RibbonMenuCompareListener, NavigationGroupListener {

    void openPathSelectionDialog();

    BooleanProperty toggleDisplayDeltaColumnsProperty();
}
