package de.vw.paso.logic.vehicle;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.vw.paso.exception.DataNotFoundException;
import de.vw.paso.logic.partlist.EfsWeightManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.mapper.VehicleConfigMapper;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.PartListFactory;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.pls.Status;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.repository.masterdata.SalesRegionRepository;
import de.vw.paso.repository.masterdata.VehicleConfigPrNumberMappingRepository;
import de.vw.paso.repository.masterdata.VehicleProjectRepository;
import de.vw.paso.repository.message.UserMessageRepository;
import de.vw.paso.repository.modelimport.ModelImportRepository;
import de.vw.paso.repository.modelimport.ModelRepository;
import de.vw.paso.repository.partlist.CostGroupRepository;
import de.vw.paso.repository.partlist.EfsElementAggregateMappingRepository;
import de.vw.paso.repository.partlist.EfsElementHistoryRepository;
import de.vw.paso.repository.partlist.EfsElementMaraHistoryRepository;
import de.vw.paso.repository.partlist.EfsElementMaraRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.FilteredOutEfsElementRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.tiwhimport.TiWhImportRepository;
import de.vw.paso.repository.vehicle.ResourceRepository;
import de.vw.paso.repository.vehicle.VehicleConfigCategoryStatusRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.repository.vehicle.VehicleProjectConfigurationCountDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.ResourceType;
import de.vw.paso.user.domain.Resource;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.util.UnauthorizedException;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;
import de.vw.paso.vehicle.domain.VehicleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleConfigManager.class);

    private static final int DELETE_VEHICLE_CONFIGURATION_EXPIRY_DAYS = 30;
    private static final int DELETE_VEHICLE_CONFIGURATION_EXPIRY_YEARS = 4;
    private static final int DELETE_UNFINISHED_VEHICLE_CONFIGURATION_EXPIRY_DAYS = 30;

    private final CostGroupRepository costGroupRepository;
    private final ModelRepository modelRepository;
    private final ModelImportRepository modelImportRepository;
    private final ResourceRepository resourceRepository;
    private final SalesRegionRepository salesRegionRepository;
    private final TiWhImportRepository tiWhImportRepository;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final VehicleConfigCategoryStatusRepository vehicleConfigCategoryStatusRepository;
    private final VehiclePartListRepository vehiclePartListRepository;
    private final VehicleProjectRepository vehicleProjectRepository;
    private final UserMessageRepository userMessageRepository;
    private final EfsElementRepository efsElementRepository;

    private final UserManager userManager;
    private final UserPropertyManager userPropertyManager;
    private final ProductRepository productRepository;
    private final FilteredOutEfsElementRepository filteredOutEfsElementRepository;
    private final EfsElementMaraRepository efsElementMaraRepository;
    private final EfsElementMaraHistoryRepository efsElementMaraHistoryRepository;
    private final EfsElementAggregateMappingRepository efsElementAggregateMappingRepository;
    private final EfsWeightManager efsWeightManager;
    private final EfsElementHistoryRepository efsElementHistoryRepository;
    private final VehicleConfigPrNumberMappingRepository vehicleConfigPrNumberMappingRepository;

    public VehicleConfigManager(CostGroupRepository costGroupRepository, ModelRepository modelRepository,
            ModelImportRepository modelImportRepository, ResourceRepository resourceRepository,
            SalesRegionRepository salesRegionRepository, TiWhImportRepository tiWhImportRepository,
            VehicleConfigRepository vehicleConfigRepository,
            VehicleConfigCategoryStatusRepository vehicleConfigCategoryStatusRepository,
            VehiclePartListRepository vehiclePartListRepository, VehicleProjectRepository vehicleProjectRepository,
            UserMessageRepository userMessageRepository, EfsElementRepository efsElementRepository,
            UserManager userManager, UserPropertyManager userPropertyManager, ProductRepository productRepository,
            FilteredOutEfsElementRepository filteredOutEfsElementRepository,
            EfsElementMaraRepository efsElementMaraRepository,
            EfsElementMaraHistoryRepository efsElementMaraHistoryRepository,
            EfsElementAggregateMappingRepository efsElementAggregateMappingRepository,
            EfsWeightManager efsWeightManager, EfsElementHistoryRepository efsElementHistoryRepository,
            VehicleConfigPrNumberMappingRepository vehicleConfigPrNumberMappingRepository) {
        this.costGroupRepository = costGroupRepository;
        this.modelRepository = modelRepository;
        this.modelImportRepository = modelImportRepository;
        this.resourceRepository = resourceRepository;
        this.salesRegionRepository = salesRegionRepository;
        this.tiWhImportRepository = tiWhImportRepository;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.vehicleConfigCategoryStatusRepository = vehicleConfigCategoryStatusRepository;
        this.vehiclePartListRepository = vehiclePartListRepository;
        this.vehicleProjectRepository = vehicleProjectRepository;
        this.userMessageRepository = userMessageRepository;
        this.efsElementRepository = efsElementRepository;
        this.userManager = userManager;
        this.userPropertyManager = userPropertyManager;
        this.productRepository = productRepository;
        this.filteredOutEfsElementRepository = filteredOutEfsElementRepository;
        this.efsElementMaraRepository = efsElementMaraRepository;
        this.efsElementMaraHistoryRepository = efsElementMaraHistoryRepository;
        this.efsElementAggregateMappingRepository = efsElementAggregateMappingRepository;
        this.efsWeightManager = efsWeightManager;
        this.efsElementHistoryRepository = efsElementHistoryRepository;
        this.vehicleConfigPrNumberMappingRepository = vehicleConfigPrNumberMappingRepository;
    }

    public VehicleConfigDTO loadVehicleConfigWithAccess(Long vehicleConfigId) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();
        if (vehicleConfigIdsWithAccess.containsKey(vehicleConfigId)) {
            return map(vehicleConfigRepository.findById(vehicleConfigId).orElseThrow(),
                    vehicleConfigIdsWithAccess.get(vehicleConfigId));
        }

        throw new RuntimeException("You do not have access to this Vehicle Configuration.");

    }

    public List<VehicleConfigDTO> loadNonDeletedVehicleConfigs() {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();
        return vehicleConfigRepository.findAllByDeletionDate(null).stream()
                .map(config -> map(config, vehicleConfigIdsWithAccess.get(config.getId()))).toList();
    }

    @Transactional
    public List<VehicleConfigDTO> loadDeletedVehicleConfigs() {
        User user = userManager.getCurrentUser();
        if (!user.isAdmin()) {
            throw new UnauthorizedException("User has to be an admin in order to access deleted vehicle configs");
        }

        Collection<Long> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess().keySet();
        return vehicleConfigRepository.findByIdInAndDeletionDateNotNull(vehicleConfigIdsWithAccess).stream()
                .map(config -> map(config, isConfigEditable(user, config))).toList();
    }

    public List<VehicleConfigDTO> loadVehicleConfigsWithAccess(Collection<Long> vehicleConfigIds) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();
        Collection<Long> vehicleConfigIdsSet = vehicleConfigIdsWithAccess.keySet();
        Collection<Long> configIdsWithAccess = vehicleConfigIds.stream().filter(vehicleConfigIdsSet::contains)
                .collect(Collectors.toCollection(ArrayList::new));

        return filterDeletedVehicleConfigs(vehicleConfigRepository.findByIdIn(configIdsWithAccess),
                vehicleConfigIdsWithAccess);
    }

    public List<VehicleConfigDTO> loadVehicleConfigByProjectIdWithAccess(long vehicleProjectId) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();

        return filterDeletedVehicleConfigs(
                vehicleConfigRepository.findByIdInAndVehicleProjectId(vehicleConfigIdsWithAccess.keySet(),
                        vehicleProjectId), vehicleConfigIdsWithAccess);
    }

    public List<VehicleConfigDTO> loadVehicleConfigByProjectIdsWithAccess(Collection<Long> vehicleProjectIds) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();
        Collection<Long> accessibleVehicleConfigIds = vehicleConfigIdsWithAccess.keySet();
        Collection<VehicleConfig> filteredVehicleConfigs = vehicleConfigRepository.findByVehicleProjectIdInAndIdIn(
                        vehicleProjectIds, accessibleVehicleConfigIds).stream()
                .filter(config -> accessibleVehicleConfigIds.contains(config.getId())).toList();

        return filterDeletedVehicleConfigs(filteredVehicleConfigs, vehicleConfigIdsWithAccess);
    }

    public List<VehicleConfigDTO> loadVehicleConfigByProductKeyWithAccess(String productKey) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();

        return filterDeletedVehicleConfigs(
                vehicleConfigRepository.findByVehicleProductKeyAndIds(productKey, vehicleConfigIdsWithAccess.keySet()),
                vehicleConfigIdsWithAccess);
    }

    public List<VehicleConfigDTO> loadVehicleConfigByBrandWithAccess(Brand brand) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();

        return filterDeletedVehicleConfigs(
                vehicleConfigRepository.findAllByBrandAndIds(brand, vehicleConfigIdsWithAccess.keySet()),
                vehicleConfigIdsWithAccess);
    }

    public List<VehicleConfigDTO> loadVehicleConfigByRecentlyUsedWithAccess() {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();
        Collection<Long> keySet = vehicleConfigIdsWithAccess.keySet();
        Collection<Long> recentlyUsedIds = userPropertyManager.getRecentlyUsedVehicleProjectIds(
                userManager.getCurrentUser());
        Collection<Long> recentlyUsedIdsWithAccess = recentlyUsedIds.stream().filter(keySet::contains)
                .collect(Collectors.toCollection(ArrayList::new));

        return filterDeletedVehicleConfigs(vehicleConfigRepository.findByIdIn(recentlyUsedIdsWithAccess),
                vehicleConfigIdsWithAccess);
    }

    @Transactional
    public VehicleConfig saveVehicleConfig(VehicleConfig vehicleConfig, boolean updateDefaultSetVersion) {
        VehicleConfig persistentVehicleConfig = null;

        if (vehicleConfig.getId() != null) {
            // if vehicleConfig has id, then we check if the user has the rights to update it or not
            Long vehicleConfigId = vehicleConfig.getId();
            Map<Long, Boolean> vehicleConfigIdsWithWriteAccess = userManager.getVehicleConfigIdsWithWriteAccess();

            if (!vehicleConfigIdsWithWriteAccess.getOrDefault(vehicleConfigId,
                    false)/* && !userId.equals("Test-User-ID")*/) {
                throw new UnauthorizedException(
                        "Current user doesn't have the right to edit vehicle config (id: " + vehicleConfigId + ")");
            }

            persistentVehicleConfig = vehicleConfigRepository.findById(vehicleConfig.getId()).orElse(null);
        }

        if (persistentVehicleConfig == null) {
            persistentVehicleConfig = createNewVehicleConfigInDB(vehicleConfig);
        }

        updateVehicleConfigName(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigDescription(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigValidDate(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigPrNumberString(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigModelYear(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigPlsProductDataId(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigPlsDataId(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigPlsDataLockId(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigDeletionDate(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigSmartFix(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigSetVersionId(vehicleConfig, persistentVehicleConfig);

        updateVehicleConfigOwnerGroup(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigProject(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigSalesRegion(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigStatus(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigResource(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigModel(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigModelImport(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigTiWhImportVehicle(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigTiWhImportGearBox(vehicleConfig, persistentVehicleConfig);

        setVehicleConfigTiWhImportMotor(vehicleConfig, persistentVehicleConfig);

        updateMotorAndEngine(vehicleConfig, persistentVehicleConfig);

        if (updateDefaultSetVersion) {
            Long setVersionId = persistentVehicleConfig.getSetVersionId();
            String productKey = persistentVehicleConfig.getVehicleProject().getProductKey();
            Product productToUpdate = productRepository.findOneByProductKey(productKey);

            if (!Objects.equals(setVersionId, productToUpdate.getSetVersionId())) {
                productToUpdate.setSetVersionId(setVersionId);
                productRepository.save(productToUpdate);
            }
        }

        return persistentVehicleConfig;
    }

    @Transactional
    public void deleteVehicleConfig(Long vehicleConfigId) {
        Map<Long, Boolean> vehicleConfigIdsWithAccess = userManager.getVehicleConfigIdsWithAccess();

        if (!vehicleConfigIdsWithAccess.getOrDefault(vehicleConfigId, false)) {
            throw new UnauthorizedException(
                    "Current user doesn't have the right to delete vehicle config (id: " + vehicleConfigId
                            + StringConstant.RIGHT_PARENTHESIS);
        }

        VehicleConfig config = vehicleConfigRepository.findById(vehicleConfigId).orElseThrow();

        if (Status.INCOMPLETE == config.getStatus()) {
            deleteVehicleConfigsPermanently(List.of(config));
        } else {
            Long deletedEntries = userPropertyManager.delete(userManager.getCurrentUser(), PropertyType.RECENTLY_USED,
                    vehicleConfigId);
            if (deletedEntries > 0) {
                LOG.info("Deleted vehicle config with id: {} from recently used", vehicleConfigId);
            }
            config.setDeletionDate(new Date());
            vehicleConfigRepository.save(config);
        }
    }

    @Transactional
    public VehicleConfigDTO resetDeletion(Long vehicleConfigId) {
        VehicleConfig config = vehicleConfigRepository.findById(vehicleConfigId).orElseThrow();

        User user = userManager.getCurrentUser();
        boolean hasAccess = user.getUserGroups().stream().anyMatch(
                userGroup -> config.getOwnerGroup().getId().equals(userGroup.getId()) || config.getUserGroups().stream()
                        .anyMatch(userGroup1 -> userGroup1.getId().equals(userGroup.getId())));

        if (!user.isAdmin() || !hasAccess) {
            throw new UnauthorizedException("User is not an admin or doesn't have access to this vehicle config");
        }

        if (config.getDeletionDate() == null) {
            return map(config, isConfigEditable(user, config));
        }

        config.setDeletionDate(null);
        VehicleConfig savedConfig = vehicleConfigRepository.save(config);
        return map(savedConfig, isConfigEditable(user, savedConfig));
    }

    @Transactional
    public void deleteExpiredDeletedVehicleConfigurations() {
        Date date = java.sql.Date.valueOf(LocalDate.now().minusDays(DELETE_VEHICLE_CONFIGURATION_EXPIRY_DAYS));
        Collection<VehicleConfig> toDelete = vehicleConfigRepository.findByDeletionDateBefore(date);
        vehicleConfigRepository.flush();

        deleteVehicleConfigsPermanently(toDelete);
    }

    public VehicleConfigCategoryStatus loadVehicleConfigCategoryStatus(Long vehicleConfigId,
            VehicleConfigCategory vehicleConfigCategory) {
        return vehicleConfigCategoryStatusRepository.findOneByVehicleConfigAndIdVehicleConfigCategory(
                vehicleConfigRepository.getReferenceById(vehicleConfigId), vehicleConfigCategory);
    }

    @Transactional
    public VehicleConfig createVehiclePartList(long vehicleConfigId) {
        VehicleConfig vehicleConfig = vehicleConfigRepository.findById(vehicleConfigId).orElseThrow(
                () -> new DataNotFoundException("Could not load vehicle config with id " + vehicleConfigId));

        VehicleConfigDTO vehicleConfigDTO = map(vehicleConfig, false);

        VehiclePartListDTO vehiclePartListDTO = PartListFactory.createVehiclePartList(vehicleConfigDTO);

        VehiclePartList vehiclePartList = VehiclePartListMapper.toEntity(vehiclePartListDTO, vehicleConfig);
        vehicleConfig.setVehiclePartList(vehiclePartList);

        vehiclePartList.setChange(userManager.getCurrentUserId());

        if (vehicleConfig.getTiWhImportVehicle() != null) {
            vehiclePartList.setProductKeyVehicle(vehicleConfig.getTiWhImportVehicle().getProductKey());
        }

        vehiclePartList = vehiclePartListRepository.save(vehiclePartList);

        if (vehiclePartList.getWeight() == 0.0D) {
            Long vehiclePartListId = vehiclePartList.getId();
            Double weight = efsWeightManager.calculateWeight(vehiclePartListId).get(Long.MIN_VALUE);
            LOG.debug("part list weight for part list id {} is (VCM) : {}", vehiclePartListId, weight);
            vehiclePartListRepository.updateWeight(vehiclePartListId, weight);
        }

        return vehicleConfigRepository.save(vehicleConfig);
    }

    public Map<Long, Long> loadConfigurationCountForVehicleProject() {
        Collection<VehicleProjectConfigurationCountDTO> counts = vehicleConfigRepository.getConfigurationCountForVehicleProjects();

        return counts.stream().collect(
                Collectors.toMap(VehicleProjectConfigurationCountDTO::id, VehicleProjectConfigurationCountDTO::count,
                        (first, second) -> second));
    }

    @Transactional
    public void deleteUnfinishedVehicleConfigs() {
        LocalDate now = LocalDate.now();
        Instant instant = now.minusDays(DELETE_UNFINISHED_VEHICLE_CONFIGURATION_EXPIRY_DAYS)
                .atStartOfDay(ZoneId.systemDefault()).toInstant();
        Timestamp expiryTimeStamp = Timestamp.from(instant);

        Collection<VehicleConfig> configs = vehicleConfigRepository.findByTimestampCreateLessThanAndStatusIn(
                expiryTimeStamp, List.of(Status.INCOMPLETE, Status.READY, Status.ERROR));
        vehicleConfigRepository.flush();

        deleteVehicleConfigsPermanently(configs);
    }

    @Transactional
    public void deleteExpiredVehicleConfigs() {
        Instant instant = LocalDate.now().minusYears(DELETE_VEHICLE_CONFIGURATION_EXPIRY_YEARS)
                .atStartOfDay(ZoneId.systemDefault()).toInstant();
        Timestamp expiryTimeStamp = Timestamp.from(instant);

        Collection<VehicleConfig> toDelete = vehicleConfigRepository.findByTimestampCreateLessThan(expiryTimeStamp);
        vehicleConfigRepository.flush();

        deleteVehicleConfigsPermanently(toDelete);
    }

    private VehicleConfigDTO map(VehicleConfig config, Boolean vehicleConfigIdsWithAccess) {
        if (vehicleConfigIdsWithAccess == null) {
            vehicleConfigIdsWithAccess = false;
        }

        return VehicleConfigMapper.toDto(config, vehicleConfigIdsWithAccess);
    }

    private void updateMotorAndEngine(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getVehiclePartList() == null || persistentVehicleConfig.getVehiclePartList() == null) {
            return;
        }

        VehiclePartList vehiclePartList = vehicleConfig.getVehiclePartList();
        VehiclePartList persistentVehiclePartList = persistentVehicleConfig.getVehiclePartList();
        if (!Objects.equals(vehiclePartList.getProductKeyGearbox(), persistentVehiclePartList.getProductKeyGearbox())) {
            persistentVehiclePartList.setProductKeyGearbox(vehiclePartList.getProductKeyGearbox());
            persistentVehicleConfig.setVehiclePartList(persistentVehiclePartList);
        }

        if (!Objects.equals(vehiclePartList.getProductKeyMotor(), persistentVehiclePartList.getProductKeyMotor())) {
            persistentVehiclePartList.setProductKeyMotor(vehiclePartList.getProductKeyMotor());
            persistentVehicleConfig.setVehiclePartList(persistentVehiclePartList);
        }
    }

    private void setVehicleConfigTiWhImportMotor(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getTiWhImportMotor() != null && vehicleConfig.getTiWhImportMotor().getId() != null && (
                persistentVehicleConfig.getTiWhImportMotor() == null || !persistentVehicleConfig.getTiWhImportMotor()
                        .getId().equals(vehicleConfig.getTiWhImportMotor().getId()))) {
            persistentVehicleConfig.setTiWhImportMotor(
                    tiWhImportRepository.findById(vehicleConfig.getTiWhImportMotor().getId()).orElse(null));
        }
    }

    private void setVehicleConfigTiWhImportGearBox(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getTiWhImportGearbox() != null && vehicleConfig.getTiWhImportGearbox().getId() != null && (
                persistentVehicleConfig.getTiWhImportGearbox() == null
                        || !persistentVehicleConfig.getTiWhImportGearbox().getId()
                        .equals(vehicleConfig.getTiWhImportGearbox().getId()))) {
            persistentVehicleConfig.setTiWhImportGearbox(
                    tiWhImportRepository.findById(vehicleConfig.getTiWhImportGearbox().getId()).orElse(null));
        }
    }

    private void setVehicleConfigTiWhImportVehicle(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getTiWhImportVehicle() != null && vehicleConfig.getTiWhImportVehicle().getId() != null && (
                persistentVehicleConfig.getTiWhImportVehicle() == null
                        || !persistentVehicleConfig.getTiWhImportVehicle().getId()
                        .equals(vehicleConfig.getTiWhImportVehicle().getId()))) {
            persistentVehicleConfig.setTiWhImportVehicle(
                    tiWhImportRepository.findById(vehicleConfig.getTiWhImportVehicle().getId()).orElse(null));
        }
    }

    private void setVehicleConfigModelImport(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getModelImport() != null && vehicleConfig.getModelImport().getId() != null && (
                persistentVehicleConfig.getModelImport() == null || !persistentVehicleConfig.getModelImport().getId()
                        .equals(vehicleConfig.getModelImport().getId()))) {
            persistentVehicleConfig.setModelImport(
                    modelImportRepository.findById(vehicleConfig.getModelImport().getId()).orElse(null));
        }
    }

    private void setVehicleConfigModel(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getModel() != null && vehicleConfig.getModel().getId() != null && (
                persistentVehicleConfig.getModel() == null || !persistentVehicleConfig.getModel().getId()
                        .equals(vehicleConfig.getModel().getId()))) {
            persistentVehicleConfig.setModel(modelRepository.findById(vehicleConfig.getModel().getId()).orElse(null));
        }
    }

    private void setVehicleConfigResource(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getResource() != null && vehicleConfig.getResource().getId() != null && (
                persistentVehicleConfig.getResource() == null || !persistentVehicleConfig.getResource().getId()
                        .equals(vehicleConfig.getResource().getId()))) {
            persistentVehicleConfig.setResource(
                    resourceRepository.findById(vehicleConfig.getResource().getId()).orElse(null));
        }
    }

    private static void setVehicleConfigStatus(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        Collection<VehicleConfigCategoryStatus> vehicleConfigCategoryStatus = vehicleConfig.getVehicleConfigCategoryStatus();
        if (vehicleConfigCategoryStatus == null) {
            return;
        }

        for (VehicleConfigCategoryStatus status : vehicleConfigCategoryStatus) {
            for (VehicleConfigCategoryStatus dbStatus : persistentVehicleConfig.getVehicleConfigCategoryStatus()) {
                if (status.getId().equals(dbStatus.getId())) {
                    dbStatus.setVehicleConfigStatus(status.getVehicleConfigStatus());

                    break;
                }
            }
        }
    }

    private void setVehicleConfigSalesRegion(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getSalesRegion() != null && vehicleConfig.getSalesRegion().getId() != null && (
                persistentVehicleConfig.getSalesRegion() == null || !persistentVehicleConfig.getSalesRegion().getId()
                        .equals(vehicleConfig.getSalesRegion().getId()))) {
            persistentVehicleConfig.setSalesRegion(
                    salesRegionRepository.findById(vehicleConfig.getSalesRegion().getId()).orElse(null));
        }
    }

    private void setVehicleConfigProject(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (vehicleConfig.getVehicleProject() != null && vehicleConfig.getVehicleProject().getId() != null && (
                persistentVehicleConfig.getVehicleProject() == null || !persistentVehicleConfig.getVehicleProject()
                        .getId().equals(vehicleConfig.getVehicleProject().getId()))) {
            persistentVehicleConfig.setVehicleProject(
                    vehicleProjectRepository.findById(vehicleConfig.getVehicleProject().getId()).orElse(null));
        }
    }

    private void updateVehicleConfigOwnerGroup(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getOwnerGroup().getId(), persistentVehicleConfig.getOwnerGroup().getId())) {
            persistentVehicleConfig.setUserGroups(vehicleConfig.getUserGroups());
            persistentVehicleConfig.setOwnerGroup(vehicleConfig.getOwnerGroup());
        }
    }

    private static void updateVehicleConfigSetVersionId(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getSetVersionId(), persistentVehicleConfig.getSetVersionId())) {
            persistentVehicleConfig.setSetVersionId(vehicleConfig.getSetVersionId());
        }
    }

    private static void updateVehicleConfigSmartFix(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.isSmartFixesActive(), persistentVehicleConfig.isSmartFixesActive())) {
            persistentVehicleConfig.setSmartFixesActive(vehicleConfig.isSmartFixesActive());
        }
    }

    private static void updateVehicleConfigDeletionDate(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getDeletionDate(), persistentVehicleConfig.getDeletionDate())) {
            persistentVehicleConfig.setDeletionDate(vehicleConfig.getDeletionDate());
        }
    }

    private static void updateVehicleConfigPlsDataLockId(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getPlsDataLockId(), persistentVehicleConfig.getPlsDataLockId())) {
            persistentVehicleConfig.setPlsDataLockId(vehicleConfig.getPlsDataLockId());
        }
    }

    private static void updateVehicleConfigPlsDataId(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getPlsDataId(), persistentVehicleConfig.getPlsDataId())) {
            persistentVehicleConfig.setPlsDataId(vehicleConfig.getPlsDataId());
        }
    }

    private static void updateVehicleConfigPlsProductDataId(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getPlsProductDataId(), persistentVehicleConfig.getPlsProductDataId())) {
            persistentVehicleConfig.setPlsProductDataId(vehicleConfig.getPlsProductDataId());
        }
    }

    private static void updateVehicleConfigModelYear(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getModelYear(), persistentVehicleConfig.getModelYear())) {
            persistentVehicleConfig.setModelYear(vehicleConfig.getModelYear());
        }
    }

    private static void updateVehicleConfigPrNumberString(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getPrNumberString(), persistentVehicleConfig.getPrNumberString())) {
            persistentVehicleConfig.setPrNumberString(vehicleConfig.getPrNumberString());
        }
    }

    private static void updateVehicleConfigValidDate(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getValidDate(), persistentVehicleConfig.getValidDate())) {
            persistentVehicleConfig.setValidDate(vehicleConfig.getValidDate());
        }
    }

    private static void updateVehicleConfigDescription(VehicleConfig vehicleConfig,
            VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getDescription(), persistentVehicleConfig.getDescription())) {
            persistentVehicleConfig.setDescription(vehicleConfig.getDescription());
        }
    }

    private static void updateVehicleConfigName(VehicleConfig vehicleConfig, VehicleConfig persistentVehicleConfig) {
        if (!Objects.equals(vehicleConfig.getName(), persistentVehicleConfig.getName())) {
            persistentVehicleConfig.setName(vehicleConfig.getName());
        }
    }

    private VehicleConfig createNewVehicleConfigInDB(VehicleConfig vehicleConfig) {
        if (vehicleConfig.getResource() == null) {
            createVehicleConfigResource(vehicleConfig);
        }

        if (vehicleConfig.getVehicleConfigCategoryStatus() == null || vehicleConfig.getVehicleConfigCategoryStatus()
                .isEmpty()) {
            Collection<VehicleConfigCategoryStatus> statuses = Arrays.stream(VehicleConfigCategory.values())
                    .map(VehicleFactory::createFogConfigCategoryStatus).toList();
            vehicleConfig.setVehicleConfigCategoryStatus(statuses);
        }

        linkVehicleConfigCategoryStatuses(vehicleConfig);

        if (vehicleConfig.getSetVersionId() == null) {
            if (vehicleConfig.getVehicleProject() != null && vehicleConfig.getVehicleProject().getProduct() != null
                    && vehicleConfig.getVehicleProject().getProduct().getProductKey() != null) {
                String vehicleProductKey = vehicleConfig.getVehicleProject().getProduct().getProductKey();
                Long setVersionId = productRepository.findOneByProductKey(vehicleProductKey).getSetVersionId();
                vehicleConfig.setSetVersionId(setVersionId);
            } else {
                vehicleConfig.setSetVersionId(Product.INITIAL_SET_VERSION_ID);
            }
        }

        if (vehicleConfig.getCostGroupVersion() == null) {
            vehicleConfig.setCostGroupVersion(costGroupRepository.findLastVersion());
        }

        if (vehicleConfig.getValidDate() == null) {
            vehicleConfig.setValidDate(new Date());
        }

        vehicleConfig.setChange(userManager.getCurrentUserId());
        return vehicleConfigRepository.save(vehicleConfig);
    }

    private void linkVehicleConfigCategoryStatuses(VehicleConfig vehicleConfig) {
        if (vehicleConfig.getVehicleConfigCategoryStatus() == null) {
            return;
        }

        for (VehicleConfigCategoryStatus status : vehicleConfig.getVehicleConfigCategoryStatus()) {
            status.setVehicleConfig(vehicleConfig);
        }
    }

    private void createVehicleConfigResource(VehicleConfig vehicleConfig) {
        Resource resource = new Resource();

        resource.setChange(userManager.getCurrentUserId());
        resource.setType(ResourceType.FZG_KONFIG);

        vehicleConfig.setResource(resource);
    }

    private void deleteVehicleConfigsPermanently(Collection<VehicleConfig> configs) {
        Collection<Long> configIds = HashSet.newHashSet(configs.size());
        Collection<VehiclePartList> vehiclePartLists = new HashSet<>();

        for (VehicleConfig config : configs) {
            Long configId = config.getId();
            configIds.add(configId);
            userPropertyManager.delete(PropertyType.RECENTLY_USED, configId);

            VehiclePartList vehiclePartList = config.getVehiclePartList();
            if (vehiclePartList != null) {
                vehiclePartLists.add(vehiclePartList);
            }

            config.getOwnerGroup().getVehicleConfigs().remove(config);
            for (UserGroup userGroup : config.getUserGroups()) {
                userGroup.getVehicleConfigs().remove(config);
            }
        }

        userMessageRepository.deleteByVehicleConfigIdIn(configIds);
        filteredOutEfsElementRepository.deleteByVehicleConfigIdIn(configIds);

        Collection<Long> vehiclePartListIds = vehiclePartLists.stream().map(VehiclePartList::getId).toList();
        Collection<EfsElement> efsElements = efsElementRepository.findByVehiclePartListIdIn(vehiclePartListIds);

        Collection<Long> efsElementIds = efsElements.stream().map(EfsElement::getId).toList();
        efsElementAggregateMappingRepository.deleteByEfsElementIdIn(efsElementIds);

        efsElementHistoryRepository.deleteByVehiclePartListIdIn(vehiclePartListIds);
        efsElementRepository.deleteAll(efsElements);

        efsElementMaraHistoryRepository.deleteByVehiclePartListIdIn(vehiclePartListIds);
        efsElementMaraRepository.deleteByVehiclePartListIdIn(vehiclePartListIds);

        vehicleConfigPrNumberMappingRepository.deleteById_VehicleConfigIdIn(configIds);
        vehicleConfigRepository.deleteAll(configs);
    }

    private List<VehicleConfigDTO> filterDeletedVehicleConfigs(Collection<VehicleConfig> vehicleConfigs,
            Map<Long, Boolean> vehicleConfigIdsWithAccess) {
        return vehicleConfigs.stream().filter(config -> Objects.isNull(config.getDeletionDate()))
                .map(config -> map(config, vehicleConfigIdsWithAccess.get(config.getId()))).toList();
    }

    private boolean isConfigEditable(User user, VehicleConfig config) {
        return user.getUserGroups().stream().map(UserGroup::getId).collect(Collectors.toSet())
                .contains(config.getOwnerGroup().getId());
    }
}
