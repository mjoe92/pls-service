package de.vw.paso.client.explorer.vehicleconfig.tree;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObjType;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.client.util.UserProperties;
import de.vw.paso.client.util.icon.ExplorerIcon;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class FavoritesFzgConfigTreeConfiguration implements FzgKonfigTreeConfiguration {

    @Override
    public TreeItem<VehicleConfigTreeObj> createTree(Collection<VehicleConfigDTO> vehicleConfigs,
            Collection<VehicleConfigDTO> deletedConfigs, Collection<VehicleProjectDTO> vehicles,
            Map<Long, Long> configCountMap, PasoWildCardPattern patternSearchTerm, List<Long> favoriteIds) {

        TreeItem<VehicleConfigTreeObj> treeItemRoot = new TreeItem<>();

        TreeItem<VehicleConfigTreeObj> favouriteTreeItem = createFavouriteTreeItems(vehicles, configCountMap,
                patternSearchTerm, favoriteIds);
        treeItemRoot.getChildren().add(favouriteTreeItem);

        TreeItem<VehicleConfigTreeObj> recentlyUsedTreeItem = createRecentlyUsedTreeItems(vehicleConfigs);
        treeItemRoot.getChildren().add(recentlyUsedTreeItem);

        if (UserProperties.getUser().isAdmin()) {
            VehicleConfigTreeObj treeObject = createVehicleConfigTreeObj(
                    I18N.getString("child.tab.my-configurations.deleted"), VehicleConfigTreeObjType.DELETED);
            TreeItem<VehicleConfigTreeObj> treeItemDeleted = new TreeItem<>(treeObject);
            addDeletedToTreeItem(treeItemDeleted, deletedConfigs);
            treeItemDeleted.setExpanded(true);

            treeItemRoot.getChildren().add(treeItemDeleted);
        }

        return treeItemRoot;
    }

    @Override
    public TreeCell<VehicleConfigTreeObj> createCell() {
        return new FavoritesFzgConfigTreeCell<>();
    }

    @Override
    public String getTitle() {
        return I18N.getString("tab.favorites.title");
    }

    @Override
    public void deleteUserData(TreeItem<VehicleConfigTreeObj> root) {
        for (TreeItem<VehicleConfigTreeObj> child : root.getChildren()) {
            if (child.getValue().isFavoritesGroup() || child.getValue().isRecentlyUsedGroup()) {
                child.getChildren().clear();
            }

            deleteUserData(child);
        }
    }

    private TreeItem<VehicleConfigTreeObj> createFavouriteTreeItems(Collection<VehicleProjectDTO> vehicleProjects,
            Map<Long, Long> configCountMap, PasoWildCardPattern patternSearchTerm, List<Long> favoriteIds) {
        VehicleConfigTreeObj favouriteTreeObject = createVehicleConfigTreeObj(
                I18N.getString("child.tab.my-configurations.favorites"), VehicleConfigTreeObjType.FAVORITES);
        TreeItem<VehicleConfigTreeObj> favouriteTreeItem = new TreeItem<>(favouriteTreeObject);
        favouriteTreeItem.setExpanded(true);

        for (VehicleProjectDTO vehicleProject : vehicleProjects) {
            TreeItem<VehicleConfigTreeObj> treeItemFzgProject = createTreeItemFzgProjekt(vehicleProject, configCountMap,
                    favoriteIds.contains(vehicleProject.getId()));

            boolean hasChildren = !treeItemFzgProject.getChildren().isEmpty();
            boolean matchesActive = matchFzgProjekt(vehicleProject, patternSearchTerm) && !vehicleProject.isArchive();
            boolean isFavorite = favoriteIds.contains(treeItemFzgProject.getValue().getVehicleProject().getId());

            if ((hasChildren || matchesActive) && isFavorite) {
                favouriteTreeItem.getChildren().add(treeItemFzgProject);
            }
        }

        return favouriteTreeItem;
    }

    private TreeItem<VehicleConfigTreeObj> createRecentlyUsedTreeItems(Collection<VehicleConfigDTO> vehicleConfigs) {
        VehicleConfigTreeObj recentlyUsedTreeObject = createVehicleConfigTreeObj(
                I18N.getString("child.tab.my-configurations.recently-used"), VehicleConfigTreeObjType.RECENTLY_USED);
        TreeItem<VehicleConfigTreeObj> recentlyUsedTreeItem = new TreeItem<>(recentlyUsedTreeObject);
        if (vehicleConfigs == null) {
            return recentlyUsedTreeItem;
        }

        recentlyUsedTreeItem.setExpanded(true);
        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            if (vehicleConfig != null && vehicleConfig.getDeletionDate() == null) {
                TreeItem<VehicleConfigTreeObj> treeItemFzgProject = createTreeItemFzgProjekt(vehicleConfig);
                recentlyUsedTreeItem.getChildren().add(treeItemFzgProject);
            }
        }

        return recentlyUsedTreeItem;
    }

    private void addDeletedToTreeItem(TreeItem<VehicleConfigTreeObj> treeItemRecentlyUsed,
            Collection<VehicleConfigDTO> vehicleConfigs) {
        if (vehicleConfigs == null) {
            return;
        }

        for (VehicleConfigDTO vehicleConfig : vehicleConfigs) {
            if (vehicleConfig != null && vehicleConfig.getDeletionDate() != null) {
                TreeItem<VehicleConfigTreeObj> treeItemFzgProject = createTreeItemFzgProjekt(vehicleConfig);
                treeItemRecentlyUsed.getChildren().add(treeItemFzgProject);
            }
        }
    }

    private TreeItem<VehicleConfigTreeObj> createTreeItemFzgProjekt(VehicleConfigDTO vehicleConfig) {
        VehicleConfigTreeObj recentlyUsedTreeObject = new VehicleConfigTreeObj();
        recentlyUsedTreeObject.setVehicleConfig(vehicleConfig);
        recentlyUsedTreeObject.setVehicleConfigTreeObjType(VehicleConfigTreeObjType.RECENTLY_USED);
        TreeItem<VehicleConfigTreeObj> treeItemFzgProjekt = new TreeItem<>(recentlyUsedTreeObject);

        treeItemFzgProjekt.setExpanded(true);
        treeItemFzgProjekt.setGraphic(new ImageView(ExplorerIcon.EXPLORER_FZGPROJEKT_16X16.getImage()));

        return treeItemFzgProjekt;
    }
}
