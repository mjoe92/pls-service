package de.vw.paso.client.main.ribbonmenu.fzgkonfig;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.control.ribbonmenu.RibbonButton;
import de.vw.paso.client.control.ribbonmenu.RibbonMenu;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroup;
import de.vw.paso.client.control.ribbonmenu.RibbonMenuGroupItemHBox;
import de.vw.paso.client.util.icon.FilterIcon;
import de.vw.paso.client.util.icon.StuecklisteIcon;

public class RibbonMenuFzgKonfig extends RibbonMenu {

	private RibbonMenuFzgKonfigListener listener;

	public RibbonMenuFzgKonfig(RibbonMenuFzgKonfigListener listener, String title) {
		setText(title);

		this.listener = listener;
		initialize();
	}

	private void initialize() {
		this.addMenuGroup(createGroupFzgKonfig());
		this.addMenuGroup(createGroupFilter());
	}

  private RibbonMenuGroup createGroupFzgKonfig() {

		RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.fahrzeugkonfiguration"));
		RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

		RibbonButton buttonFzgKonfig = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.anlegen"), StuecklisteIcon.EFS_32X32.getImage());
		buttonFzgKonfig.setOnAction(e -> listener.handleActionErstelleStueckliste());
		buttonFzgKonfig.disableProperty().bind(listener.disablePropertyErstelleStueckliste());

		itemBox.addButton(buttonFzgKonfig);
		group.addItemBox(itemBox);

		return group;
	}

  private RibbonMenuGroup createGroupFilter() {

    RibbonMenuGroup group = new RibbonMenuGroup(I18N.getString("ribbonmenugroup.filter"));
    RibbonMenuGroupItemHBox itemBox = new RibbonMenuGroupItemHBox();

    RibbonButton buttonClearFilters = new RibbonButton(I18N.getString("ribbonmenubutton.fzgkonfig.clearFilters"), FilterIcon.CLEARFILTERS_32X32.getImage());
    buttonClearFilters.setOnAction(e -> listener.handleActionClearFilters());
    buttonClearFilters.disableProperty().bind(listener.disablePropertyClearFilters());

    itemBox.addButton(buttonClearFilters);
    group.addItemBox(itemBox);

    return group;
  }

}
