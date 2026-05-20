package de.vw.paso.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.right.Role;
import de.vw.paso.service.right.RoleDTO;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;

public final class UserMapper {

    public static UserDTO toDto(User user, Collection<Role> roles, Collection<UserGroup> userGroups) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setActive(user.getActive());
        userDTO.setTimestampCreate(user.getTimestampCreate());
        userDTO.setTimestampChange(user.getTimestampChange());

        if (roles != null) {
            Set<RoleDTO> roleDtos = roles.stream().map(role -> RoleMapper.toDTO(role, null))
                    .collect(Collectors.toSet());
            userDTO.setRoles(roleDtos);
        }

        if (userGroups != null) {
            List<UserGroupDTO> users = userGroups.stream().map(UserGroupMapper::toDto).toList();
            userDTO.setUserGroups(users);
        }

        return userDTO;
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setActive(dto.getActive());
        Set<Role> roles = dto.getRoles().stream().map(role -> RoleMapper.toEntity(role, null))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        List<UserGroup> userGroups = dto.getUserGroups().stream()
                .map(userGroup -> UserGroupMapper.toEntity(userGroup, userGroup.getUsers(),
                        userGroup.getVehicleConfigs(), userGroup.getOwnedVehicleConfigs())).toList();
        user.setUserGroups(userGroups);
        user.setTimestampCreate(dto.getTimestampCreate());
        user.setTimestampChange(dto.getTimestampChange());

        return user;
    }
}
