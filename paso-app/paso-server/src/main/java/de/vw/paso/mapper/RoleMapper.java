package de.vw.paso.mapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.right.Role;
import de.vw.paso.service.right.RoleDTO;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.user.domain.User;

public final class RoleMapper {

    public static Role toEntity(RoleDTO dto) {
        return toEntity(dto, dto.getUsers());
    }

    public static Role toEntity(RoleDTO dto, Collection<UserDTO> userDtos) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        if (userDtos != null) {
            Set<User> users = userDtos.stream().map(UserMapper::toEntity).collect(Collectors.toSet());
            role.setUsers(users);
        }

        return role;
    }

    public static RoleDTO toDTO(Role role) {
        return toDTO(role, role.getUsers());
    }

    public static RoleDTO toDTO(Role role, Collection<User> users) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        if (users != null) {
            Set<UserDTO> userDtos = role.getUsers().stream()
                    .map(user -> UserMapper.toDto(user, user.getRoles(), user.getUserGroups()))
                    .collect(Collectors.toSet());
            roleDTO.setUsers(userDtos);
        }

        return roleDTO;
    }
}
