package de.vw.paso.client.main.ribbonmenu;

import javafx.beans.property.BooleanProperty;

public interface ReportMenuGroupListener {

  void handleActionExcelExport();

  BooleanProperty disablePropertyExcelExport();
}

