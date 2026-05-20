package de.vw.paso.client.explorer.vehicleconfig.tree;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObjType;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public interface FzgKonfigTreeConfiguration {

    TreeItem<VehicleConfigTreeObj> createTree(Collection<VehicleConfigDTO> vehicleConfigs,
        Collection<VehicleConfigDTO> deletedConfigs, Collection<VehicleProjectDTO> brandToVehicleProjectsMap,
        Map<Long, Long> configCountMap, PasoWildCardPattern patternSearchTerm, List<Long> favoriteIds);

    TreeCell<VehicleConfigTreeObj> createCell();

    String getTitle();

    default boolean matchFzgProjekt(VehicleProjectDTO vehicleProject, PasoWildCardPattern patternSearchTerm) {
        return patternSearchTerm == null || patternSearchTerm.matches(vehicleProject.getProjectName()) != null
            || patternSearchTerm.matches(vehicleProject.getDescription()) != null
            || patternSearchTerm.matches(vehicleProject.getProductKey()) != null
            || patternSearchTerm.matches(vehicleProject.getSalesKey()) != null || (
            vehicleProject.getFirstModelYear() != null
                && patternSearchTerm.matches(vehicleProject.getFirstModelYear().toString()) != null)
            || patternSearchTerm.matches(vehicleProject.getPlatform()) != null;
    }

    default TreeItem<VehicleConfigTreeObj> createTreeItemFzgProjekt(VehicleProjectDTO vehicleProject,
        Map<Long, Long> configCountMap, boolean isFavorite) {
        VehicleConfigTreeObj treeObject = new VehicleConfigTreeObj();
        treeObject.setVehicleProject(vehicleProject);
        treeObject.setConfigCount(configCountMap);
        treeObject.setFavorite(isFavorite);

        TreeItem<VehicleConfigTreeObj> treeItemFzgProjekt = new TreeItem<>(treeObject);

        treeItemFzgProjekt.setExpanded(true);
        treeItemFzgProjekt.setGraphic(new ImageView(ExplorerIcon.EXPLORER_FZGPROJEKT_16X16.getImage()));

        return treeItemFzgProjekt;
    }

    default void deleteUserData(TreeItem<VehicleConfigTreeObj> root) {
    }

    default VehicleConfigTreeObj createVehicleConfigTreeObj(String value, VehicleConfigTreeObjType type) {
        VehicleConfigTreeObj treeObject = new VehicleConfigTreeObj();
        treeObject.setValue(value);
        treeObject.setVehicleConfigTreeObjType(type);

        return treeObject;
    }
}
