package de.vw.paso.client.explorer.vehicleconfig.tree;

import javafx.scene.image.Image;

import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.utility.StringConstant;
import org.apache.commons.lang3.StringUtils;

public class DefaultFzgConfigTreeCell<T extends VehicleConfigTreeObj> extends AbstractFzgConfigCell<T> {

    @Override
    protected void displayVehicle(T item) {
        first.setText(item.getVehicleProject().getProjectName());
        first.setMinWidth(95);

        second.setText(item.getVehicleProject().getProductKey());
        second.setMinWidth(28);

        third.setText(item.getVehicleProject().getDescription());
        third.setMinWidth(100);

        if (item.getConfigCount() > 0) {
            fourth.setText(StringConstant.LEFT_PARENTHESIS + item.getConfigCount() + StringConstant.RIGHT_PARENTHESIS);
        }

        fourth.setMinWidth(10);

        if (StringUtils.isEmpty(item.getVehicleProject().getProductKey())) {
            first.getStyleClass().add(CELL_STYLE_CLASS_NO_PRODUCT);
            second.getStyleClass().add(CELL_STYLE_CLASS_NO_PRODUCT);
            third.getStyleClass().add(CELL_STYLE_CLASS_NO_PRODUCT);
            fourth.getStyleClass().add(CELL_STYLE_CLASS_NO_PRODUCT);
        }
    }

    @Override
    protected void displayBrand(Brand item) {
        first.setMinWidth(20);
        first.setText(item.getBrandName());
    }

    @Override
    protected void displayOther(T item) {
        first.setText(item.getProductKey());
    }

    @Override
    protected void displayRecentlyUsed(T item) {
    }

    @Override
    protected Image displayFavorite(T item) {
        Boolean favorite = item.isFavorite();
        if (favorite == null) {
            return null;
        }

        ExplorerIcon icon = favorite ? ExplorerIcon.EXPLORER_STAR_16x16 : ExplorerIcon.EXPLORER_STAR_EMPTY_16x16;
        return icon.getImage();
    }
}
