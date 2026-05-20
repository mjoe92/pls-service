package de.vw.paso.services.usergroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static testutil.TestUtils.saveUserGroup;

import java.util.Collection;
import java.util.List;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.usergroup.UserGroupListDTO;
import de.vw.paso.service.usergroup.UserGroupRestController;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.domain.UserGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserGroupTests extends AbstractServiceTests {

    public static final String USER_GROUP_TEST = "User Group Test";
    @Autowired
    private UserGroupRestController userGroupRestController;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private VehicleConfigRepository vehicleConfigRepository;

    @Test
    public void testGetAllUserGroups() {
        UserGroupListDTO allUserGroups = userGroupRestController.getAllUserGroups();
        assertEquals(1, allUserGroups.userGroupDTOList().size());
    }

    @Test
    public void testSaveUserGroup() {
        UserGroupDTO userGroupDTO = new UserGroupDTO();
        userGroupDTO.setBrand(Brand.AU.toString());
        userGroupDTO.setWriteAccess(true);
        userGroupDTO.setName(USER_GROUP_TEST);
        userGroupDTO.setChange(TEST_USER_ID);
        userGroupRestController.saveUserGroup(userGroupDTO);
        List<UserGroup> allUserGroups = userGroupRepository.findAll();
        UserGroup savedUserGroup = allUserGroups.stream()
                .filter(userGroup -> userGroup.getName().equals(USER_GROUP_TEST)).findFirst().orElse(null);
        assertNotNull(savedUserGroup);
    }

    @Test
    public void testAddVehicleConfigToUserGroup() {
        UserGroup userGroup = new UserGroup();
        userGroup.setBrand(Brand.BG.toString());
        userGroup.setWriteAccess(true);
        userGroup.setName(USER_GROUP_TEST + 1);
        userGroup.setChange(TEST_USER_ID);
        userGroup.setUsers(List.of(userManager.getCurrentUser()));
        UserGroup savedUserGroup = userGroupRepository.save(userGroup);

        VehicleConfigDTO savedVehicleConfig = createFzgConfigWithNameAndVehicleProject();

        userGroupRestController.addVehicleConfigToUserGroup(savedUserGroup.getId(), savedVehicleConfig.getId());

        List<VehicleConfigDTO> vehicleConfigDTOS = userGroupRestController.getVehicleConfigsFromUserGroup(
                savedUserGroup.getId()).vehicleConfigDTOList();
        assertEquals(1, vehicleConfigDTOS.size());
    }

    @Test
    public void testGetVehicleConfigsFromUserGroup() {
        VehicleConfigDTO savedVehicleConfig = createFzgConfigWithNameAndVehicleProject();

        UserGroup userGroup = userGroupRepository.findByName("VW test").stream().findAny().orElseThrow();

        userGroupRestController.addVehicleConfigToUserGroup(userGroup.getId(), savedVehicleConfig.getId());

        saveUserGroup(userManager, userGroupRepository, "Test-User-ID", vehicleConfigRepository, userGroup.getId());

        Collection<VehicleConfigDTO> vehicleConfigDTOS = userGroupRestController.getVehicleConfigsFromUserGroup(1L)
                .vehicleConfigDTOList();
        assertEquals(1, vehicleConfigDTOS.size());
    }

    @Test
    public void testGetGroupUsers() {
        List<UserDTO> userDTOS = userGroupRestController.getGroupUsers(1L).userDTOList();
        assertEquals(1, userDTOS.size());
    }
}
