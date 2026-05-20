package de.vw.paso.service.usergroup;

import java.util.ArrayList;
import java.util.stream.Collectors;

import de.vw.paso.logic.user.UserGroupManager;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.UserGroupMapper;
import de.vw.paso.mapper.UserMapper;
import de.vw.paso.service.user.UserListDTO;
import de.vw.paso.service.vehicle.VehicleConfigListDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = UserGroupRestService.URL)
public class UserGroupRestController implements UserGroupRestService {

    private final UserGroupManager userGroupManager;
    private final UserManager userManager;

    public UserGroupRestController(UserGroupManager userGroupManager, UserManager userManager) {
        this.userGroupManager = userGroupManager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    @Transactional
    public UserGroupListDTO getAllUserGroups() {
        userManager.requireAdminUser();

        return new UserGroupListDTO(userGroupManager.getAllUserGroups().stream()
                .map(entity -> UserGroupMapper.toDto(entity, entity.getUsers(), entity.getVehicleConfigs(),
                        entity.getOwnedVehicleConfigs())).collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    @PostMapping
    @Transactional
    public void saveUserGroup(@RequestBody UserGroupDTO userGroup) {
        userManager.requireAdminUser();

        userGroupManager.saveUserGroup(
                UserGroupMapper.toEntity(userGroup, userGroup.getUsers(), userGroup.getVehicleConfigs(),
                        userGroup.getOwnedVehicleConfigs()));
    }

    @Override
    @PutMapping(path = ADD_NEW_CONFIG + "/user-group/{userGroupId}/vehicle-config/{vehicleConfigId}")
    public void addVehicleConfigToUserGroup(@PathVariable Long userGroupId, @PathVariable Long vehicleConfigId) {
        userGroupManager.addVehicleConfigToUserGroup(userGroupId, vehicleConfigId);
    }

    @Override
    @GetMapping(path = CONFIGS + "/{userGroupId}")
    @Transactional
    public VehicleConfigListDTO getVehicleConfigsFromUserGroup(@PathVariable Long userGroupId) {
        return new VehicleConfigListDTO(userGroupManager.getVehicleConfigs(userGroupId));
    }

    @Override
    @GetMapping(path = USERS + "/{userGroupId}")
    @Transactional
    public UserListDTO getGroupUsers(@PathVariable Long userGroupId) {
        userManager.requireAdminUser();

        return new UserListDTO(userGroupManager.getGroupUsers(userGroupId).stream()
                .map(user -> UserMapper.toDto(user, user.getRoles(), user.getUserGroups()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}
