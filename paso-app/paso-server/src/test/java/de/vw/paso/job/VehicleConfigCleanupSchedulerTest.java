package de.vw.paso.job;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.core.TestVehicleConfigManager;
import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.logic.vehicle.VehicleConfigManager;
import de.vw.paso.mapper.VehicleConfigMapper;
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
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class VehicleConfigCleanupSchedulerTest extends AbstractServiceTests {

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
    private VehicleConfigManager vehicleConfigManager;

    @Test
    void testVehicleConfigCleanupJobWithDefaultDays() {
        VehicleConfigDTO vehicleConfig = createFzgConfigWithNameAndVehicleProject();
        vehicleConfig.setDeletionDate(Date.valueOf(LocalDate.now().minusDays(40)));

        VehicleConfig savedVehicleConfig = vehicleConfigRepository.save(VehicleConfigMapper.toEntity(vehicleConfig));

        vehicleConfigManager.deleteExpiredDeletedVehicleConfigurations();

        assertNull(vehicleConfigRepository.findById(savedVehicleConfig.getId()).orElse(null));
    }

    @Test
    void testVehicleConfigCleanupJobWithMultipleVehicleConfigs() {
        VehicleConfigDTO vehicleConfig = createFzgConfigWithNameAndVehicleProject();
        vehicleConfig.setDeletionDate(Date.valueOf(LocalDate.now().minusDays(350)));

        VehicleConfigDTO vehicleConfig2 = createFzgConfigWithNameAndVehicleProject();
        vehicleConfig2.setDeletionDate(Date.valueOf(LocalDate.now().minusDays(40)));

        List<VehicleConfig> savedVehicleConfig = vehicleConfigRepository.saveAll(
                List.of(VehicleConfigMapper.toEntity(vehicleConfig), VehicleConfigMapper.toEntity(vehicleConfig2)));

        vehicleConfigManager.deleteExpiredDeletedVehicleConfigurations();

        assertNull(vehicleConfigRepository.findById(savedVehicleConfig.get(0).getId()).orElse(null));
        assertNull(vehicleConfigRepository.findById(savedVehicleConfig.get(1).getId()).orElse(null));
    }

    @Test
    void testVehicleConfigCleanupJobWithNoExpiredVehicleConfig() {
        VehicleConfigDTO vehicleConfig = createFzgConfigWithNameAndVehicleProject();
        vehicleConfig.setDeletionDate(Date.valueOf(LocalDate.now()));

        VehicleConfig savedVehicleConfig = vehicleConfigRepository.save(VehicleConfigMapper.toEntity(vehicleConfig));

        vehicleConfigManager.deleteExpiredDeletedVehicleConfigurations();

        assertNotNull(vehicleConfigRepository.findById(savedVehicleConfig.getId()).orElse(null));
    }

    @Transactional
    @Test
    void testScheduledCleanUpAfterFourYearsWithDeletable() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpDeletable(ChronoUnit.YEARS, 4, AbstractModifiableEntity::setTimestampCreate);

        vehicleConfigManager.deleteExpiredVehicleConfigs();

        testVehicleConfigManager.assertDeletableNotInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpAfterFourYearsWithNonDeletable() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpNonDeletable(ChronoUnit.YEARS, 4, AbstractModifiableEntity::setTimestampCreate);

        vehicleConfigManager.deleteExpiredVehicleConfigs();

        testVehicleConfigManager.assertNonDeletableDataInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpAfterFourYears() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpDeletable(ChronoUnit.YEARS, 4, AbstractModifiableEntity::setTimestampCreate);
        testVehicleConfigManager.setUpNonDeletable(ChronoUnit.YEARS, 4, AbstractModifiableEntity::setTimestampCreate);

        vehicleConfigManager.deleteExpiredVehicleConfigs();

        testVehicleConfigManager.assertDeletableNotInDatabase();
        testVehicleConfigManager.assertNonDeletableDataInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpUnfinishedConfigAfterThirtyDaysDeletable() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpDeletable(ChronoUnit.DAYS, 30, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusDeletable(Status.INCOMPLETE);

        vehicleConfigManager.deleteUnfinishedVehicleConfigs();

        testVehicleConfigManager.assertDeletableNotInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpUnfinishedConfigAfterThirtyDaysNonDeletable() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpNonDeletable(ChronoUnit.DAYS, 30, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusNonDeletable(Status.INCOMPLETE);

        vehicleConfigManager.deleteUnfinishedVehicleConfigs();

        testVehicleConfigManager.assertNonDeletableDataInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpUnfinishedConfigAfterThirtyDays() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpDeletable(ChronoUnit.DAYS, 30, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setUpNonDeletable(ChronoUnit.DAYS, 30, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusDeletable(Status.INCOMPLETE);
        testVehicleConfigManager.setStatusNonDeletable(Status.INCOMPLETE);

        vehicleConfigManager.deleteUnfinishedVehicleConfigs();

        testVehicleConfigManager.assertDeletableNotInDatabase();
        testVehicleConfigManager.assertNonDeletableDataInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpUnfinishedConfigAfterThirtyDaysFinished() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpDeletable(ChronoUnit.DAYS, 30, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setUpNonDeletable(ChronoUnit.DAYS, 30, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusDeletable(Status.COMPLETE);
        testVehicleConfigManager.setStatusNonDeletable(Status.INCOMPLETE);

        vehicleConfigManager.deleteUnfinishedVehicleConfigs();

        testVehicleConfigManager.assertDeletableInDatabase();
        testVehicleConfigManager.assertNonDeletableDataInDatabase();
    }

    @Transactional
    @Test
    void testScheduledCleanUpDeletableLessThanThirtyDaysOld() {
        TestVehicleConfigManager testVehicleConfigManager = new TestVehicleConfigManager(userRepository,
                vehiclePartListRepository, vehicleConfigRepository, vehicleProjectRepository, setVersionRepository,
                userGroupRepository, efsElementMaraRepository, efsElementRepository, efsElementMaraHistoryRepository,
                efsElementAggregateMappingRepository, vehicleConfigCategoryStatusRepository,
                filteredOutEfsElementRepository, userMessageRepository, userPropertyManager);
        testVehicleConfigManager.setUpDeletable(ChronoUnit.DAYS, 1, VehicleConfig::setTimestampCreate);
        testVehicleConfigManager.setStatusDeletable(Status.INCOMPLETE);

        vehicleConfigManager.deleteUnfinishedVehicleConfigs();

        testVehicleConfigManager.assertDeletableInDatabase();
    }
}
