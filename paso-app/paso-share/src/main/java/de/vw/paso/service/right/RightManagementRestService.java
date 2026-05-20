package de.vw.paso.service.right;

import de.vw.paso.service.user.UserListDTO;

public interface RightManagementRestService {

    String URL = "/api/right-management";
    String PERMISSIONS_FOR_USER = "/permissions-for-user";
    String ROLES_FOR_USER = "/roles-for-user";
    String USERS_FOR_ROLE = "/users-for-role";
    String ADD_USERS_TO_ROLE = "/add-users-to-role";
    String ADD_ROLES_TO_USER = "/add-roles-to-user";
    String REMOVE_ROLE_FROM_USER = "/remove-role-from-user";
    String REMOVE_ALL_ROLES_FROM_USER = "/remove-all-roles-from-user";
    String REMOVE_ALL_USERS_FROM_ROLE = "/remove-all-users-from-role";

    RoleListDTO getAllRoles();

    RoleListDTO getRolesForUser(String userId);

    UserListDTO getUsersForRole(Long roleId);

    void addUsersToRole(AddUsersToRoleDTO addUsersToRoleDTO);

    void addRolesToUser(AddRolesToUserDTO addRolesToUserDTO);

    void removeRoleFromUser(String userId, Long roleId);

    void removeAllRolesFromUser(String userId);

    void removeAllUsersFromRole(Long roleId);
}
