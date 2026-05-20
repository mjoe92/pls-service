package testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.repository.user.UserGroupRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.user.domain.UserGroup;

public class TestUtils {

    public static void saveUserGroup(UserManager userManager, UserGroupRepository userGroupRepository, String userId,
            VehicleConfigRepository vehicleConfigRepository, Long... alreadyExistingUserGroupIds) {
        UserGroup userGroup = new UserGroup();
        userGroup.setWriteAccess(true);
        userGroup.setVehicleConfigs(vehicleConfigRepository.findAll());
        userGroup.setOwnedVehicleConfigs(vehicleConfigRepository.findAll());
        var alreadyExistingUserGroups = Arrays.stream(alreadyExistingUserGroupIds)
                .map(id -> userGroupRepository.findById(id).orElse(null)).filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
        alreadyExistingUserGroups.add(userGroup);
        userManager.getUser(userId).setUserGroups(alreadyExistingUserGroups);
    }
}
