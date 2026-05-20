package de.vw.paso.service.vehicle;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.logic.vehicle.VehicleConfigManager;
import de.vw.paso.mapper.VehicleConfigCategoryStatusMapper;
import de.vw.paso.mapper.VehicleConfigMapper;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(VehicleConfigRestService.URL)
public class VehicleConfigRestController implements VehicleConfigRestService {

    private final VehicleConfigManager vehicleConfigManager;

    public VehicleConfigRestController(VehicleConfigManager vehicleConfigManager) {
        this.vehicleConfigManager = vehicleConfigManager;
    }

    @Override
    @GetMapping(path = GET_NON_DELETED_CONFIGS)
    @Transactional
    public VehicleConfigListDTO loadNonDeletedVehicleConfigs() {
        return new VehicleConfigListDTO(vehicleConfigManager.loadNonDeletedVehicleConfigs());
    }

    @Override
    @GetMapping(path = GET_DELETED_CONFIGS)
    public VehicleConfigListDTO loadDeletedVehicleConfigs() {
        return new VehicleConfigListDTO(vehicleConfigManager.loadDeletedVehicleConfigs());
    }

    @Override
    @GetMapping(path = GET_BY_CONFIG_ID + "{vehicleConfigId}")
    @Transactional
    public VehicleConfigDTO loadFzgKonfig(@PathVariable Long vehicleConfigId) {
        return vehicleConfigManager.loadVehicleConfigWithAccess(vehicleConfigId);
    }

    @Override
    @GetMapping(path = GET_BY_CONFIG_IDS)
    @Transactional
    public VehicleConfigListDTO loadVehicleConfigs(@RequestParam List<String> vehicleConfigIds) {
        Set<Long> configIds = vehicleConfigIds.stream().map(Long::parseLong).collect(Collectors.toSet());
        return new VehicleConfigListDTO(vehicleConfigManager.loadVehicleConfigsWithAccess(configIds));
    }

    @Override
    @GetMapping(path = GET_BY_PROJECT_ID + "{vehicleProjectId}")
    @Transactional
    public VehicleConfigListDTO loadVehicleConfigByProjectId(@PathVariable long vehicleProjectId) {
        return new VehicleConfigListDTO(vehicleConfigManager.loadVehicleConfigByProjectIdWithAccess(vehicleProjectId));
    }

    @Override
    @GetMapping(path = GET_BY_PROJECT_IDS)
    @Transactional
    public VehicleConfigListDTO loadVehicleConfigByProjectIds(@RequestParam List<String> vehicleProjectIds) {
        Set<Long> projectIDs = vehicleProjectIds.stream().map(Long::parseLong).collect(Collectors.toSet());
        return new VehicleConfigListDTO(vehicleConfigManager.loadVehicleConfigByProjectIdsWithAccess(projectIDs));
    }

    @Override
    @GetMapping(value = { GET_BY_PRODUCT_KEY + "{productKey}", GET_BY_PRODUCT_KEY })
    @Transactional
    public VehicleConfigListDTO loadVehicleConfigByProductKey(@PathVariable(required = false) String productKey) {
        return new VehicleConfigListDTO(vehicleConfigManager.loadVehicleConfigByProductKeyWithAccess(productKey));
    }

    @Override
    @GetMapping(path = GET_BY_BRAND + "{brand}")
    @Transactional
    public VehicleConfigListDTO loadVehicleConfigByBrand(@PathVariable String brand) {
        return new VehicleConfigListDTO(vehicleConfigManager.loadVehicleConfigByBrandWithAccess(Brand.valueOf(brand)));
    }

    @Override
    @GetMapping(path = GET_RECENTLY_USED)
    @Transactional
    public VehicleConfigListDTO loadVehicleConfigByRecentlyUsed() {
        return new VehicleConfigListDTO(vehicleConfigManager.loadVehicleConfigByRecentlyUsedWithAccess());
    }

    @Override
    @PostMapping
    @Transactional
    public VehicleConfigDTO saveFzgKonfig(@RequestBody VehicleConfigDTO vehicleConfig) {
        VehicleConfig savedVehicleConfig = vehicleConfigManager.saveVehicleConfig(
                VehicleConfigMapper.toEntity(vehicleConfig), vehicleConfig.isUpdateDefaultSetVersion());
        return VehicleConfigMapper.toDto(savedVehicleConfig, false);
    }

    @Override
    @PostMapping(RESET_DELETION)
    public VehicleConfigDTO resetDeletion(@RequestBody Long id) {
        return vehicleConfigManager.resetDeletion(id);
    }

    @Override
    @DeleteMapping(path = DELETE_VEHICLE_CONFIG + "{vehicleConfigId}")
    @Transactional
    public void deleteVehicleConfig(@PathVariable Long vehicleConfigId) {
        vehicleConfigManager.deleteVehicleConfig(vehicleConfigId);
    }

    @Override
    @GetMapping(path = GET_VEHICLE_CONFIG_STATUS)
    @Transactional
    public VehicleConfigCategoryStatusDTO loadVehicleConfigCategoryStatus(@RequestParam Long vehicleConfigId,
            @RequestParam VehicleConfigCategory vehicleConfigCategory) {
        VehicleConfigCategoryStatus vehicleConfigCategoryStatus = vehicleConfigManager.loadVehicleConfigCategoryStatus(
                vehicleConfigId, vehicleConfigCategory);
        return VehicleConfigCategoryStatusMapper.toDto(vehicleConfigCategoryStatus);
    }

    @Override
    @PostMapping(path = CREATE_PART_LIST + "{vehicleConfigId}")
    @Transactional
    public VehicleConfigDTO createVehiclePartList(@PathVariable long vehicleConfigId) {
        VehicleConfig vehicleConfig = vehicleConfigManager.createVehiclePartList(vehicleConfigId);
        return VehicleConfigMapper.toDto(vehicleConfig, true);
    }

    @Override
    @GetMapping(path = GET_CONFIG_COUNT_FOR_VEHICLE_PROJECT)
    public ConfigCountForVehicleProjectDTO loadConfigurationCountForVehicleProject() {
        return new ConfigCountForVehicleProjectDTO(vehicleConfigManager.loadConfigurationCountForVehicleProject());
    }
}
