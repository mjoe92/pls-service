package de.vw.paso.services.vehicle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static testutil.TestUtils.saveUserGroup;

import java.util.Collection;
import java.util.List;

import de.vw.paso.consumer.vehicle.LoadVehicleConfigConsumer;
import de.vw.paso.consumer.vehicle.SaveVehicleConfigConsumer;
import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.core.TestVehicleConfigManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.mapper.VehiclePartListMapper;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.pls.Status;
import de.vw.paso.repository.masterdata.VehicleProjectRepository;
import de.vw.paso.repository.message.UserMessageRepository;
import de.vw.paso.repository.partlist.EfsElementAggregateMappingRepository;
import de.vw.paso.repository.partlist.EfsElementMaraHistoryRepository;
import de.vw.paso.repository.partlist.EfsElementMaraRepository;
import de.vw.paso.repository.partlist.EfsElementRepository;
import de.vw.paso.repository.partlist.FilteredOutEfsElementRepository;
import de.vw.paso.repository.partlist.SetVersionRepository;
import de.vw.paso.repository.partlist.VehiclePartListRepository;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.repository.vehicle.VehicleConfigCategoryStatusRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.usergroup.UserGroupRestController;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigRestController;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class VehicleConfigTests extends AbstractServiceTests {

    @Autowired
    private VehicleConfigRepository vehicleConfigRepository;
    @Autowired
    private VehiclePartListRepository vehiclePartListRepository;
    @Autowired
    private UserPropertyManager userPropertyManager;
    @Autowired
    private EfsElementRepository efsElementRepository;
    @Autowired
    private EfsElementMaraRepository efsElementMaraRepository;
    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private FilteredOutEfsElementRepository filteredOutEfsElementRepository;
    @Autowired
    private VehicleConfigCategoryStatusRepository vehicleConfigCategoryStatusRepository;
    @Autowired
    private EfsElementAggregateMappingRepository efsElementAggregateMappingRepository;
    @Autowired
    private EfsElementMaraHistoryRepository efsElementMaraHistoryRepository;
    @Autowired
    private VehicleProjectRepository vehicleProjectRepository;
    @Autowired
    private SetVersionRepository setVersionRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SaveVehicleConfigConsumer saveVehicleConfigConsumer;
    @Autowired
    private LoadVehicleConfigConsumer loadVehicleConfigsConsumer;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserGroupRestController userGroupRestController;
    @Autowired
    private VehicleConfigRestController vehicleConfigRestController;

    @Test
    public void testCreatingVehicleConfig() {

        VehicleConfigDTO config = createFzgConfigWithNameAndVehicleProject();

        VehicleConfig foundConfig = vehicleConfigRepository.findById(config.getId()).orElseThrow();
        assertEquals(config.getName(), foundConfig.getName());
        assertNotNull(config.getUserCreate());
        assertNotNull(config.getResource());
        assertNotNull(config.getVehicleConfigCategoryStatus());
        assertNotNull(config.getValidDate());
        assertNotNull(config.getVehicleProject());
        validateDefaultInitialValues(foundConfig);
        assertNull(config.getVehiclePartList());
        assertNull(config.getModel());
        assertNull(config.getModelImport());
        assertNull(config.getTiWhImportVehicle());
        assertNull(config.getTiWhImportMotor());
        assertNull(config.getTiWhImportGearbox());
        assertNull(config.getSalesRegion());
        assertNull(config.getModelYear());
        assertNull(config.getPrNumberString());
    }

    @Test
    @Transactional
    @Disabled
    public void testDeletingVehicleConfig() {
        VehicleConfigDTO configReturnedByService = createFzgConfigWithNameAndVehicleProject();

        VehicleConfig foundConfig = vehicleConfigRepository.findById(configReturnedByService.getId()).orElseThrow();
        foundConfig.setStatus(Status.COMPLETE);
        assertNull(foundConfig.getDeletionDate());

        saveUserGroup(userManager, userGroupRepository, TEST_USER_ID, vehicleConfigRepository, 1L);

        vehicleConfigRestController.deleteVehicleConfig(foundConfig.getId());

        assertNotNull(vehicleConfigRepository.findById(foundConfig.getId()).orElseThrow().getDeletionDate());
    }

    @Test
    public void testDeletingVehicleConfigWithPartList() {
        VehicleConfigDTO configReturnedByService = createFzgConfigWithNameAndVehicleProject();

        VehicleConfig foundConfig = vehicleConfigRepository.findById(configReturnedByService.getId()).orElseThrow();
        foundConfig.setStatus(Status.COMPLETE);
        vehicleConfigRepository.save(foundConfig);

        UserGroupDTO userGroupDTO = userGroupRestController.getAllUserGroups().userGroupDTOList().getFirst();
        userGroupDTO.setVehicleConfigs(List.of(configReturnedByService));
        userGroupRestController.saveUserGroup(userGroupDTO);

        saveUserGroup(userManager, userGroupRepository, TEST_USER_ID, vehicleConfigRepository, 1L);

        VehicleConfigDTO foundConfigDto = vehicleConfigRestController.loadFzgKonfig(configReturnedByService.getId());

        VehiclePartListDTO vehiclePartListDTO = new VehiclePartListDTO();
        vehiclePartListDTO.setVehicleConfig(foundConfigDto);
        vehiclePartListDTO.setProductKeyVehicle("5G0");
        vehiclePartListDTO.setChange(userManager.getCurrentUserId());

        foundConfigDto.setVehiclePartList(vehiclePartListDTO);

        foundConfigDto.setTiWhImportVehicle(getTiWhImport("YYY"));

        vehicleConfigRestController.saveFzgKonfig(foundConfigDto);

        VehiclePartList vehiclePartList = VehiclePartListMapper.toEntity(vehiclePartListDTO, null);
        vehiclePartListRepository.save(vehiclePartList);

        assertNull(vehicleConfigRepository.findById(foundConfig.getId()).get().getDeletionDate());

        vehicleConfigRestController.deleteVehicleConfig(foundConfig.getId());

        assertNotNull(vehicleConfigRepository.findById(foundConfig.getId()).get().getDeletionDate());
    }

    @Test
    public void testCreateVehicleConfigWithTiWhImport() {
        VehicleConfigDTO Config = createFzgConfigWithNameAndVehicleProject();

        saveUserGroup(userManager, userGroupRepository, TEST_USER_ID, vehicleConfigRepository, 1L);

        VehicleConfigDTO foundConfig = setTiWhImportForVehicleConfig(Config, "A");

        assertTiWhImports("A", foundConfig);
    }

    @Test
    public void changeWithTiWhImport() {
        VehicleConfigDTO Config = createFzgConfigWithNameAndVehicleProject();

        VehicleConfigDTO foundConfig = setTiWhImportForVehicleConfig(Config, "A");

        saveUserGroup(userManager, userGroupRepository, TEST_USER_ID, vehicleConfigRepository, 1L);

        foundConfig = setTiWhImportForVehicleConfig(foundConfig, "B");

        assertTiWhImports("B", foundConfig);
    }

    @Test
    public void testLoadVehicleConfigByBrand() {
        Brand brand = Brand.VW;
        loadVehicleConfigsConsumer.loadVehicleConfigByBrand(brand);
        Collection<VehicleConfigDTO> result = loadVehicleConfigsConsumer.getResult();

        assertEquals(0, result.size(), "Check config count");
        for (VehicleConfigDTO element : result) {
            assertEquals(brand, element.getVehicleProject().getBrandCode(), "Check brand");
        }
    }

    @Test
    public void testLoadVehicleConfigByProjectId() {
        Long projectID = 1187L;
        loadVehicleConfigsConsumer.loadVehicleConfigByProjectId(projectID);
        Collection<VehicleConfigDTO> result = loadVehicleConfigsConsumer.getResult();

        assertEquals(0, result.size(), "Check Config Count");
        for (VehicleConfigDTO element : result) {
            assertEquals(projectID, element.getVehicleProject().getId(), "Check vehicle project");
        }
    }

    @Transactional
    @Test
    public void testDeletionWhenConfigIsFinished() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);

        testVehicleConfigManager.setUpNonDeletable(0, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusNonDeletable(Status.COMPLETE);
        vehicleConfigRestController.deleteVehicleConfig(testVehicleConfigManager.getConfigNonDeletable().getId());

        testVehicleConfigManager.assertNonDeletableDataInDatabase();

        assertNotNull(
                vehicleConfigRepository.findById(testVehicleConfigManager.getConfigNonDeletable().getId()).orElseThrow()
                        .getDeletionDate());
    }

    @Transactional
    @Test
    public void testDeletionWhenConfigIsUnfinished() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);

        testVehicleConfigManager.setUpDeletable(0, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusDeletable(Status.INCOMPLETE);
        vehicleConfigRestController.deleteVehicleConfig(testVehicleConfigManager.getConfigDeletable().getId());

        testVehicleConfigManager.assertDeletableNotInDatabase();
    }

    @Transactional
    @Test
    public void testResetDeletion() {
        TestVehicleConfigManager vehicleConfigHolder = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);

        vehicleConfigHolder.setUpDeletable(0, VehicleConfig::setTimestampCreate);

        VehicleConfig config = vehicleConfigHolder.getConfigDeletable();
        vehicleConfigHolder.setStatusDeletable(Status.COMPLETE);
        vehicleConfigRestController.deleteVehicleConfig(config.getId());

        assertNotNull(vehicleConfigRepository.findById(config.getId()).orElseThrow().getDeletionDate());

        vehicleConfigRestController.resetDeletion(config.getId());

        assertNull(vehicleConfigRepository.findById(config.getId()).orElseThrow().getDeletionDate());
    }

    private VehicleConfigDTO setTiWhImportForVehicleConfig(VehicleConfigDTO config, String end) {
        config.setTiWhImportVehicle(getTiWhImport(getProdFzg(end)));
        config.setTiWhImportMotor(getTiWhImport(getProdMot(end)));
        config.setTiWhImportGearbox(getTiWhImport(getProdGetr(end)));
        saveVehicleConfigConsumer.saveVehicleConfig(config, null);

        return saveVehicleConfigConsumer.getResult();
    }

    private void assertTiWhImports(String end, VehicleConfigDTO foundConfig) {
        VehicleConfig vehicleConfig = vehicleConfigRepository.findById(foundConfig.getId()).get();

        assertEquals(getProdFzg(end), vehicleConfig.getTiWhImportVehicle().getProductKey());
        assertEquals(getProdMot(end), vehicleConfig.getTiWhImportMotor().getProductKey());
        assertEquals(getProdGetr(end), vehicleConfig.getTiWhImportGearbox().getProductKey());
    }

    private static String getProdFzg(String end) {
        return "TT" + end;
    }

    private static String getProdMot(String end) {
        return "TTA" + end;
    }

    private static String getProdGetr(String end) {
        return "TTB" + end;
    }
}
