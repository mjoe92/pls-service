package de.vw.paso.client.explorer.vehicleconfig.tree;

import javafx.scene.image.Image;

import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.utility.StringConstant;

public class FavoritesFzgConfigTreeCell<T extends VehicleConfigTreeObj> extends AbstractFzgConfigCell<T> {

    @Override
    protected void displayVehicle(T item) {
        brandImage.setImage(getBrandIcon(item.getVehicleProject().getBrandCode()));

        first.setMinWidth(95);
        first.setText(item.getVehicleProject().getProjectName());

        second.setText(item.getVehicleProject().getProductKey());
        second.setMinWidth(28);

        third.setText(item.getVehicleProject().getDescription());
        third.setMinWidth(100);

        if (item.getConfigCount() > 0) {
            fourth.setText(StringConstant.LEFT_PARENTHESIS + item.getConfigCount() + StringConstant.RIGHT_PARENTHESIS);
        }
        fourth.setMinWidth(10);
    }

    @Override
    protected void displayBrand(Brand item) {
        first.setMinWidth(20);
        first.setText(item.name());
        second.setText(item.getBrandName());
    }

    @Override
    protected void displayOther(T item) {
        first.setText(item.getValue());
    }

    @Override
    protected void displayRecentlyUsed(T item) {
        VehicleConfigDTO vehicleConfig = item.getVehicleConfig();
        if (vehicleConfig == null) {
            return;
        }

        VehicleProjectDTO vehicleProject = vehicleConfig.getVehicleProject();
        Image brandIcon = getBrandIcon(vehicleProject.getBrandCode());
        brandImage.setImage(brandIcon);

        first.setMinWidth(95);
        first.setText(vehicleProject.getProjectName());

        second.setMinWidth(28);
        second.setText(vehicleProject.getProductKey());

        third.setMinWidth(100);
        third.setText(vehicleConfig.getName());
    }

    @Override
    protected Image displayFavorite(T item) {
        return ExplorerIcon.EXPLORER_STAR_16x16.getImage();
    }
}
