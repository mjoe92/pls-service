package de.vw.paso.client.main.ribbonmenu.compare.partlist;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuToggleButton;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.StuecklisteIcon;

public class RibbonMenuPartList extends RibbonMenu {

  private final RibbonMenuComparePartlistListener listener;

  public RibbonMenuPartList(final RibbonMenuComparePartlistListener listener, final String title) {
    this.listener = listener;

    setText(title);

    initialize();
  }

  private void initialize() {
    addMenuGroup(createGroupNavigation(listener));
    addMenuGroup(createDelta(listener));
    addMenuGroup(createGroupHighlighting());
    addMenuGroup(createGroupCompareTab(listener));
    addMenuGroup(createChooosePathButton(listener));
  }

  protected RibbonMenuGroup createDelta(final RibbonMenuComparePartlistListener listener) {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.display.modes"));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonMenuToggleButton buttonDisplayDeltaColumns = createRibbonToggleButton(
      StuecklisteIcon.DISPLAY_DELTA_32x32, listener.toggleDisplayDeltaColumnsProperty(), null
    );
    itemBox.addToggleButton(buttonDisplayDeltaColumns);
    group.addItemBox(itemBox);

    return group;
  }

  protected RibbonMenuGroup createChooosePathButton(RibbonMenuComparePartlistListener listener) {
    RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.compare.tab"));
    RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonButton buttonOpenPathChooser = createRibbonButton(ActionIcon.CHECK_32x32, e -> listener.openPathSelectionDialog(), null);

    itemBox.addButton(buttonOpenPathChooser);

    group.addItemBox(itemBox);

    return group;
  }
}
