package de.vw.paso.services.right;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.vw.paso.core.AbstractServiceTests;
import de.vw.paso.exception.AdminPermissionException;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.RoleMapper;
import de.vw.paso.mapper.UserMapper;
import de.vw.paso.right.Role;
import de.vw.paso.service.right.AddRolesToUserDTO;
import de.vw.paso.service.right.RightManagementRestService;
import de.vw.paso.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RightManagementTests extends AbstractServiceTests {

    @Autowired
    private UserManager userManager;
    @Autowired
    private RightManagementRestService rightManagementService;

    @BeforeEach
    public void setUp() {
        if (userManager.getUserWithRoles(userManager.getCurrentUserId()) == null) {
            User user = new User();
            user.setId(userManager.getCurrentUserId());
            user.setCostCenter("0");

            userManager.saveUser(user);
        } else {
            rightManagementService.removeRoleFromUser(userManager.getCurrentUserId(), 2L);
        }
    }

    @Test
    public void testGetAllRoles() {
        boolean exists = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity)
                .findFirst().isPresent();
        assertTrue(exists);
    }

    @Test
    public void testAddRolesToUser() {
        User user = new User();
        user.setId(userManager.getCurrentUserId());
        user.setCostCenter("0");
        Collection<Role> roles = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity)
                .toList();

        user.getRoles().addAll(roles);

        User result = userManager.saveUser(user);

        assertNotNull(result);
        assertEquals(roles.size(), result.getRoles().size(), "Check added role size");
    }

    @Test
    public void testGetAllRolesForUser() {
        User user = userManager.getUserWithRoles(userManager.getCurrentUserId());
        Collection<Role> roles = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity)
                .toList();

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userManager.saveUser(user);

        Collection<Role> rolesResult = rightManagementService.getRolesForUser(userManager.getCurrentUserId())
                .roleDTOList().stream().map(RoleMapper::toEntity).toList();

        assertNotNull(rolesResult);
        assertEquals(roles.size(), rolesResult.size(), "Check role size");
    }

    @Test
    public void testGetAllRolesForWrongUser() {
        List<Role> rolesResult = rightManagementService.getRolesForUser("XX").roleDTOList().stream()
                .map(RoleMapper::toEntity).toList();

        assertEquals(new ArrayList<>(), rolesResult);
    }

    @Test
    public void testGetUsersForRole() {
        Role role = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity).toList()
                .getFirst();
        boolean exists = rightManagementService.getUsersForRole(role.getId()).userDTOList().stream()
                .map(UserMapper::toEntity).findFirst().isPresent();

        assertTrue(exists);
    }

    @Test
    public void testGetUsersForWrongRole() {
        assertThrows(AdminPermissionException.class, () -> rightManagementService.getUsersForRole(125L));
    }

    @Test
    public void testRemoveAllRoleFromUser() {
        User user = userManager.getUserWithRoles(userManager.getCurrentUserId());
        Collection<Role> roles = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity)
                .toList();

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userManager.saveUser(user);

        rightManagementService.removeAllRolesFromUser(userManager.getCurrentUserId());

        assertThrows(AdminPermissionException.class,
                () -> rightManagementService.getRolesForUser(userManager.getCurrentUserId()));
    }

    @Test
    public void testAddRoleToUser() {
        User user = userManager.getUserWithRoles(userManager.getCurrentUserId());
        Role role = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity).toList()
                .get(1);
        int alreadyAssignedRolesSize = rightManagementService.getRolesForUser(user.getId()).roleDTOList().size();
        rightManagementService.addRolesToUser(new AddRolesToUserDTO(user.getId(), List.of(role.getId())));

        assertEquals(alreadyAssignedRolesSize + 1,
                rightManagementService.getRolesForUser(user.getId()).roleDTOList().size(), "Check role size");
    }

    @Test
    public void testRemoveRoleFromUser() {
        User user = userManager.getUserWithRoles(userManager.getCurrentUserId());
        Collection<Role> roles = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity)
                .toList();

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userManager.saveUser(user);

        Iterator<Role> iterator = roles.iterator();
        iterator.next();
        Role toRemove = iterator.next();

        rightManagementService.removeRoleFromUser(user.getId(), toRemove.getId());

        Collection<Role> rolesResult = rightManagementService.getRolesForUser(user.getId()).roleDTOList().stream()
                .map(RoleMapper::toEntity).toList();

        assertNotNull(rolesResult);
        assertEquals(roles.size() - 1, rolesResult.size(), "Check role size");
    }

    @Test
    public void testRemoveRoleFromUserWithWrongRoleId() {
        User user = userManager.getUserWithRoles(userManager.getCurrentUserId());
        Collection<Role> roles = rightManagementService.getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity)
                .toList();

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userManager.saveUser(user);

        rightManagementService.removeRoleFromUser(user.getId(), 1232L);

        Collection<Role> rolesResult = rightManagementService.getRolesForUser(user.getId()).roleDTOList().stream()
                .map(RoleMapper::toEntity).toList();

        assertNotNull(rolesResult);
        assertEquals(roles.size(), rolesResult.size(), "Check role size");
    }
}
