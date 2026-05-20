package de.vw.paso.client;

import java.util.Collection;
import java.util.stream.Collectors;

import de.vw.paso.client.util.UserProperties;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;

public final class UserChecker {

    private UserChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isAdminOrInUserGroup(UserGroupDTO userGroup) {
        UserDTO user = UserProperties.getUser();
        if (user.isAdmin()) {
            return true;
        }

        if (userGroup == null) {
            return false;
        }

        return user.getUserGroups().stream().anyMatch(userGroupDTO -> userGroupDTO.getId().equals(userGroup.getId()));
    }

    public static boolean isAdminOrInUserGroups(Collection<UserGroupDTO> userGroups) {
        UserDTO user = UserProperties.getUser();
        if (user.isAdmin()) {
            return true;
        }

        if (userGroups == null || userGroups.isEmpty()) {
            return false;
        }

        Collection<Long> userGroupIds = userGroups.stream().map(UserGroupDTO::getId).collect(Collectors.toSet());
        return user.getUserGroups().stream().anyMatch(userGroupDTO -> userGroupIds.contains(userGroupDTO.getId()));
    }

    public static boolean isAdminOrUser(String userId) {
        UserDTO user = UserProperties.getUser();
        return user.isAdmin() || user.getId().equals(userId);
    }
}
