package de.vw.paso.client.main.ribbonmenu.usermanagement;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.util.icon.ActionIcon;
import de.vw.paso.client.util.icon.FilterIcon;
import org.apache.commons.lang3.StringUtils;

public class RibbonMenuUserManagement extends RibbonMenu {

  private final RibbonMenuUserManagementListener listener;

  public RibbonMenuUserManagement(final RibbonMenuUserManagementListener listener) {
    this.listener = listener;

    setText(I18N.getString("ribbonmenu.usermanagement.title"));

    initialize();
  }

  private void initialize() {
    this.addMenuGroup(createGroupRefresh());
    this.addMenuGroup(createFilter());
  }

  private RibbonMenuGroup createGroupRefresh() {
    final RibbonMenuGroup group = new RibbonMenuGroup(StringUtils.EMPTY);
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonButton buttonRefresh = new RibbonButton(I18N.getString(
      "ribbonmenubutton.refresh"), ActionIcon.REFRESH_32X32.getImage()
    );

    buttonRefresh.setOnAction(e -> listener.handleActionRefresh());

    itemBox.addButton(buttonRefresh);

    group.addItemBox(itemBox);

    return group;
  }

  private RibbonMenuGroup createFilter() {
    final RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.filter"));
    final RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();
    final RibbonButton buttonClearFilters = new RibbonButton(
      I18N.getString("ribbonmenubutton.clearFilters"), FilterIcon.CLEARFILTERS_32X32.getImage()
    );

    buttonClearFilters.setOnAction(e -> listener.handleActionClearFilters());
    buttonClearFilters.disableProperty().bind(listener.disablePropertyClearFilters());

    itemBox.addButton(buttonClearFilters);

    group.addItemBox(itemBox);

    return group;
  }

}
