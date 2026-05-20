package de.vw.paso.core;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.masterdata.domain.VehicleProject;
import de.vw.paso.message.UserMessageType;
import de.vw.paso.message.domain.UserMessage;
import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.EfsElementAggregateMapping;
import de.vw.paso.partlist.domain.EfsElementMara;
import de.vw.paso.partlist.domain.EfsElementMaraHistory;
import de.vw.paso.partlist.domain.FilteredOutEfsElement;
import de.vw.paso.partlist.domain.SetVersion;
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
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.ResourceType;
import de.vw.paso.user.domain.Resource;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;

public class TestVehicleConfigManager {

    private final VehiclePartListRepository vehiclePartListRepository;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final VehicleProjectRepository vehicleProjectRepository;
    private final SetVersionRepository setVersionRepository;
    private final UserGroupRepository userGroupRepository;
    private final EfsElementMaraRepository efsElementMaraRepository;
    private final EfsElementRepository efsElementRepository;
    private final EfsElementMaraHistoryRepository efsElementMaraHistoryRepository;
    private final EfsElementAggregateMappingRepository efsElementAggregateMappingRepository;
    private final VehicleConfigCategoryStatusRepository vehicleConfigCategoryStatusRepository;
    private final FilteredOutEfsElementRepository filteredOutEfsElementRepository;
    private final UserMessageRepository userMessageRepository;
    private final UserPropertyManager userPropertyManager;
    private final UserRepository userRepository;
    private VehicleConfig configDeletable;
    private EfsElementMara efsElementMaraDeletable;
    private EfsElement efsElementDeletable;
    private EfsElementMaraHistory efsElementMaraHistoryDeletable;
    private EfsElementAggregateMapping efsElementAggregateMappingDeletable;
    private FilteredOutEfsElement filteredOutEfsElementDeletable;
    private UserMessage userMessageDeletable;
    private VehicleConfig configNonDeletable;
    private EfsElementMara efsElementMaraNonDeletable;
    private EfsElement efsElementNonDeletable;
    private EfsElementMaraHistory efsElementMaraHistoryNonDeletable;
    private EfsElementAggregateMapping efsElementAggregateMappingNonDeletable;
    private FilteredOutEfsElement filteredOutEfsElementNonDeletable;
    private UserMessage userMessageNonDeletable;

    public TestVehicleConfigManager(UserRepository userRepository, VehiclePartListRepository vehiclePartListRepository,
        VehicleConfigRepository vehicleConfigRepository, VehicleProjectRepository vehicleProjectRepository,
        SetVersionRepository setVersionRepository, UserGroupRepository userGroupRepository,
        EfsElementMaraRepository efsElementMaraRepository, EfsElementRepository efsElementRepository,
        EfsElementMaraHistoryRepository efsElementMaraHistoryRepository,
        EfsElementAggregateMappingRepository efsElementAggregateMappingRepository,
        VehicleConfigCategoryStatusRepository vehicleConfigCategoryStatusRepository,
        FilteredOutEfsElementRepository filteredOutEfsElementRepository, UserMessageRepository userMessageRepository,
        UserPropertyManager userPropertyManager) {
        this.vehiclePartListRepository = vehiclePartListRepository;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.vehicleProjectRepository = vehicleProjectRepository;
        this.setVersionRepository = setVersionRepository;
        this.userGroupRepository = userGroupRepository;
        this.efsElementMaraRepository = efsElementMaraRepository;
        this.efsElementRepository = efsElementRepository;
        this.efsElementMaraHistoryRepository = efsElementMaraHistoryRepository;
        this.efsElementAggregateMappingRepository = efsElementAggregateMappingRepository;
        this.vehicleConfigCategoryStatusRepository = vehicleConfigCategoryStatusRepository;
        this.filteredOutEfsElementRepository = filteredOutEfsElementRepository;
        this.userMessageRepository = userMessageRepository;
        this.userPropertyManager = userPropertyManager;
        this.userRepository = userRepository;
    }

    public void setUpDeletable(int toSubtract, BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp) {
        setUpDeletable(null, toSubtract, setUpTimeStamp);
    }

    public void setUpDeletable(ChronoUnit unit, int toSubtract, BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp) {
        Timestamp targetTimeStamp =
            unit == null ? Timestamp.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : Timestamp.from(switch (unit) {
                    case ChronoUnit.YEARS ->
                        LocalDate.now().minusYears(toSubtract).minusDays(1).atStartOfDay(ZoneId.systemDefault())
                        .toInstant();
                    case ChronoUnit.DAYS ->
                        LocalDate.now().minusDays(toSubtract).minusDays(1).atStartOfDay(ZoneId.systemDefault())
                        .toInstant();
                    default -> LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
                });
        setUpDeletable(targetTimeStamp, setUpTimeStamp);
    }

