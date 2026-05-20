package de.vw.paso.client.main.ribbonmenu;

import javafx.beans.property.BooleanProperty;

public interface CompareGroupListener {

  void handleActionShowCompareDialog();

  BooleanProperty disablePropertyCompare();

}
