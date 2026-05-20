package de.vw.paso.logic.user;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.vw.paso.logic.activitylog.AdminActivityLogManager;
import de.vw.paso.repository.user.UserPropertyRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserProperty;
import de.vw.paso.util.RequestData;
import de.vw.paso.util.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPropertyManager {

    private final UserPropertyRepository userPropertyRepository;
    private final AdminActivityLogManager adminActivityLogManager;
    private final UserRepository userRepository;

    public UserPropertyManager(UserPropertyRepository userPropertyRepository,
            AdminActivityLogManager adminActivityLogManager, UserRepository userRepository) {
        this.userPropertyRepository = userPropertyRepository;
        this.adminActivityLogManager = adminActivityLogManager;
        this.userRepository = userRepository;
    }

    @Value("${recently-used.vehicle-configurations.limit}")
    private int recentlyUsedVehicleConfigurationLimit;

    @Value("${recently-used.vehicle-configurations.expiry-days}")
    private int recentlyUsedVehicleConfigurationExpiryDays;

    public UserProperty save(User user, PropertyType type, String userData) {
        checkIfUserIsNull(user);
        UserProperty userProperty = createUserProperty(user, type);
        userProperty.setUserData(userData);

        return save(userProperty);
    }

    public UserProperty saveOrUpdate(User user, PropertyType type, String userData) {
        checkIfUserIsNull(user);
        UserProperty userProperty = getOrCreateUserPropertyByUserId(user, type);
        userProperty.setUserData(userData);
        userProperty.setTimestampChange(Timestamp.from(Instant.now()));

        return save(userProperty);
    }

    public UserProperty getOrCreateUserPropertyByUserId(User user, PropertyType type) {
        checkIfUserIsNull(user);
        return userPropertyRepository.getByUserAndType(user, type).orElseGet(() -> createUserProperty(user, type));
    }

    public UserProperty getUserPropertyByUserId(User user, PropertyType type) {
        checkIfUserIsNull(user);
        return userPropertyRepository.getByUserAndType(user, type).orElse(null);
    }

    public void saveNew(User user, PropertyType type, String userData) {
        checkIfUserIsNull(user);
        userPropertyRepository.getByUserAndType(user, type).orElseGet(() -> {
            UserProperty newUserProperty = createUserProperty(user, type);
            newUserProperty.setUserData(userData);
            newUserProperty.setTimestampChange(Timestamp.from(Instant.now()));
            save(newUserProperty);
            return newUserProperty;
        });
    }

    public String getLastLoginDate(User user) {
        checkIfUserIsNull(user);
        Optional<UserProperty> lastLogIn = userPropertyRepository.getByUserAndType(user, PropertyType.LAST_LOGIN);
        return lastLogIn.map(UserProperty::getUserData).orElse(null);
    }

    private UserProperty createUserProperty(User user, PropertyType type) {
        checkIfUserIsNull(user);
        UserProperty userProperty = new UserProperty();

        userProperty.setUser(user);
        userProperty.setType(type);
        userProperty.setChange(user.getId());
        return userProperty;
    }

    @Transactional
    public UserProperty saveRecentlyUsed(User user, PropertyType type, String userData) {
        checkIfUserIsNull(user);
        UserProperty savedProperty = userPropertyRepository.findByUserAndTypeAndUserData(user, type, userData);

        if (savedProperty != null) {
            savedProperty.setChange(user.getId());
        } else {
            savedProperty = save(user, type, userData);
        }

        if (userPropertyRepository.countByUserAndType(user, type) > recentlyUsedVehicleConfigurationLimit) {
            userPropertyRepository.deleteById(
                    userPropertyRepository.findFirstByTypeOrderByTimestampChange(type).getId());
        }

        return savedProperty;
    }

    public UserProperty save(UserProperty userProperty) {
        return userPropertyRepository.save(userProperty);
    }

    public void clearPropertiesByUserId(User user, String currentUserId) {
        checkIfUserIsNull(user);
        String logMessage = "Data of " + user.getId() + " has been deleted.";
        Optional<UserProperty> lastLoginPropertyOpt = userPropertyRepository.getByUserAndType(user,
                PropertyType.LAST_LOGIN);
        if (lastLoginPropertyOpt.isEmpty()) {
            return;
        }

        adminActivityLogManager.logUserDataDeletion(currentUserId, logMessage);
        userPropertyRepository.deleteByUser(user);

        UserProperty userPropertyToSave = new UserProperty();
        UserProperty lastLoginProperty = lastLoginPropertyOpt.get();
        userPropertyToSave.setUser(lastLoginProperty.getUser());
        userPropertyToSave.setType(lastLoginProperty.getType());
        userPropertyToSave.setUserData(lastLoginProperty.getUserData());
        userPropertyToSave.setChange(currentUserId);

        save(userPropertyToSave);
    }

    public void deleteAllPropertiesByUserId(User user, String currentUserId) {
        checkIfUserIsNull(user);
        String logMessage = "Data of " + user.getId() + " has been deleted.";
        adminActivityLogManager.logUserDataDeletion(currentUserId, logMessage);
        userPropertyRepository.deleteByUser(user);
    }

    public void deleteUserPropertyType(User user, PropertyType type) {
        checkIfUserIsNull(user);
        userPropertyRepository.deleteByUserAndType(user, type);
    }

    public Long delete(User user, PropertyType type, Object userData) {
        checkIfUserIsNull(user);
        return userPropertyRepository.deleteByUserAndTypeAndUserData(user, type, userData.toString());
    }

    public Long delete(PropertyType type, Object userData) {
        return userPropertyRepository.deleteByTypeAndUserData(type, userData.toString());
    }

    public List<Long> getFavoriteVehicleProjectIds(User user) {
        checkIfUserIsNull(user);
        return getMyConfigurationPropertyIds(user, PropertyType.FAVORITE_PROJECTS);
    }

    public List<Long> getRecentlyUsedVehicleProjectIds(User user) {
        checkIfUserIsNull(user);
        return getMyConfigurationPropertyIds(user, PropertyType.RECENTLY_USED);
    }

    public int deleteExpiredRecentlyUsedVehicleConfigurations() {
        Date date = Date.valueOf(LocalDate.now().minusDays(recentlyUsedVehicleConfigurationExpiryDays));

        return userPropertyRepository.deleteByTimestampChangeBefore(date);
    }

    private List<Long> getMyConfigurationPropertyIds(User user, PropertyType type) {
        checkIfUserIsNull(user);
        List<UserProperty> userProperties = userPropertyRepository.findAllByUserAndType(user, type);

        userProperties.sort(Comparator.comparing(UserProperty::getTimestampChange));

        return userProperties.stream().map(e -> Long.valueOf(e.getUserData())).collect(Collectors.toList());
    }

    public UserProperty load(User user, PropertyType type) {
        checkIfUserIsNull(user);
        UserProperty defaultUserProperty = new UserProperty();
        defaultUserProperty.setUser(user);
        defaultUserProperty.setType(type);

        return userPropertyRepository.findAllByUserAndType(user, type).stream()
                .max(Comparator.comparing(UserProperty::getTimestampChange)).orElse(defaultUserProperty);
    }

    public String getCurrentUserLanguage() {
        User currentUser = userRepository.findByIdIgnoreCase(getCurrentUserId());
        return userPropertyRepository.getByUserAndType(currentUser, PropertyType.PREFERRED_LANGUAGE).orElseThrow()
                .getUserData();
    }

    private String getCurrentUserId() {
        return RequestData.getRequestData(RequestDataKey.USERID);
    }

    private void checkIfUserIsNull(User user) {
        if (user == null) {
            throw new UnauthorizedException("No active user is currently logged in");
        }
    }
}
