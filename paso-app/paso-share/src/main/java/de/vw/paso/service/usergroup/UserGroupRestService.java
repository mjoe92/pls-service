package de.vw.paso.service.usergroup;

import de.vw.paso.service.user.UserListDTO;
import de.vw.paso.service.vehicle.VehicleConfigListDTO;

public interface UserGroupRestService {

    String URL = "/api/user-group";
    String CONFIGS = "/configs";
    String USERS = "/users";
    String ADD_NEW_CONFIG = "/add-new-config";

    UserGroupListDTO getAllUserGroups();

    VehicleConfigListDTO getVehicleConfigsFromUserGroup(Long userGroupId);

    UserListDTO getGroupUsers(Long userGroupId);

    void saveUserGroup(UserGroupDTO userGroup);

    void addVehicleConfigToUserGroup(Long userGroupId, Long vehicleConfigId);
}