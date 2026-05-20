package de.vw.paso.logic.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.vw.paso.logic.activitylog.AdminActivityLogManager;
import de.vw.paso.mapper.VehicleConfigMapper;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserGroupManager {

    private final UserGroupRepository userGroupRepository;
    private final AdminActivityLogManager adminActivityLogManager;
    private final UserManager userManager;
    private final VehicleConfigRepository vehicleConfigRepository;

    public UserGroupManager(UserGroupRepository userGroupRepository, AdminActivityLogManager adminActivityLogManager,
            UserManager userManager, VehicleConfigRepository vehicleConfigRepository) {
        this.userGroupRepository = userGroupRepository;
        this.adminActivityLogManager = adminActivityLogManager;
        this.userManager = userManager;
        this.vehicleConfigRepository = vehicleConfigRepository;
    }

    @Transactional
    public void saveUserGroup(UserGroup userGroup) {
        userManager.requireAdminUser();

        if (userGroup.getId() == null) {
            UserGroup savedUserGroup = userGroupRepository.saveAndFlush(userGroup);
            adminActivityLogManager.logNewUserGroupCreation(userManager.getCurrentUserId(), savedUserGroup.getId(),
                    userGroup.getName(), userGroup.getBrand(), userGroup.isWriteAccess());
            return;
        }

        UserGroup existingUserGroup = userGroupRepository.findById(userGroup.getId()).orElseThrow();
        logUserGroupChanges(userGroup, existingUserGroup);
        userGroupRepository.save(userGroup);
    }

    public List<UserGroup> getAllUserGroups() {
        return userGroupRepository.findAll();
    }

    public List<VehicleConfigDTO> getVehicleConfigs(Long userGroupId) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId).orElse(new UserGroup());
        return userGroup.getVehicleConfigs().stream().filter(config -> config.getDeletionDate() == null)
                .map(config -> VehicleConfigMapper.toDto(config, userGroup.isWriteAccess())).toList();
    }

    public List<User> getGroupUsers(Long userGroupId) {
        return userGroupRepository.findById(userGroupId).orElse(new UserGroup()).getUsers();
    }

    @Transactional
    public void addVehicleConfigToUserGroup(Long userGroupId, Long vehicleConfigId) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow();
        Collection<String> usersIds = userGroup.getUsers().stream().map(User::getId).toList();
        if (usersIds.contains(userManager.getCurrentUserId())) {
            VehicleConfig vehicleConfig = vehicleConfigRepository.findById(vehicleConfigId).orElseThrow();
            userGroup.getVehicleConfigs().add(vehicleConfig);
            vehicleConfig.getUserGroups().add(userGroup);
        }
    }

    private void logUserGroupChanges(UserGroup userGroup, UserGroup existingUserGroup) {
        Collection<String> existingUserIds = getUserIds(existingUserGroup);
        Collection<String> removedUserIds = getUserIds(existingUserGroup);
        Collection<String> addedUserIds = getUserIds(userGroup);
        removedUserIds.removeAll(addedUserIds);
        addedUserIds.removeAll(existingUserIds);

        Collection<String> existingVehicleConfigsInfo = getVehicleConfigsInfo(existingUserGroup);
        Collection<String> removedVehicleConfigsInfo = getVehicleConfigsInfo(existingUserGroup);
        Collection<String> addedVehicleConfigsInfo = getVehicleConfigsInfo(userGroup);
        removedVehicleConfigsInfo.removeAll(addedVehicleConfigsInfo);
        addedVehicleConfigsInfo.removeAll(existingVehicleConfigsInfo);

        String userGroupData =
                "userGroupId: " + userGroup.getId().toString() + StringConstant.COMMA_SPACE + "userGroupName: "
                        + userGroup.getName();
        StringBuilder logMessage = buildLogMessage(userGroup, existingUserGroup, removedUserIds, addedUserIds,
                removedVehicleConfigsInfo, addedVehicleConfigsInfo, userGroupData);
        adminActivityLogManager.logUserGroupChange(userManager.getCurrentUserId(), logMessage.toString());
    }

    private StringBuilder buildLogMessage(UserGroup userGroup, UserGroup existingUserGroup,
            Collection<String> removedUserIds, Collection<String> addedUserIds,
            Collection<String> removedVehicleConfigsInfo, Collection<String> addedVehicleConfigsInfo,
            String userGroupData) {
        StringBuilder logMessage = new StringBuilder("Changed " + userGroupData);
        if (!addedUserIds.isEmpty()) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Added Users: ").append(addedUserIds);
        }

        if (!removedUserIds.isEmpty()) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Removed Users: ").append(removedUserIds);
        }

        if (!addedVehicleConfigsInfo.isEmpty()) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Added Vehicle Configs: ")
                    .append(addedVehicleConfigsInfo);
        }

        if (!removedVehicleConfigsInfo.isEmpty()) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Removed Vehicle Configs: ")
                    .append(removedVehicleConfigsInfo);
        }

        if (!Objects.equals(existingUserGroup.getName(), userGroup.getName())) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Name has changed from ")
                    .append(existingUserGroup.getName()).append(" to ").append(userGroup.getName());
        }

        if (!Objects.equals(existingUserGroup.getBrand(), userGroup.getBrand())) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Brand has changed from ")
                    .append(existingUserGroup.getBrand()).append(" to ").append(userGroup.getBrand());
        }

        if (existingUserGroup.isWriteAccess() != userGroup.isWriteAccess()) {
            logMessage.append(StringConstant.COMMA_SPACE).append("Write access changed to: ")
                    .append(userGroup.isWriteAccess());
        }

        return logMessage;
    }

    private Collection<String> getVehicleConfigsInfo(UserGroup existingUserGroup) {
        return existingUserGroup.getVehicleConfigs().stream()
                .map(vc -> "id: " + vc.getId() + StringConstant.COMMA_SPACE + "Name: " + vc.getName())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Collection<String> getUserIds(UserGroup existingUserGroup) {
        return existingUserGroup.getUsers().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
    }
}