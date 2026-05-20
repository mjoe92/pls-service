package de.vw.paso.client.main.ribbonmenu.compare;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.main.ribbonmenu.CompareDisplayModesGroupListener;
import de.vw.paso.partlist.domain.ApCompareGroup;

public class CompareViewModeSelector extends RibbonMenuGroupItemHBox {

  private static final String RIBBON_MENU_VIEW_MODE_SYSTEM = "ribbonmenuchoicebox.system";
  private static final String RIBBON_MENU_VIEW_MODE_PLATFORM = "ribbonmenuchoicebox.platform";
  private static final String RIBBON_MENU_VIEW_MODE_HUT = "ribbonmenuchoicebox.hut";
  private static final String RIBBON_MENU_VIEW_MODE_WEIGHT_ALL = "ribbonmenuchoicebox.weightall";

  private final CompareDisplayModesGroupListener listener;

  public CompareViewModeSelector(CompareDisplayModesGroupListener listener) {
    super();

    this.listener = listener;

    initCheckBoxes();
  }

  private void initCheckBoxes() {
    VBox checkboxes = new VBox();
    CheckBox platformChB = new CheckBox(I18N.getString(RIBBON_MENU_VIEW_MODE_PLATFORM));
    platformChB.selectedProperty().addListener(
      (observableValue, oldVal, newVal) -> listener.handleCompareViewModeChange(ApCompareGroup.PLATFORM, newVal));
    platformChB.setSelected(true);

    CheckBox systemChB = new CheckBox(I18N.getString(RIBBON_MENU_VIEW_MODE_SYSTEM));
    systemChB.selectedProperty().addListener(
      (observableValue, oldVal, newVal) -> listener.handleCompareViewModeChange(ApCompareGroup.SYSTEM, newVal));
    systemChB.setSelected(true);

    CheckBox hutChB = new CheckBox(I18N.getString(RIBBON_MENU_VIEW_MODE_HUT));
    hutChB.selectedProperty().addListener(
      (observableValue, oldVal, newVal) -> listener.handleCompareViewModeChange(ApCompareGroup.HUT, newVal));
    hutChB.setSelected(true);

    CheckBox weightAllChB = new CheckBox(I18N.getString(RIBBON_MENU_VIEW_MODE_WEIGHT_ALL));
    weightAllChB.selectedProperty().addListener(
      (observableValue, oldVal, newVal) -> listener.handleCompareViewModeChange(ApCompareGroup.SUM, newVal));
    weightAllChB.setSelected(true);

    checkboxes.setFillWidth(true);
    checkboxes.setPadding(new Insets(0, 0, 0, 5));
    checkboxes.setSpacing(5);
    checkboxes.getChildren().add(platformChB);
    checkboxes.getChildren().add(systemChB);
    checkboxes.getChildren().add(hutChB);
    checkboxes.getChildren().add(weightAllChB);

    this.addItemBox(checkboxes);
  }
}
