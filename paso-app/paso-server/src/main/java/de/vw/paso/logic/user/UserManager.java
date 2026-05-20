package de.vw.paso.logic.user;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.vw.paso.exception.AdminPermissionException;
import de.vw.paso.logic.activitylog.AdminActivityLogManager;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.right.Role;
import de.vw.paso.service.idplogin.IdpService;
import de.vw.paso.stage.Stage;
import de.vw.paso.tableconfig.TableConfig;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.user.domain.UserProperty;
import de.vw.paso.util.RequestData;
import de.vw.paso.utility.StringConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManager {

    public static final int MAX_INACTIVE_DAYS_LIMIT = 180;
    public static final int MAX_DAYS_LIMIT_TO_DELETE_SETTINGS = 360;
    public static final String SYSTEM_ADMIN = "EOSTESI";
    public static final int MAX_DAYS_WITHOUT_ROLES = 30;
    public static final int MAX_DAYS_FROM_LAST_LOGIN = 90;
    public static final int MAX_RETENTION_DAYS_WITHOUT_ROLES = 731;
    public static final int MIN_COST_CENTER_CHANGE = 30;
    public static final String SYSTEM = "System";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String stage;

    private final UserRepository userRepository;
    private final IdpService idpService;
    private final UserPropertyManager userPropertyManager;
    private final AdminActivityLogManager adminActivityLogManager;

    public UserManager(UserRepository userRepository, IdpService idpService, UserPropertyManager userPropertyManager,
            AdminActivityLogManager adminActivityLogManager, @Value("${stage}") String stage) {
        this.userRepository = userRepository;
        this.idpService = idpService;
        this.userPropertyManager = userPropertyManager;
        this.adminActivityLogManager = adminActivityLogManager;
        this.stage = stage;
    }

    public void changeUserDefaultTableConfig(TableConfig tableConfig) {
        User user = getCurrentUser();
        if (tableConfig == null) {
            userPropertyManager.deleteUserPropertyType(user, PropertyType.DEFAULT_TABLE_CONFIG);
            return;
        }

        UserProperty userProperty = userPropertyManager.getUserPropertyByUserId(user,
                PropertyType.DEFAULT_TABLE_CONFIG);
        if (userProperty == null) {
            userProperty = new UserProperty();
            userProperty.setUser(user);
            userProperty.setType(PropertyType.DEFAULT_TABLE_CONFIG);
            userProperty.setChange(user.getId());
        }

        userProperty.setUserData(tableConfig.getId().toString());
        userPropertyManager.save(userProperty);
    }

    public String createInactivityInfo(User user) {
        boolean isActive = user.getActive();
        Timestamp costCenterChangedAt = user.getCostCenterChangedAt();
        if (isActive && costCenterChangedAt != null) {
            return "Cost center changed. User will be deactivated in " + (MIN_COST_CENTER_CHANGE
                    - getNumberOfDaysOfCostCenterChange(costCenterChangedAt)) + " days.";
        }

        if (isActive) {
            return StringConstant.EMPTY;
        }

        if (costCenterChangedAt != null) {
            return "Deactivated due to cost center change.";
        }

        String lastLoginDate = userPropertyManager.getLastLoginDate(user);
        if (lastLoginDate != null
                && getDifferenceOfNumberOfDays(lastLoginDate, LocalDate.now()) > MAX_INACTIVE_DAYS_LIMIT) {
            return "Deactivated due to long inactivity.";
        }

        return "Deactivated by Admin.";
    }

    public void disableUser(String id) {
        User user = getUser(id);
        if (user != null && !getCurrentUserId().equals(id) && !id.equals(SYSTEM_ADMIN) && isCurrentUserAdmin()) {
            Collection<Role> roles = user.getRoles();
            user.getRoles().removeAll(roles);

            setNewUser(user, getUser(id), false, id);
        }
    }

    public void enableUser(String id) {
        User user = getUser(id);
        if (user != null && isCurrentUserAdmin()) {
            setNewUser(user, user, true, id);
        }
    }

    public Collection<User> getAllActiveUsers() {
        return isCurrentUserAdmin() ? userRepository.findAllByActive(true) : List.of();
    }

    public Collection<User> getAllUser() {
        return isCurrentUserAdmin() ? userRepository.findAll() : List.of();
    }

    public Collection<User> getAllUsersForNotification() {
        return userRepository.findAll();
    }

    public String getCurrentUserId() {
        return RequestData.getRequestData(RequestDataKey.USERID);
    }

    public User getUser(String id) {
        return userRepository.findByIdIgnoreCase(id);
    }

    public User getCurrentUser() {
        return userRepository.findByIdIgnoreCase(getCurrentUserId());
    }

    public User getUserFromIdp(String code) {
        User userInfo = userRepository.findByIdIgnoreCase(code);
        userInfo.setPreferredLanguage("en");

        return createOrUpdateUser(userInfo);
    }

    public Collection<UserGroup> getUserGroups(String userId) {
        User user = getUser(userId);
        return user.getUserGroups();
    }

    public User getUserWithRoles(String id) {
        return userRepository.getUserWithRoles(id);
    }

    public Collection<User> getUsersById(Collection<String> userIds) {
        if (isCurrentUserAdmin()) {
            return userRepository.findAllById(userIds);
        }
        return List.of();
    }

    public Collection<UserGroup> getUsersGroupWrite(String userId) {
        Collection<UserGroup> userGroups = getUserGroups(userId);
        return userGroups.stream().filter(UserGroup::isWriteAccess).toList();
    }

    /** @return vehicle config in {@link Map} for current user */
    public Map<Long, Boolean> getVehicleConfigIdsWithAccess() {
        Stream<UserGroup> userGroupStream = getUserGroups(getCurrentUserId()).stream();

        return createVehicleConfigMapFromStream(userGroupStream);
    }

    public Map<Long, Boolean> getVehicleConfigIdsWithWriteAccess() {
        Stream<UserGroup> userGroupStream = getUsersGroupWrite(getCurrentUserId()).stream();

        return createVehicleConfigMapFromStream(userGroupStream);
    }

    @Transactional
    public void inactiveTestersLastLoginCleanup() {
        RequestData.setRequestData(RequestDataKey.USERID, SYSTEM_ADMIN);
        Collection<User> allUser = userRepository.findAll();
        LocalDate currentDate = LocalDate.now();
        for (User user : allUser) {
            boolean isDevOrQa = stage.equalsIgnoreCase(Stage.DEV.name()) || stage.equalsIgnoreCase(Stage.QA.name());
            if (!isDevOrQa || user.isAdmin()) {
                continue;
            }

            String lastLoginDate = getUserLoginOrChangeTime(user);
            long numberOfDaysSinceLastLogin = getDifferenceOfNumberOfDays(lastLoginDate, currentDate);
            if (numberOfDaysSinceLastLogin >= MAX_DAYS_FROM_LAST_LOGIN) {
                deleteUserIncludingProperties(user);
            }
        }
    }

    @Transactional
    public void deleteInactiveNonAdminUser() {
        RequestData.setRequestData(RequestDataKey.USERID, SYSTEM_ADMIN);
        Collection<User> allUser = userRepository.findAll();
        LocalDate currentDate = LocalDate.now();
        for (User user : allUser) {
            if (user.isAdmin()) {
                continue;
            }

            String lastLoginDate = getUserLoginOrChangeTime(user);
            if (!lastLoginDate.isEmpty()) {
                long numberOfDaysSinceLastLogin = getDifferenceOfNumberOfDays(lastLoginDate, currentDate);
                if (!user.getActive() && numberOfDaysSinceLastLogin >= MAX_DAYS_LIMIT_TO_DELETE_SETTINGS) {
                    userPropertyManager.deleteAllPropertiesByUserId(user, SYSTEM);
                    deleteAllUserProperties(user);
                    continue;
                }

                deactivateUser(user, numberOfDaysSinceLastLogin);
            }

            Timestamp costCenterChangedAt = user.getCostCenterChangedAt();
            if (costCenterChangedAt == null) {
                continue;
            }

            boolean outOfCostCenterChange =
                    getNumberOfDaysSinceCostCenterChange(costCenterChangedAt, currentDate) > MIN_COST_CENTER_CHANGE;
            if (outOfCostCenterChange) {
                user.setActive(false);
                adminActivityLogManager.logUserActive(SYSTEM, List.of(user.getId()), false);
            }
        }
    }

    @Transactional
    public boolean isConfigDefault(Long id) {
        if (id == null) {
            return false;
        }

        UserProperty property = userPropertyManager.getUserPropertyByUserId(getUser(getCurrentUserId()),
                PropertyType.DEFAULT_TABLE_CONFIG);
        return property != null && property.getUserData().equals(id.toString());
    }

    public boolean isCurrentUserAdmin() {
        return getUser(getCurrentUserId()).isAdmin();
    }

    public void resetCostCenterChangedAt(String userId) {
        if (getUser(getCurrentUserId()).isAdmin()) {
            User user = getUser(userId);
            user.setCostCenterChangedAt(null);
            saveUser(user);
        }
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void usersWithOutRolesCleanup() {
        RequestData.setRequestData(RequestDataKey.USERID, SYSTEM_ADMIN);
        Collection<User> allUser = userRepository.findAll();
        LocalDate currentDate = LocalDate.now();
        for (User user : allUser) {
            Collection<Role> roles = user.getRoles();
            if (!roles.isEmpty()) {
                continue;
            }

            String lastLoginDate = getUserLoginOrChangeTime(user);
            long numberOfDaysSinceLastLogin = getDifferenceOfNumberOfDays(lastLoginDate, currentDate);
            UserProperty userPropertyNoRoleSince = userPropertyManager.getUserPropertyByUserId(user,
                    PropertyType.NO_ROLE_SINCE);
            if (userPropertyNoRoleSince == null && numberOfDaysSinceLastLogin >= MAX_DAYS_WITHOUT_ROLES) {
                deleteUserIncludingProperties(user);
            } else if (userPropertyNoRoleSince != null
                    && getDifferenceOfNumberOfDays(userPropertyNoRoleSince.getUserData(), currentDate)
                    > MAX_RETENTION_DAYS_WITHOUT_ROLES) {
                deleteUserIncludingProperties(user);
            }
        }
    }

    public void requireAdminUser() {
        if (!isCurrentUserAdmin()) {
            throw new AdminPermissionException();
        }
    }

    private long getDifferenceOfNumberOfDays(String lastLoginDate, LocalDate currentDate) {
        LocalDate lastLogin = LocalDate.parse(lastLoginDate, DateTimeFormatter.ISO_DATE);
        return ChronoUnit.DAYS.between(lastLogin, currentDate);
    }

    private long getNumberOfDaysOfCostCenterChange(Timestamp costCenterChangedAt) {
        return ChronoUnit.DAYS.between(costCenterChangedAt.toLocalDateTime().toLocalDate(), LocalDate.now());
    }

    private long getNumberOfDaysSinceCostCenterChange(Timestamp lastLoginDate, LocalDate currentDate) {
        return ChronoUnit.DAYS.between(lastLoginDate.toLocalDateTime().toLocalDate(), currentDate);
    }

    private User createOrUpdateUser(User user) {
        User existingUser = userRepository.findByIdIgnoreCase(user.getId());

        if (existingUser == null) {
            existingUser = user;
            existingUser.setChange("USER_SERVICE");
            existingUser.setActive(true);
            User savedUser = userRepository.save(existingUser);
            userPropertyManager.saveOrUpdate(savedUser, PropertyType.LAST_LOGIN, LocalDate.now().toString());
            userPropertyManager.saveNew(user, PropertyType.PREFERRED_LANGUAGE, user.getPreferredLanguage());

            return savedUser;
        } else if (existingUser.hasChanges(user)) {
            existingUser.setChange("USER_SERVICE");
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser = userRepository.save(existingUser);
        }

        if (existingUser.costCenterChanged(user)) {
            if (!existingUser.getCostCenter().equals("0")) {
                existingUser.setCostCenterChangedAt(new Timestamp(System.currentTimeMillis()));
            }

            existingUser.setCostCenter(user.getCostCenter());
            existingUser = userRepository.save(existingUser);
        }

        if (!existingUser.getRoles().isEmpty()) {
            userPropertyManager.saveOrUpdate(existingUser, PropertyType.LAST_LOGIN, LocalDate.now().toString());
        }

        userPropertyManager.saveNew(user, PropertyType.PREFERRED_LANGUAGE, user.getPreferredLanguage());
        return existingUser;
    }

    private Map<Long, Boolean> createVehicleConfigMapFromStream(Stream<UserGroup> stream) {
        return stream.flatMap(userGroup -> Stream.concat(userGroup.getOwnedVehicleConfigs().stream(),
                                userGroup.getVehicleConfigs().stream())
                        .map(vehicleConfig -> Map.entry(vehicleConfig.getId(), userGroup.isWriteAccess())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1 || v2));
    }

    private void deactivateUser(User user, long numberOfDaysSinceLastLogin) {
        if (user.getActive() && numberOfDaysSinceLastLogin >= MAX_INACTIVE_DAYS_LIMIT) {
            user.setActive(false);
            saveUser(user);
            userPropertyManager.clearPropertiesByUserId(user, SYSTEM);
            adminActivityLogManager.logUserSettingDeletion(SYSTEM, List.of(user.getId()));
            adminActivityLogManager.logUserActive(SYSTEM, List.of(user.getId()), false);
        }
    }

    private void deleteUserIncludingProperties(User user) {
        userPropertyManager.deleteAllPropertiesByUserId(user, SYSTEM);
        adminActivityLogManager.logUserSettingDeletion(SYSTEM, List.of(user.getId()));
        deleteAllUserProperties(user);
    }

    private void deleteAllUserProperties(User user) {
        adminActivityLogManager.logUserSettingDeletion(SYSTEM, List.of(user.getId()));
        Collection<UserGroup> userGroups = user.getUserGroups();
        for (UserGroup userGroup : userGroups) {
            userGroup.getUsers().remove(user);
        }

        userRepository.deleteById(user.getId());
    }

    private void setNewUser(User newUser, User currentUser, boolean active, String id) {
        userPropertyManager.deleteUserPropertyType(currentUser, PropertyType.LAST_LOGIN);
        adminActivityLogManager.logUserActive(getCurrentUserId(), List.of(id), active);

        newUser.setActive(active);
        newUser.setChange(id);
        newUser.setCostCenterChangedAt(null);
        userRepository.save(newUser);
    }

    private String getUserLoginOrChangeTime(User user) {
        String lastLoginDate = userPropertyManager.getLastLoginDate(user);
        if (lastLoginDate == null) {
            lastLoginDate = user.getTimestampChange().toInstant().atZone(ZoneId.systemDefault()).format(DATE_FORMATTER);
        }

        return lastLoginDate;
    }
}