    public void setUpDeletable(Timestamp targetTimeStamp, BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp) {
        //set up four-year-old vehicle config
        VehiclePartList partListDeletable = addVehiclePartList(vehiclePartListRepository);
        configDeletable = addVehicleConfig(partListDeletable, targetTimeStamp, "DELETABLE", setUpTimeStamp,
            vehicleConfigRepository, vehicleProjectRepository, setVersionRepository, userGroupRepository);
        efsElementMaraDeletable = addEfsElementMara(partListDeletable.getId(), efsElementMaraRepository);
        efsElementDeletable = addEfsElement(partListDeletable.getId(), efsElementMaraDeletable, efsElementRepository);
        efsElementMaraHistoryDeletable = addEfsElementMaraHistory(efsElementMaraDeletable, partListDeletable.getId(),
            efsElementMaraHistoryRepository);
        efsElementAggregateMappingDeletable = addEfsElementAggregateMapping(efsElementDeletable,
            efsElementAggregateMappingRepository);
        filteredOutEfsElementDeletable = addFilteredOutEfsElements(configDeletable, efsElementMaraDeletable,
            filteredOutEfsElementRepository);
        userMessageDeletable = addUserMessage(configDeletable, userMessageRepository);
        addToRecentlyUser(configDeletable, userPropertyManager);
    }

    public void setUpNonDeletable(int toSubtract, BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp) {
        setUpNonDeletable(null, toSubtract, setUpTimeStamp);
    }

    public void setUpNonDeletable(ChronoUnit unit, int toSubtract,
        BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp) {
        Timestamp targetTimeStamp =
            unit == null ? Timestamp.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                : Timestamp.from(switch (unit) {
                    case ChronoUnit.YEARS ->
                        LocalDate.now().minusYears(toSubtract).plusDays(1).atStartOfDay(ZoneId.systemDefault())
                        .toInstant();
                    case ChronoUnit.DAYS ->
                        LocalDate.now().minusDays(toSubtract).plusDays(1).atStartOfDay(ZoneId.systemDefault())
                        .toInstant();
                    default -> LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
                });
        setUpNonDeletable(targetTimeStamp, setUpTimeStamp);
    }

    private void setUpNonDeletable(Timestamp targetTimeStamp, BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp) {
        //set up three-year-old vehicle config
        VehiclePartList partListNonDeletable = addVehiclePartList(vehiclePartListRepository);
        configNonDeletable = addVehicleConfig(partListNonDeletable, targetTimeStamp, "NON_DELETABLE", setUpTimeStamp,
            vehicleConfigRepository, vehicleProjectRepository, setVersionRepository, userGroupRepository);
        efsElementMaraNonDeletable = addEfsElementMara(partListNonDeletable.getId(), efsElementMaraRepository);
        efsElementNonDeletable = addEfsElement(partListNonDeletable.getId(), efsElementMaraNonDeletable,
            efsElementRepository);
        efsElementMaraHistoryNonDeletable = addEfsElementMaraHistory(efsElementMaraNonDeletable,
            partListNonDeletable.getId(), efsElementMaraHistoryRepository);
        efsElementAggregateMappingNonDeletable = addEfsElementAggregateMapping(efsElementNonDeletable,
            efsElementAggregateMappingRepository);
        filteredOutEfsElementNonDeletable = addFilteredOutEfsElements(configNonDeletable, efsElementMaraNonDeletable,
            filteredOutEfsElementRepository);
        userMessageNonDeletable = addUserMessage(configNonDeletable, userMessageRepository);
        addToRecentlyUser(configNonDeletable, userPropertyManager);
    }

    public VehicleConfig addVehicleConfig(VehiclePartList partList, Timestamp timestamp, String name,
        BiConsumer<VehicleConfig, Timestamp> setUpTimeStamp, VehicleConfigRepository vehicleConfigRepository,
        VehicleProjectRepository vehicleProjectRepository, SetVersionRepository setVersionRepository,
        UserGroupRepository userGroupRepository) {
        VehicleConfig config = new VehicleConfig();
        config.setName(name);
        setUpTimeStamp.accept(config, timestamp);
        config.setValidDate(Date.valueOf(LocalDate.now()));
        config.setVehicleProject(createVehicleProject(name, vehicleProjectRepository));
        config.setResource(createResource());
        config.setSetVersionId(createSetVersion(name, setVersionRepository).getId());
        config.setCostGroupVersion(1L);
        UserGroup userGroup = createUserGroup(name, userGroupRepository);
        userGroup.getVehicleConfigs().add(config);
        config.setOwnerGroup(userGroup);
        config.setVehiclePartList(partList);

        return vehicleConfigRepository.save(config);
    }

