package de.vw.paso.service.right;

import java.util.Set;

public record AddUsersToRoleDTO(Set<String> userIds, Long roleId) {
}
