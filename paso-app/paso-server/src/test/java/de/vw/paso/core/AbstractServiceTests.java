package de.vw.paso.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.vw.paso.consumer.tiwhimport.ImportPartListConsumer;
import de.vw.paso.consumer.vehicle.SaveVehicleConfigConsumer;
import de.vw.paso.core.domain.AbstractModifiableEntity;
import de.vw.paso.logic.role.RoleManager;
import de.vw.paso.logic.user.RequestDataKey;
import de.vw.paso.logic.user.UserGroupManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.right.Role;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectRestService;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.util.RequestData;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.domain.VehicleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("SpringJavaAutowiringInspection")
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractServiceTests {

    private static final String SERVICE_TEST = "SERVICE_TEST_";
    protected static final String TEST_USER_ID = "Test-User-ID";

    @Autowired
    private VehicleProjectRestService projectService;
    @Autowired
    protected SaveVehicleConfigConsumer saveVehicleConfigConsumer;
    @Autowired
    private ImportPartListConsumer importPartListConsumer;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserGroupManager userGroupManager;
    @Autowired
    private RoleManager roleManager;

    private Collection<VehicleProjectDTO> vehicleProjects;

    @BeforeEach
    public void startUp() {
        RequestData.setRequestData(RequestDataKey.USERID, TEST_USER_ID);

        if (userManager.getUser(TEST_USER_ID) == null) {
            User user = createUser();

            Role admin = createAdmin(user);
            Role weightManagerRole = createWeightManagerRole(user);

            user.setRoles(Set.of(admin, weightManagerRole));

            UserGroup userGroup = createUserGroup(user);

            userManager.saveUser(user);
            roleManager.saveRole(admin);
            roleManager.saveRole(weightManagerRole);

            userGroupManager.saveUserGroup(userGroup);

            user.setUserGroups(List.of(userGroup));
            userManager.saveUser(user);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(TEST_USER_ID, StringConstant.EMPTY,
                List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        vehicleProjects = projectService.loadVehicleProjects().vehicleProjectDTOList();
    }

    protected VehicleProjectDTO get5G0VehicleProject() {
        for (VehicleProjectDTO project : vehicleProjects) {
            if ("5G0".equals(project.getProductKey())) {
                return project;
            }
        }

        return null;
    }

    private static String getTestName() {
        try {
            throw new Exception();
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            StackTraceElement stackTraceElement = stackTrace[2];
            String[] splitClassPath = stackTraceElement.getClassName().split("\\.");

            return SERVICE_TEST + splitClassPath[splitClassPath.length - 1] + StringConstant.UNDERLINE
                    + stackTraceElement.getMethodName();
        }
    }

    protected VehicleConfigDTO createFzgConfigWithNameAndVehicleProject() {
        String name = getTestName();
        VehicleConfigDTO config = VehicleFactory.createFzgConfig(name, vehicleProjects.iterator().next());

        config.setName(name);
        assertNull(config.getUserCreate());
        assertNull(config.getResource());
        assertNotNull(config.getVehicleConfigCategoryStatus());
        assertNull(config.getVehiclePartList());
        assertNull(config.getValidDate());
        saveVehicleConfigConsumer.saveVehicleConfig(config, null);

        return saveVehicleConfigConsumer.getResult();
    }

    protected TiWhImportDTO getTiWhImport(String tiWhImportProd) {
        importPartListConsumer.importPartList(tiWhImportProd);

        return importPartListConsumer.getResult();
    }

    protected void validateDefaultInitialValues(AbstractModifiableEntity<?> entity) {
        assertNotNull(entity.getId());
        assertNotNull(entity.getUserCreate());
        assertNotNull(entity.getUserChange());
        assertNotNull(entity.getTimestampCreate());
        assertNotNull(entity.getTimestampChange());
    }

    private User createUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testuser@vw.de");
        user.setActive(true);
        user.setCostCenter("0");
        user.setTimestampCreate(Timestamp.valueOf(LocalDateTime.now()));
        user.setTimestampChange(Timestamp.valueOf(LocalDateTime.now()));

        return user;
    }

    private Role createAdmin(User user) {
        Role admin = new Role();
        admin.setId(1L);
        admin.setName("Admin");
        admin.setDescription(
                "This role has access to the user administration and has full authority in the system in order to update and maintain the Master Data as well as to be able to intervene in the case of support.");
        admin.setUsers(Set.of(user));

        return admin;
    }

    private Role createWeightManagerRole(User user) {
        Role weightManagerRole = new Role();
        weightManagerRole.setId(2L);
        weightManagerRole.setName("Weight Manager");
        weightManagerRole.setDescription(
                "This role enables user to create and check single vehicle part lists in order to analyze, summarize and compare their weights.");
        weightManagerRole.setUsers(Set.of(user));

        return weightManagerRole;
    }

    private UserGroup createUserGroup(User user) {
        UserGroup userGroup = new UserGroup();
        userGroup.setBrand(Brand.VW.toString());
        userGroup.setUsers(List.of(user));
        userGroup.setWriteAccess(true);
        userGroup.setName("VW test");
        userGroup.setChange(TEST_USER_ID);

        return userGroup;
    }
}
