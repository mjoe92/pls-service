package de.vw.paso.client.main.ribbonmenu.compare.config;

import javafx.beans.property.BooleanProperty;

import de.vw.paso.client.main.ribbonmenu.NavigationGroupListener;
import de.vw.paso.client.main.ribbonmenu.ReportMenuGroupListener;
import de.vw.paso.client.main.ribbonmenu.SummaryGroupListener;
import de.vw.paso.client.main.ribbonmenu.compare.RibbonMenuCompareListener;

public interface RibbonMenuCompareConfigListener
        extends NavigationGroupListener, SummaryGroupListener, ReportMenuGroupListener, RibbonMenuCompareListener {

    BooleanProperty toggleDisplayAllProperty();

    BooleanProperty toggleFilterDiffProperty();

    BooleanProperty toggleFilterCommProperty();

    BooleanProperty toggleGroupPrNumbersProperty();
}
