package de.vw.paso.service.right;

import java.util.List;

public record AddRolesToUserDTO(String userId, List<Long> roleIds) {
}