    private VehicleProject createVehicleProject(String name, VehicleProjectRepository vehicleProjectRepository) {
        VehicleProject project = new VehicleProject();
        project.setProjectName("VEHICLE_PROJECT_" + name);
        project.setBrandCode(Brand.AU);
        project.setSalesKey("SALES_KEY");
        project.setDescription("desc");
        project.setPlatform("platform");

        return vehicleProjectRepository.save(project);
    }

    private Resource createResource() {
        Resource resource = new Resource();
        resource.setChange("Test-User-ID");
        resource.setType(ResourceType.FZG_KONFIG);

        return resource;
    }

    private SetVersion createSetVersion(String name, SetVersionRepository setVersionRepository) {
        SetVersion setVersion = new SetVersion();
        setVersion.setName("SET_VERSION_" + name);
        setVersion.setTimestampCreate(Timestamp.valueOf(LocalDateTime.now()));
        setVersion.setUserCreate("Test-User-ID");

        return setVersionRepository.save(setVersion);
    }

    private UserGroup createUserGroup(String name, UserGroupRepository userGroupRepository) {
        User user = userRepository.findById("Test-User-ID").orElseThrow();
        UserGroup userGroup = new UserGroup();
        userGroup.setBrand(Brand.AU.getBrandName());
        userGroup.setName("USER_GROUP_" + name);
        userGroup.setWriteAccess(true);
        userGroup.setUserCreate("USER");
        userGroup.setUserCreate(user.getId());
        userGroup.setUsers(List.of(user));
        userGroup.setTimestampCreate(Timestamp.valueOf(LocalDateTime.now()));

        userGroup = userGroupRepository.save(userGroup);
        user.getUserGroups().add(userGroup);
        userRepository.save(user);

        return userGroup;
    }

    private EfsElementMaraHistory addEfsElementMaraHistory(EfsElementMara efsElementMara, Long partListId,
        EfsElementMaraHistoryRepository efsElementMaraHistoryRepository) {
        EfsElementMaraHistory efsElementMaraHistory = new EfsElementMaraHistory();
        efsElementMaraHistory.setDescription1De("desc");
        efsElementMaraHistory.setRevision(1L);
        efsElementMaraHistory.setPartNumber("AAA");
        efsElementMaraHistory.setEfsElementMara(efsElementMara);
        efsElementMaraHistory.setVehiclePartListId(partListId);

        return efsElementMaraHistoryRepository.save(efsElementMaraHistory);
    }

    private EfsElementAggregateMapping addEfsElementAggregateMapping(EfsElement efsElement,
        EfsElementAggregateMappingRepository efsElementAggregateMappingRepository) {
        EfsElementAggregateMapping efsElementAggregateMapping = new EfsElementAggregateMapping();
        efsElementAggregateMapping.setEfsElementId(efsElement.getId());
        efsElementAggregateMapping.setProductDataId("ID");

        return efsElementAggregateMappingRepository.save(efsElementAggregateMapping);
    }

    private FilteredOutEfsElement addFilteredOutEfsElements(VehicleConfig config, EfsElementMara mara,
        FilteredOutEfsElementRepository filteredOutEfsElementRepository) {
        FilteredOutEfsElement filteredOutEfsElement = new FilteredOutEfsElement();
        filteredOutEfsElement.setQuantity(1);
        filteredOutEfsElement.setQuantityUnit("KG");
        filteredOutEfsElement.setRevision(1L);
        filteredOutEfsElement.setGap(1);
        filteredOutEfsElement.setEfsElementMara(mara);
        filteredOutEfsElement.setVehicleConfigId(config.getId());

        return filteredOutEfsElementRepository.save(filteredOutEfsElement);
    }

    private UserMessage addUserMessage(VehicleConfig config, UserMessageRepository userMessageRepository) {
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId("Test-User-ID");
        userMessage.setMessage("msg");
        userMessage.setVehicleConfigId(config.getId());
        userMessage.setCreated(Date.valueOf(LocalDate.now()));
        userMessage.setType(UserMessageType.PART_LIST_READY);

        return userMessageRepository.save(userMessage);
    }

    private VehiclePartList addVehiclePartList(VehiclePartListRepository vehiclePartListRepository) {
        VehiclePartList partList = new VehiclePartList();
        partList.setProductKeyVehicle("A");
        partList.setRevision(1L);

        return vehiclePartListRepository.save(partList);
    }

