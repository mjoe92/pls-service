package de.vw.paso.client.main.ribbonmenu;

import javafx.beans.property.BooleanProperty;

import de.vw.paso.partlist.domain.ApCompareGroup;

public interface CompareDisplayModesGroupListener {

    BooleanProperty toggleDisplayNumberOfPartsProperty();

    BooleanProperty toggleDisplayDeltaColumnsProperty();

    void handleCompareViewModeChange(ApCompareGroup selectedApGroup, Boolean newVal);

}
