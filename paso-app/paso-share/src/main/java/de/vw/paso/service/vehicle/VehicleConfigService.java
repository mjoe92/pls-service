package de.vw.paso.service.vehicle;

import java.util.List;
import java.util.Map;

import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.PasoService;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;

public interface VehicleConfigService extends PasoService {

    String URL = "/api/paso-service/VehicleConfigService";

    List<VehicleConfig> loadVehicleConfigs();

    List<VehicleConfig> loadVehicleConfigs(List<Long> vehicleConfigIds);

    List<VehicleConfig> loadVehicleConfigByProjectId(long vehicleProjectId);

    List<VehicleConfig> loadVehicleConfigByProjectIds(List<Long> vehicleProjectIds);

    List<VehicleConfig> loadVehicleConfigByProductKey(String productKey);

    List<VehicleConfig> loadVehicleConfigByBrand(Brand brand);

    List<VehicleConfig> loadVehicleConfigByRecentlyUsed();

    VehicleConfig loadFzgKonfig(Long vehicleConfigId);

    VehicleConfig saveFzgKonfig(VehicleConfig vehicleConfig);

    VehicleConfig deleteFzgKonfig(VehicleConfig vehicleConfig);

    VehicleConfigCategoryStatus loadVehicleConfigCategoryStatus(Long vehicleConfigId,
            VehicleConfigCategory vehicleConfigCategory);

    VehicleConfig createVehiclePartList(long vehicleConfigId);

    Map<Long, Long> loadConfigurationCountForVehicleProject();

    List<VehicleConfig> loadNonDeletedVehicleConfigs();

}
