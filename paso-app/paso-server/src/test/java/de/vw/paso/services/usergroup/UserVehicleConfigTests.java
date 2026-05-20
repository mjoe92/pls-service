package de.vw.paso.services.usergroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import de.vw.paso.logic.user.RequestDataKey;
import de.vw.paso.logic.user.UserManager;
import de.vw.paso.masterdata.Brand;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.util.RequestData;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserVehicleConfigTests {

    @InjectMocks
    private UserManager userManager;

    @Mock
    private UserRepository userRepository;

    private AutoCloseable autoCloseable;
    private User user;
    private List<UserGroup> userGroups;

    @BeforeEach
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("User1");
        userGroups = LongStream.rangeClosed(1, 5).mapToObj(i -> {
            List<VehicleConfig> configs = LongStream.rangeClosed(i * 5 - 5, i * 5).mapToObj(j -> {
                VehicleConfig config = new VehicleConfig();
                config.setId(j);
                return config;
            }).toList();
            var userGroup = new UserGroup();
            userGroup.setId(i);
            userGroup.setBrand(Brand.AU.getBrandName());
            userGroup.setName("UserGroup" + i);
            userGroup.setUsers(List.of(user));
            userGroup.setVehicleConfigs(configs);
            return userGroup;
        }).toList();
        user.setUserGroups(userGroups);

        given(userRepository.findByIdIgnoreCase(user.getId())).willReturn(user);
        RequestData.setRequestData(RequestDataKey.USERID, user.getId());
    }

    @AfterEach
    public void closeMocks() throws Exception {
        autoCloseable.close();
        RequestData.clearRequestData();
    }

    @Test
    public void getVehicleConfigsWithNoWriteAccess() {
        userGroups.forEach(userGroup -> userGroup.setWriteAccess(false));

        given(userRepository.findByIdIgnoreCase(user.getId())).willReturn(user);

        assertEquals(Map.of(), userManager.getVehicleConfigIdsWithWriteAccess());
    }

    @Test
    public void getVehicleConfigsWithWriteAccess() {
        userGroups.forEach(userGroup -> userGroup.setWriteAccess(true));

        given(userRepository.findByIdIgnoreCase(user.getId())).willReturn(user);

        var excepctedMap = userGroups.stream().map(UserGroup::getVehicleConfigs).flatMap(Set::stream)
                .collect(Collectors.toMap(VehicleConfig::getId, config -> true, (v1, v2) -> v1 || v2));

        assertEquals(excepctedMap, userManager.getVehicleConfigIdsWithWriteAccess());
    }

    @Test
    public void getVehicleConfigsWithReadAccess() {
        userGroups.forEach(userGroup -> userGroup.setWriteAccess(false));

        given(userRepository.findByIdIgnoreCase(user.getId())).willReturn(user);

        var excepctedMap = userGroups.stream().map(UserGroup::getVehicleConfigs).flatMap(Set::stream)
                .collect(Collectors.toMap(VehicleConfig::getId, config -> false, (v1, v2) -> v1 || v2));

        assertEquals(excepctedMap, userManager.getVehicleConfigIdsWithAccess());
    }

    @Test
    public void getVehicleConfigsWithReadAndWriteAccess() {
        userGroups.forEach(userGroup -> userGroup.setWriteAccess(userGroup.getId() % 2 == 1));

        given(userRepository.findByIdIgnoreCase(user.getId())).willReturn(user);

        Map<Long, Boolean> excepctedMap = new HashMap<>();
        userGroups.forEach(userGroup -> userGroup.getVehicleConfigs().forEach(vh -> {
            if (excepctedMap.containsKey(vh.getId())) {
                if (!excepctedMap.get(vh.getId()) && userGroup.isWriteAccess()) {
                    excepctedMap.put(vh.getId(), true);
                } else {
                    excepctedMap.putIfAbsent(vh.getId(), true);
                }
            } else {
                excepctedMap.put(vh.getId(), userGroup.isWriteAccess());
            }
        }));

        assertEquals(excepctedMap, userManager.getVehicleConfigIdsWithAccess());
    }
}
