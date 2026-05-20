package de.vw.paso.mapper;

import java.util.Collection;
import java.util.List;

import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.usergroup.UserGroupDTO;
import de.vw.paso.service.vehicle.OwnedVehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.vehicle.domain.VehicleConfig;

public final class UserGroupMapper {

    public static UserGroupDTO toDto(UserGroup entity) {
        return toDto(entity, entity.getUsers(), entity.getVehicleConfigs(), entity.getOwnedVehicleConfigs());
    }

    public static UserGroupDTO toDto(UserGroup entity, Collection<User> users, Collection<VehicleConfig> vehicleConfigs,
            Collection<VehicleConfig> ownedVehicleConfigs) {
        UserGroupDTO userGroupDTO = new UserGroupDTO();
        userGroupDTO.setId(entity.getId());
        userGroupDTO.setBrand(entity.getBrand());
        userGroupDTO.setName(entity.getName());
        userGroupDTO.setWriteAccess(entity.isWriteAccess());
        userGroupDTO.setUserCreate(entity.getUserCreate());
        userGroupDTO.setUserChange(entity.getUserChange());
        userGroupDTO.setTimestampCreate(entity.getTimestampCreate());
        userGroupDTO.setTimestampChange(entity.getTimestampChange());

        if (users != null) {
            List<UserDTO> userDtos = users.stream().map(user -> UserMapper.toDto(user, user.getRoles(), null)).toList();
            userGroupDTO.setUsers(userDtos);
        }

        if (vehicleConfigs != null) {
            List<VehicleConfigDTO> configs = vehicleConfigs.stream()
                    .map(config -> VehicleConfigMapper.toDto(config, false)).toList();
            userGroupDTO.setVehicleConfigs(configs);
        }

        if (ownedVehicleConfigs != null) {
            List<OwnedVehicleConfigDTO> ownedConfigs = ownedVehicleConfigs.stream().map(VehicleConfigMapper::toDto)
                    .toList();
            userGroupDTO.setOwnedVehicleConfigs(ownedConfigs);
        }

        return userGroupDTO;
    }

    public static UserGroup toEntity(UserGroupDTO dto, List<UserDTO> userDtos, List<VehicleConfigDTO> vehicleConfigDtos,
            List<OwnedVehicleConfigDTO> ownedVehicleConfigDtos) {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(dto.getId());
        userGroup.setBrand(dto.getBrand());
        userGroup.setName(dto.getName());
        userGroup.setWriteAccess(dto.isWriteAccess());
        userGroup.setTimestampCreate(dto.getTimestampCreate());
        userGroup.setUserCreate(dto.getUserCreate());
        userGroup.setTimestampChange(dto.getTimestampChange());
        userGroup.setUserChange(dto.getUserChange());

        if (userDtos != null) {
            List<User> users = userDtos.stream().map(UserMapper::toEntity).toList();
            userGroup.setUsers(users);
        }

        if (vehicleConfigDtos != null) {
            List<VehicleConfig> configs = vehicleConfigDtos.stream().map(VehicleConfigMapper::toEntity).toList();
            userGroup.setVehicleConfigs(configs);
        }

        if (ownedVehicleConfigDtos != null) {
            List<VehicleConfig> ownedConfigs = ownedVehicleConfigDtos.stream().map(VehicleConfigMapper::toEntity)
                    .toList();
            userGroup.setOwnedVehicleConfigs(ownedConfigs);
        }

        return userGroup;
    }
}