    private EfsElementMara addEfsElementMara(Long partListId, EfsElementMaraRepository efsElementMaraRepository) {
        EfsElementMara efsElementMara = new EfsElementMara();
        efsElementMara.setDescription1De("desc");
        efsElementMara.setRevision(1L);
        efsElementMara.setPartNumber("AAA");
        efsElementMara.setPartNumberIndex("");
        efsElementMara.setVehiclePartListId(partListId);
        efsElementMara.setPartNumberVornummer("");
        efsElementMara.setPartNumberMittelgruppe("");
        efsElementMara.setPartNumberEndNumber("");
        efsElementMara.setEntityChange(Boolean.TRUE);

        return efsElementMaraRepository.save(efsElementMara);
    }

    private EfsElement addEfsElement(Long partListId, EfsElementMara mara, EfsElementRepository efsElementRepository) {
        EfsElement efsElement = new EfsElement();
        efsElement.setQuantity(1);
        efsElement.setQuantityUnit("KG");
        efsElement.setRevision(1L);
        efsElement.setGap(1);
        efsElement.setEfsElementMara(mara);
        efsElement.setVehiclePartListId(partListId);

        return efsElementRepository.save(efsElement);
    }

    private void addToRecentlyUser(VehicleConfig config, UserPropertyManager userPropertyManager) {
        User user = userRepository.findByIdIgnoreCase("Test-User-ID");
        userPropertyManager.save(user, PropertyType.RECENTLY_USED, config.getId().toString());
    }

    public void setStatusDeletable(Status status) {
        getConfigDeletable().setStatus(status);
        vehicleConfigRepository.save(getConfigDeletable());
    }

    public void setStatusNonDeletable(Status status) {
        getConfigNonDeletable().setStatus(status);
        vehicleConfigRepository.save(getConfigNonDeletable());
    }

    public void assertDeletableNotInDatabase() {
        assertFalse(isVehicleConfigFullyDeleted(configDeletable, efsElementMaraDeletable, efsElementDeletable,
            efsElementMaraHistoryDeletable, efsElementAggregateMappingDeletable, filteredOutEfsElementDeletable,
            userMessageDeletable, Optional::isPresent));
    }

    public void assertDeletableInDatabase() {
        assertFalse(isVehicleConfigFullyDeleted(configDeletable, efsElementMaraDeletable, efsElementDeletable,
            efsElementMaraHistoryDeletable, efsElementAggregateMappingDeletable, filteredOutEfsElementDeletable,
            userMessageDeletable, Optional::isEmpty));
    }

    public void assertNonDeletableDataInDatabase() {
        assertFalse(isVehicleConfigFullyDeleted(configNonDeletable, efsElementMaraNonDeletable, efsElementNonDeletable,
            efsElementMaraHistoryNonDeletable, efsElementAggregateMappingNonDeletable,
            filteredOutEfsElementNonDeletable, userMessageNonDeletable, Optional::isEmpty));
    }

    private boolean isVehicleConfigFullyDeleted(VehicleConfig vehicleConfig, EfsElementMara efsElementMara,
        EfsElement efsElement, EfsElementMaraHistory efsElementMaraHistory,
        EfsElementAggregateMapping efsElementAggregateMapping, FilteredOutEfsElement filteredOutEfsElement,
        UserMessage userMessage, Predicate<Optional<?>> predicate) {
        return predicate.test(vehiclePartListRepository.findById(vehicleConfig.getVehiclePartList().getId()))
            && predicate.test(efsElementMaraRepository.findById(efsElementMara.getId())) && predicate.test(
            efsElementRepository.findById(efsElement.getId())) && predicate.test(
            efsElementMaraHistoryRepository.findById(efsElementMaraHistory.getId())) && predicate.test(
            efsElementAggregateMappingRepository.findById(efsElementAggregateMapping.getEfsElementId()))
            //this should be empty
            && predicate.test(vehicleConfigCategoryStatusRepository.findAllById(
                vehicleConfig.getVehicleConfigCategoryStatus().stream().map(VehicleConfigCategoryStatus::getId).toList())
            .stream().findFirst()) && predicate.test(
            filteredOutEfsElementRepository.findById(filteredOutEfsElement.getId())) && predicate.test(
            userMessageRepository.findById(userMessage.getId()));
    }

    public VehicleConfig getConfigDeletable() {
        return configDeletable;
    }

    public VehicleConfig getConfigNonDeletable() {
        return configNonDeletable;
    }
}
