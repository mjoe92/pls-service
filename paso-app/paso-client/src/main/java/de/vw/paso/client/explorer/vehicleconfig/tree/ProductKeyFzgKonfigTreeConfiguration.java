package de.vw.paso.client.explorer.vehicleconfig.tree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObjType;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class ProductKeyFzgKonfigTreeConfiguration implements FzgKonfigTreeConfiguration {

    @Override
    public TreeItem<VehicleConfigTreeObj> createTree(Collection<VehicleConfigDTO> vehicleConfigs,
        Collection<VehicleConfigDTO> deletedConfigs, Collection<VehicleProjectDTO> vehicles,
        Map<Long, Long> configCountMap, PasoWildCardPattern patternSearchTerm, List<Long> favoriteIds) {

        Map<String, TreeItem<VehicleConfigTreeObj>> productKeyToTreeItemMap = new HashMap<>(vehicles.size() / 4);
        for (VehicleProjectDTO vehicleProject : vehicles) {
            TreeItem<VehicleConfigTreeObj> productIdTreeItem = productKeyToTreeItemMap.get(
                vehicleProject.getProductKey());

            if (productIdTreeItem == null) {
                VehicleConfigTreeObj vehicleConfigObject = createVehicleConfigTreeObj(vehicleProject.getProductKey(),
                    VehicleConfigTreeObjType.PRODUCT_KEY);
                productIdTreeItem = new TreeItem<>(vehicleConfigObject);
                productKeyToTreeItemMap.put(vehicleProject.getProductKey(), productIdTreeItem);
            }

            TreeItem<VehicleConfigTreeObj> treeItemFzgProjekt = createTreeItemFzgProjekt(vehicleProject, configCountMap,
                favoriteIds.contains(vehicleProject.getId()));

            if (!treeItemFzgProjekt.getChildren().isEmpty()
                || matchFzgProjekt(vehicleProject, patternSearchTerm) && !vehicleProject.isArchive()) {
                productIdTreeItem.getChildren().add(treeItemFzgProjekt);
            }
        }

        TreeItem<VehicleConfigTreeObj> treeItemRoot = new TreeItem<>();
        Collection<TreeItem<VehicleConfigTreeObj>> productKeyItems = productKeyToTreeItemMap.values().stream()
            .sorted(Comparator.comparing(item -> item.getValue().getProductKey())).toList();
        for (TreeItem<VehicleConfigTreeObj> productKeyItem : productKeyItems) {
            if (!productKeyItem.getChildren().isEmpty()) {
                treeItemRoot.getChildren().add(productKeyItem);
            }
        }

        return treeItemRoot;
    }

    @Override
    public TreeCell<VehicleConfigTreeObj> createCell() {
        return new ProductKeyFzgConfigTreeCell<>();
    }

    @Override
    public String getTitle() {
        return I18N.getString("tab.productid.title");
    }
}
