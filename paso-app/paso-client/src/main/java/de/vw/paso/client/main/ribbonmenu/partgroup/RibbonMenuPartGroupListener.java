package de.vw.paso.client.main.ribbonmenu.partgroup;

import de.vw.paso.client.main.ribbonmenu.CompareGroupListener;
import de.vw.paso.client.main.ribbonmenu.NavigationGroupListener;
import de.vw.paso.client.main.ribbonmenu.NumberOfPartsGroupListener;
import de.vw.paso.client.main.ribbonmenu.ReportMenuGroupListener;
import de.vw.paso.client.main.ribbonmenu.SummaryGroupListener;
import de.vw.paso.client.main.ribbonmenu.ansicht.ViewModeEfsPropertyListener;

public interface RibbonMenuPartGroupListener
        extends ViewModeEfsPropertyListener, NavigationGroupListener, CompareGroupListener, NumberOfPartsGroupListener,
        ReportMenuGroupListener, SummaryGroupListener {

}
