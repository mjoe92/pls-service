package de.vw.paso.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.repository.user.UserPropertyRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.service.right.AddRolesToUserDTO;
import de.vw.paso.service.right.RightManagementRestController;
import de.vw.paso.stage.Stage;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RecentlyUsedCleanupSchedulerTest extends AbstractServiceTests {

    @Autowired
    private UserPropertyRepository userPropertyRepository;
    @Autowired
    private UserPropertyManager userPropertyManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RightManagementRestController rightManagementRestController;
    @Autowired
    private UserManager userManager;

    @Value("${stage}")
    private String stage;

    @Test
    public void cleanupInactiveTestersSinceLastLoginOver90Days() {
        LocalDate userCreationDateInPast = LocalDate.now().minusDays(91);
        User user = createUserForDeletionTest(userCreationDateInPast);

        userManager.inactiveTestersLastLoginCleanup();

        Collection<UserProperty> allUserProperties = userPropertyRepository.getByUser(user);

        boolean isNotLocal = !Stage.LOCAL.name().equalsIgnoreCase(stage);
        assertEquals(isNotLocal, allUserProperties.isEmpty(), "User properties not null");
        assertEquals(isNotLocal, userRepository.findById(user.getId()).isEmpty(), "User not found");
    }

    @Test
    public void cleanupInactiveTestersSinceLastLoginUnder90Days() {
        LocalDate userCreationDateInPastMonth = LocalDate.now().minusDays(90);

        User user = createUserForDeletionTest(userCreationDateInPastMonth);

        userManager.deleteInactiveNonAdminUser();

        Collection<UserProperty> allUserProperties = userPropertyRepository.getByUser(user);

        assertFalse(allUserProperties.isEmpty(), "User properties not null");
        assertTrue(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void deleteUserAfterRetentionPeriodWithNoRolesAfterTwoYears() {
        LocalDate userCreationDateInPastMonth = LocalDate.now().minusDays(732);

        User user = createUserForRetentionTest(userCreationDateInPastMonth);

        userManager.usersWithOutRolesCleanup();

        Collection<UserProperty> allUserProperties = userPropertyRepository.getByUser(user);

        assertTrue(allUserProperties.isEmpty(), "No user properties");
        assertThrows(JpaObjectRetrievalFailureException.class, () -> userRepository.getReferenceById(user.getId()));
    }

    @Test
    public void deleteUserWithNoRolesSinceCreation() {
        LocalDate userCreationDateInPastMonth = LocalDate.now().minusDays(31);

        User user = createUserForDeletionTest(userCreationDateInPastMonth);

        userManager.usersWithOutRolesCleanup();

        Collection<UserProperty> allUserProperties = userPropertyRepository.getByUser(user);

        assertTrue(allUserProperties.isEmpty(), "No user properties");
        assertThrows(JpaObjectRetrievalFailureException.class, () -> userRepository.getReferenceById(user.getId()));
    }

    @Test
    public void deleteUserWithNoRolesSinceCreationUnder30Days() {
        LocalDate userCreationDateInPastMonth = LocalDate.now().minusDays(29);

        User user = createUserForDeletionTest(userCreationDateInPastMonth);

        userManager.usersWithOutRolesCleanup();

        Collection<UserProperty> allUserProperties = userPropertyRepository.getByUser(user);

        assertFalse(allUserProperties.isEmpty(), "User properties not null");
        assertNotNull(userRepository.getReferenceById(user.getId()));
    }

    @Test
    //    @Disabled
    public void inactiveUserCleanupTest13Months() {
        LocalDate localDateTime = LocalDate.now().minusMonths(13);
        LocalDate modifiedDate = LocalDate.now();

        User user = createUser(modifiedDate);

        createUserPropertyLastLogin(user.getId(), localDateTime.toString(), modifiedDate);

        userManager.deleteInactiveNonAdminUser();

        List<UserProperty> propertiesByUser = userPropertyRepository.getByUser(user);
        assertEquals(1, propertiesByUser.size());
        assertEquals(PropertyType.LAST_LOGIN, propertiesByUser.getFirst().getType());
        assertFalse(userRepository.getReferenceById(user.getId()).getActive());
    }

    @Test
    public void inactiveUserTest5Months() {
        LocalDate localDateTime = LocalDate.now().minusMonths(5);
        LocalDate modifiedDate = LocalDate.now();

        User user = createUser(modifiedDate);

        UserProperty userProperty = createUserPropertyLastLogin(user.getId(), localDateTime.toString(), modifiedDate);

        userManager.deleteInactiveNonAdminUser();

        assertNotNull(userPropertyRepository.findByUserAndTypeAndUserData(user, userProperty.getType(),
                userProperty.getUserData()), "Check result");
        assertTrue(userRepository.getReferenceById(user.getId()).getActive());
    }

    @Test
    public void inactiveUserTest7Months() {
        LocalDate localDateTime = LocalDate.now().minusMonths(7);
        LocalDate modifiedDate = LocalDate.now();

        User user = createUser(modifiedDate);

        UserProperty userProperty = createUserPropertyLastLogin(user.getId(), localDateTime.toString(), modifiedDate);

        userManager.deleteInactiveNonAdminUser();

        assertNotNull(userPropertyRepository.findByUserAndTypeAndUserData(user, userProperty.getType(),
                userProperty.getUserData()), "Check result");
        assertFalse(userRepository.getReferenceById(user.getId()).getActive());
    }

    @Test
    public void recentlyUsedCleanupSchedulerTest() {
        User user = userManager.getUser("EOSTESI");
        UserProperty userProperty = createUserProperty("1", LocalDateTime.now().minusMonths(2));

        userPropertyRepository.save(userProperty);
        userPropertyManager.deleteExpiredRecentlyUsedVehicleConfigurations();

        assertNull(userPropertyRepository.findByUserAndTypeAndUserData(user, userProperty.getType(),
                userProperty.getUserData()), "Check result");
    }

    @Test
    public void recentlyUsedCleanupSchedulerTestWithMultipleUserProperties() {
        userPropertyRepository.save(createUserProperty("1", LocalDateTime.now().minusMonths(2)));
        userPropertyRepository.save(createUserProperty("2", LocalDateTime.now()));
        userPropertyRepository.save(createUserProperty("3", LocalDateTime.now().minusMonths(3)));
        userPropertyRepository.save(createUserProperty("4", LocalDateTime.now()));

        userPropertyManager.deleteExpiredRecentlyUsedVehicleConfigurations();

        assertEquals(2, userPropertyRepository.count(), "Check user properties size");
    }

    @Test
    public void recentlyUsedCleanupSchedulerTestWithNoExpiredUserProperties() {
        User user = userManager.getUser("EOSTESI");
        UserProperty userProperty = createUserProperty("1", LocalDateTime.now());

        userPropertyRepository.save(userProperty);
        userPropertyManager.deleteExpiredRecentlyUsedVehicleConfigurations();

        assertEquals(1, userPropertyRepository.count(), "Check user properties size");
        assertNotNull(userPropertyRepository.findByUserAndTypeAndUserData(user, userProperty.getType(),
                userProperty.getUserData()), "Check result");
    }

    @Test
    public void retainUserWithNoRolesForTwoYears() {
        LocalDate userCreationDateInPastMonth = LocalDate.now().minusDays(60);

        User user = createUserForRetentionTest(userCreationDateInPastMonth);

        userManager.usersWithOutRolesCleanup();

        Collection<UserProperty> allUserProperties = userPropertyRepository.getByUser(user);

        assertFalse(allUserProperties.isEmpty(), "User properties not null");
        assertNotNull(userRepository.getReferenceById(user.getId()));
    }

    @BeforeEach
    public void setUp() {
        userPropertyRepository.deleteAll();
    }

    private User createUser(LocalDate modifiedDate) {
        User user = new User();
        user.setId("TestUser");
        user.setActive(true);
        user.setCostCenter("0");
        user.setTimestampCreate(Timestamp.valueOf(modifiedDate.minusDays(10).atStartOfDay()));
        user.setTimestampChange(Timestamp.valueOf(modifiedDate.atStartOfDay()));

        return userRepository.save(user);
    }

    private User createUserForDeletionTest(LocalDate modifiedDate) {
        User user = new User();
        user.setId("TestUser");
        user.setActive(true);
        user.setCostCenter("0");
        user.setTimestampCreate(Timestamp.valueOf(modifiedDate.atStartOfDay()));
        user.setTimestampChange(Timestamp.valueOf(modifiedDate.atStartOfDay()));

        User savedUser = userRepository.save(user);
        createUserPropertyLastLogin(savedUser.getId(), modifiedDate.toString(), modifiedDate);

        return savedUser;
    }

    private User createUserForRetentionTest(LocalDate modifiedDate) {
        User user = new User();
        user.setId("TestUser");
        user.setActive(true);
        user.setCostCenter("0");
        user.setTimestampCreate(Timestamp.valueOf(modifiedDate.atStartOfDay()));
        user.setTimestampChange(Timestamp.valueOf(modifiedDate.atStartOfDay()));
        user.setRoles(Set.of());

        User savedUser = userRepository.save(user);

        rightManagementRestController.addRolesToUser(new AddRolesToUserDTO(savedUser.getId(), List.of(2L)));

        rightManagementRestController.removeRoleFromUser(savedUser.getId(), 2L);

        Optional<UserProperty> noRoleSince = userPropertyRepository.getByUserAndType(savedUser,
                PropertyType.NO_ROLE_SINCE);
        noRoleSince.get().setUserData(modifiedDate.atStartOfDay().toLocalDate().toString());

        userPropertyRepository.save(noRoleSince.get());

        createUserPropertyLastLogin(savedUser.getId(), modifiedDate.toString(), modifiedDate);

        return savedUser;
    }

    private UserProperty createUserProperty(String userData, LocalDateTime modifiedDate) {
        User user = userManager.getUser("EOSTESI");
        UserProperty userProperty = new UserProperty();

        userProperty.setUser(user);
        userProperty.setType(PropertyType.RECENTLY_USED);
        userProperty.setUserData(userData);
        userProperty.setTimestampCreate(Timestamp.valueOf(modifiedDate.minusMinutes(1)));
        userProperty.setTimestampChange(Timestamp.valueOf(modifiedDate));
        userProperty.setUserCreate("EOSTESI");
        userProperty.setUserChange("EOSTESI");

        return userProperty;
    }

    private UserProperty createUserPropertyLastLogin(String user, String localDateTime, LocalDate modifiedDate) {
        User userObject = userManager.getUser(user);
        UserProperty userProperty = new UserProperty();

        userProperty.setUser(userObject);
        userProperty.setType(PropertyType.LAST_LOGIN);
        userProperty.setUserData(localDateTime);
        userProperty.setTimestampCreate(Timestamp.valueOf(modifiedDate.minusDays(1).atStartOfDay()));
        userProperty.setTimestampChange(Timestamp.valueOf(modifiedDate.atStartOfDay()));
        userProperty.setUserCreate(user);
        userProperty.setUserChange(user);
        return userPropertyRepository.save(userProperty);
    }
}