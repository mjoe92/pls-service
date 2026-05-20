package de.vw.paso.client.main.ribbonmenu.costgroup;

import de.vw.paso.client.main.ribbonmenu.CompareGroupListener;
import de.vw.paso.client.main.ribbonmenu.NavigationGroupListener;
import de.vw.paso.client.main.ribbonmenu.NumberOfPartsGroupListener;
import de.vw.paso.client.main.ribbonmenu.ReportMenuGroupListener;
import de.vw.paso.client.main.ribbonmenu.SummaryGroupListener;
import de.vw.paso.client.main.ribbonmenu.ansicht.ViewModeEfsPropertyListener;

public interface RibbonMenuCostGroupListener
        extends ViewModeEfsPropertyListener, NavigationGroupListener, SummaryGroupListener, CompareGroupListener,
        ReportMenuGroupListener, NumberOfPartsGroupListener {

}
