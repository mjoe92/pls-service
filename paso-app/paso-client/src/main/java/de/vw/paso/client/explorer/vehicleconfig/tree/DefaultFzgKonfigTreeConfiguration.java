package de.vw.paso.client.explorer.vehicleconfig.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.explorer.vehicleconfig.VehicleConfigTreeObj;
import de.vw.paso.client.util.PasoWildCardPattern;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class DefaultFzgKonfigTreeConfiguration implements FzgKonfigTreeConfiguration {

    @Override
    public TreeItem<VehicleConfigTreeObj> createTree(Collection<VehicleConfigDTO> vehicleConfigs,
        Collection<VehicleConfigDTO> deletedConfigs, Collection<VehicleProjectDTO> vehicles,
        Map<Long, Long> configCountMap, PasoWildCardPattern patternSearchTerm, List<Long> favoriteIds) {
        ImmutableListMultimap<Brand, VehicleProjectDTO> brandToVehicleProjectsMap = Multimaps.index(vehicles,
            VehicleProjectDTO::getBrandCode);

        TreeItem<VehicleConfigTreeObj> treeItemRoot = new TreeItem<>();
        for (Brand brand : Brand.values()) {
            Collection<VehicleProjectDTO> vehicleProjects = new ArrayList<>(brandToVehicleProjectsMap.get(brand));
            if (vehicleProjects.isEmpty() || checkForEmptyOrFullyArchivedBrands(vehicleProjects)) {
                continue;
            }

            TreeItem<VehicleConfigTreeObj> treeItemMarke = createTreeItemBrand(brand);
            for (VehicleProjectDTO vehicleProject : vehicleProjects) {
                addTreeItemFzgProjekt(configCountMap, patternSearchTerm, favoriteIds, treeItemMarke, vehicleProject);
            }

            if (!treeItemMarke.getChildren().isEmpty() || matchMarke(brand, patternSearchTerm)) {
                treeItemRoot.getChildren().add(treeItemMarke);
            }
        }

        return treeItemRoot;
    }

    @Override
    public TreeCell<VehicleConfigTreeObj> createCell() {
        return new DefaultFzgConfigTreeCell<>();
    }

    @Override
    public String getTitle() {
        return I18N.getString("tab.brand.title");
    }

    private boolean checkForEmptyOrFullyArchivedBrands(Collection<VehicleProjectDTO> vehicleProjects) {
        Collection<VehicleProjectDTO> archived = vehicleProjects.stream().filter(VehicleProjectDTO::isArchive).toList();
        return (vehicleProjects.size() - archived.size()) == 0;
    }

    private TreeItem<VehicleConfigTreeObj> createTreeItemBrand(Brand brand) {
        VehicleConfigTreeObj brandObject = new VehicleConfigTreeObj();
        brandObject.setBrand(brand);

        return new TreeItem<>(brandObject);
    }

    private void addTreeItemFzgProjekt(Map<Long, Long> configCountMap, PasoWildCardPattern patternSearchTerm,
        List<Long> favoriteIds, TreeItem<VehicleConfigTreeObj> treeItemMarke, VehicleProjectDTO vehicleProject) {
        TreeItem<VehicleConfigTreeObj> treeItemFzgProjekt = createTreeItemFzgProjekt(vehicleProject, configCountMap,
            favoriteIds.contains(vehicleProject.getId()));
        if (!treeItemFzgProjekt.getChildren().isEmpty()
            || matchFzgProjekt(vehicleProject, patternSearchTerm) && !vehicleProject.isArchive()) {
            treeItemMarke.getChildren().add(treeItemFzgProjekt);
        }
    }

    private boolean matchMarke(Brand brand, PasoWildCardPattern patternSearchTerm) {
        return patternSearchTerm == null || patternSearchTerm.matches(brand.name()) != null;
    }
}
