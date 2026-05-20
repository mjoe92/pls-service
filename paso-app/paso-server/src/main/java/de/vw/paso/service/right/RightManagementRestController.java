package de.vw.paso.service.right;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.vw.paso.exception.AdminPermissionException;
import de.vw.paso.logic.activitylog.AdminActivityLogManager;
import de.vw.paso.logic.role.RoleManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.mapper.RoleMapper;
import de.vw.paso.mapper.UserMapper;
import de.vw.paso.right.Role;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.user.UserListDTO;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = RightManagementRestService.URL)
public class RightManagementRestController implements RightManagementRestService {

    private final UserManager userManager;
    private final AdminActivityLogManager adminActivityLogManager;
    private final RoleManager roleManager;
    private final UserPropertyManager userPropertyManager;

    public RightManagementRestController(UserManager userManager, AdminActivityLogManager adminActivityLogManager,
            RoleManager roleManager, UserPropertyManager userPropertyManager) {
        this.userManager = userManager;
        this.adminActivityLogManager = adminActivityLogManager;
        this.roleManager = roleManager;
        this.userPropertyManager = userPropertyManager;
    }

    @Override
    @GetMapping
    @Transactional
    public RoleListDTO getAllRoles() {
        List<RoleDTO> roleDTOS =
                userManager.isCurrentUserAdmin() ? roleManager.getAllRoles().stream().map(RoleMapper::toDTO).toList() :
                        new ArrayList<>();

        return new RoleListDTO(roleDTOS);
    }

    @Override
    @GetMapping(path = ROLES_FOR_USER + "/{userId}")
    @Transactional
    public RoleListDTO getRolesForUser(@PathVariable String userId) {
        userManager.requireAdminUser();

        User user = userManager.getUser(userId);
        if (user == null) {
            return new RoleListDTO(List.of());
        }

        List<RoleDTO> roles = user.getRoles().stream().map(RoleMapper::toDTO).toList();

        return new RoleListDTO(roles);
    }

    @Override
    @GetMapping(path = USERS_FOR_ROLE + "/{roleId}")
    @Transactional
    public UserListDTO getUsersForRole(@PathVariable Long roleId) {
        userManager.requireAdminUser();

        Role role = roleManager.getRoleById(roleId);
        if (role == null) {
            throw new AdminPermissionException();
        }

        List<UserDTO> users = role.getUsers().stream()
                .map(user -> UserMapper.toDto(user, user.getRoles(), user.getUserGroups())).toList();
        return new UserListDTO(users);
    }

    @Override
    @PutMapping(path = ADD_USERS_TO_ROLE)
    @Transactional
    public void addUsersToRole(@RequestBody AddUsersToRoleDTO addUsersToRoleDTO) {
        userManager.requireAdminUser();

        Long roleId = addUsersToRoleDTO.roleId();
        Role role = roleManager.getRoleById(roleId);
        if (role == null) {
            return;
        }

        Set<String> userIds = addUsersToRoleDTO.userIds();
        for (String id : userIds) {
            User user = userManager.getUser(id);
            user.getRoles().add(role);
            role.getUsers().add(user);
            userManager.saveUser(user);

            if (userPropertyManager.getUserPropertyByUserId(user, PropertyType.NO_ROLE_SINCE) != null) {
                userPropertyManager.deleteUserPropertyType(user, PropertyType.NO_ROLE_SINCE);
            }

            adminActivityLogManager.logPermissionChange(userManager.getCurrentUserId(), userIds,
                    List.of(role.getName()), null);
        }
    }

    @Override
    @PutMapping(path = ADD_ROLES_TO_USER)
    @Transactional
    public void addRolesToUser(@RequestBody AddRolesToUserDTO addRolesToUserDTO) {
        userManager.requireAdminUser();

        List<Long> roleIds = addRolesToUserDTO.roleIds();
        String userId = addRolesToUserDTO.userId();
        Collection<Role> roles = roleManager.getRolesByIds(roleIds);
        User user = userManager.getUser(userId);
        user.getRoles().addAll(roles);

        userManager.saveUser(user);

        Set<String> userIds = new HashSet<>();
        userIds.add(userId);

        User updatedUser = userManager.getUser(userId);
        if (userPropertyManager.getUserPropertyByUserId(updatedUser, PropertyType.NO_ROLE_SINCE) != null) {
            userPropertyManager.deleteUserPropertyType(updatedUser, PropertyType.NO_ROLE_SINCE);
        }

        adminActivityLogManager.logPermissionChange(userManager.getCurrentUserId(), userIds, getRoleNamesForLogs(roles),
                null);
    }

    @Override
    @PutMapping(REMOVE_ROLE_FROM_USER)
    @Transactional
    public void removeRoleFromUser(@RequestParam String userId, @RequestParam Long roleId) {
        userManager.requireAdminUser();

        Role role = roleManager.getRoleById(roleId);
        if (role == null) {
            return;
        }

        User user = userManager.getUser(userId);
        user.getRoles().remove(role);
        userManager.saveUser(user);

        checkRolesForDeactivation(user);

        Set<String> userIds = new HashSet<>();
        userIds.add(userId);

        adminActivityLogManager.logPermissionChange(userManager.getCurrentUserId(), userIds, null,
                List.of(role.getName()));
    }

    @Override
    @PutMapping(REMOVE_ALL_ROLES_FROM_USER + "/{userId}")
    @Transactional
    public void removeAllRolesFromUser(@PathVariable String userId) {
        userManager.requireAdminUser();

        User user = userManager.getUser(userId);
        user.getRoles().clear();

        userManager.saveUser(user);
        checkRolesForDeactivation(user);

        Set<String> userIds = new HashSet<>();
        userIds.add(userId);

        Collection<Role> roles = getAllRoles().roleDTOList().stream().map(RoleMapper::toEntity).toList();
        adminActivityLogManager.logPermissionChange(userManager.getCurrentUserId(), userIds, null,
                getRoleNamesForLogs(roles));
    }

    @Override
    @PutMapping(path = REMOVE_ALL_USERS_FROM_ROLE + "/{roleId}")
    @Transactional
    public void removeAllUsersFromRole(@PathVariable Long roleId) {
        userManager.requireAdminUser();

        Role role = roleManager.getRoleById(roleId);
        if (role == null) {
            return;
        }

        Collection<User> users = role.getUsers();
        Set<String> userIds = new HashSet<>(users.size());
        for (User user : users) {
            user.getRoles().remove(role);
            checkRolesForDeactivation(user);
            userIds.add(user.getId());
        }

        adminActivityLogManager.logPermissionChange(userManager.getCurrentUserId(), userIds, null,
                List.of(role.getName()));
    }

    private List<String> getRoleNamesForLogs(Collection<Role> roles) {
        return roles.stream().map(Role::getName).toList();
    }

    private void checkRolesForDeactivation(User user) {
        if (user.getRoles().isEmpty()) {
            userPropertyManager.saveOrUpdate(user, PropertyType.NO_ROLE_SINCE, LocalDate.now().toString());
        }
    }
}
