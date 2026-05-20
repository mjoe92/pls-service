package de.vw.paso.client.explorer.vehicleconfig.tree;

import javafx.scene.image.Image;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class ProductKeyFzgConfigTreeCell<T extends VehicleConfigTreeObj> extends AbstractFzgConfigCell<T> {

    @Override
    protected void displayVehicle(T item) {
        VehicleProjectDTO vehicleProject = item.getVehicleProject();
        brandImage.setImage(getBrandIcon(vehicleProject.getBrandCode()));
        second.setText(vehicleProject.getProjectName());
        third.setText(vehicleProject.getDescription());

        long configCount = item.getConfigCount();
        if (configCount > 0) {
            fourth.setText(StringConstant.LEFT_PARENTHESIS + configCount + StringConstant.RIGHT_PARENTHESIS);
        }
    }

    @Override
    protected void displayBrand(Brand item) {
        first.setMinWidth(20);
        first.setText(item.name());
        second.setText(item.getBrandName());
    }

    @Override
    protected void displayOther(T item) {
        first.setText(StringUtils.isEmpty(item.getProductKey()) ? I18N.getString("tab.productid.noid") :
                item.getProductKey());
    }

    @Override
    protected void displayRecentlyUsed(T item) {
        //
    }

    @Override
    protected Image displayFavorite(T item) {
        ExplorerIcon icon =
                item.isFavorite() ? ExplorerIcon.EXPLORER_STAR_16x16 : ExplorerIcon.EXPLORER_STAR_EMPTY_16x16;
        return icon.getImage();
    }
}
