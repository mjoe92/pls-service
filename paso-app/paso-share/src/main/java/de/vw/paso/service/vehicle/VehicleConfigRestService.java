package de.vw.paso.service.vehicle;

import java.util.List;

import de.vw.paso.vehicle.VehicleConfigCategory;

public interface VehicleConfigRestService {

    String URL = "/api/vehicle-config";
    String GET_NON_DELETED_CONFIGS = "/non-deleted-configs";
    String GET_DELETED_CONFIGS = "/deleted-configs";
    String GET_BY_CONFIG_IDS = "/config-ids";
    String GET_BY_CONFIG_ID = "/config-id/";
    String GET_BY_PROJECT_ID = "/project-id/";
    String GET_BY_PROJECT_IDS = "/project-ids";
    String DELETE_VEHICLE_CONFIG = "/vehicle-config-id/";
    String GET_BY_PRODUCT_KEY = "/product-key/";
    String GET_BY_BRAND = "/brand/";
    String GET_RECENTLY_USED = "/recently-used";
    String GET_VEHICLE_CONFIG_STATUS = "/vehicle-config-status";
    String GET_CONFIG_COUNT_FOR_VEHICLE_PROJECT = "/config-count";
    String CREATE_PART_LIST = "/part-list/";
    String RESET_DELETION = "/reset-deletion";

    VehicleConfigListDTO loadNonDeletedVehicleConfigs();

    VehicleConfigListDTO loadDeletedVehicleConfigs();

    VehicleConfigDTO loadFzgKonfig(Long vehicleConfigId);

    VehicleConfigListDTO loadVehicleConfigs(List<String> vehicleConfigIds);

    VehicleConfigListDTO loadVehicleConfigByProjectId(long vehicleProjectId);

    VehicleConfigListDTO loadVehicleConfigByProjectIds(List<String> vehicleProjectIds);

    VehicleConfigListDTO loadVehicleConfigByProductKey(String productKey);

    VehicleConfigListDTO loadVehicleConfigByBrand(String brandName);

    VehicleConfigListDTO loadVehicleConfigByRecentlyUsed();

    VehicleConfigDTO saveFzgKonfig(VehicleConfigDTO vehicleConfig);

    VehicleConfigDTO resetDeletion(Long id);

    void deleteVehicleConfig(Long vehicleConfigId);

    VehicleConfigCategoryStatusDTO loadVehicleConfigCategoryStatus(Long vehicleConfigId,
            VehicleConfigCategory vehicleConfigCategory);

    VehicleConfigDTO createVehiclePartList(long vehicleConfigId);

    ConfigCountForVehicleProjectDTO loadConfigurationCountForVehicleProject();
}
