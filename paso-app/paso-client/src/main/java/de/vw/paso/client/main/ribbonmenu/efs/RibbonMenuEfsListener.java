package de.vw.paso.client.main.ribbonmenu.efs;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;

import de.vw.paso.client.main.ribbonmenu.CompareGroupListener;
import de.vw.paso.client.main.ribbonmenu.NavigationGroupListener;
import de.vw.paso.client.main.ribbonmenu.ReportMenuGroupListener;
import de.vw.paso.client.main.ribbonmenu.ansicht.ViewModeEfsPropertyListener;

public interface RibbonMenuEfsListener
        extends ViewModeEfsPropertyListener, NavigationGroupListener, CompareGroupListener, ReportMenuGroupListener {

    void handleActionNewEfsElement();

    void handleActionDeleteEfsElemente();

    void handleActionCopyEfsElemente();

    void handleActionCutEfsElemente();

    void handleActionPasteEfsElemente();

    void handleActionShowHistorie();

    void handleActionShowRevisionen();

    void handleActionShowSuche();

    void handleActionShowInspector();

    void handleActionShowPartPropertiesView();

    void handlerActionShowReplaceAggregat();

    BooleanProperty toggleAenderungsansichtProperty();

    ObjectProperty<DisplayMode> selectedDisplayModeproperty();

    ObjectProperty<List<DisplayMode>> availableDisplayModesProperty();

    BooleanProperty disablePropertyNewEfsElement();

    BooleanProperty disablePropertyDeleteEfsElemente();

    BooleanProperty disablePropertyCopyEfsElemente();

    BooleanProperty disablePropertyCutEfsElemente();

    BooleanProperty disablePropertyPasteEfsElemente();

    BooleanProperty disablePropertyShowHistory();

    BooleanProperty disablePropertyShowRevision();

    BooleanProperty disablePropertyShowChanges();

    BooleanProperty disablePropertyShowSuche();

    BooleanProperty disablePropertyShowInspector();

    BooleanProperty disablePropertyShowPartProperties();
}