package de.vw.paso.service.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.mapper.UserMapper;
import de.vw.paso.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserRestService.URL)
public class UserRestController implements UserRestService {

    private static final Logger LOG = LoggerFactory.getLogger(UserRestController.class);
    private final UserManager userManager;

    public UserRestController(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    @GetMapping
    @Transactional
    public UserListDTO getAllUser() {
        Collection<User> users = userManager.getAllUser();

        List<UserDTO> userDTOS = new ArrayList<>(users.size());
        for (User user : users) {
            UserDTO userDTO = UserMapper.toDto(user, user.getRoles(), user.getUserGroups());
            userDTO.setInactivityInfo(userManager.createInactivityInfo(user));
            userDTOS.add(userDTO);
        }

        return new UserListDTO(userDTOS);
    }

    @Override
    @GetMapping(path = UserRestService.ACTIVE_USERS)
    @Transactional
    public UserListDTO getAllActiveUsers() {
        List<UserDTO> userDTOS = userManager.getAllActiveUsers().stream()
                .map(user -> UserMapper.toDto(user, user.getRoles(), user.getUserGroups())).toList();
        return new UserListDTO(userDTOS);
    }

    @Override
    @PutMapping(path = UserRestService.ENABLE_USER + "{id}")
    @Transactional
    public void enableUser(@PathVariable String id) {
        userManager.enableUser(id);
        LOG.info("{} user is enabled", id);
    }

    @Override
    @PutMapping(path = UserRestService.DISABLE_USER + "{id}")
    @Transactional
    public void disableUser(@PathVariable String id) {
        userManager.disableUser(id);
        LOG.info("{} user is disabled", id);
    }

    @Override
    @GetMapping(path = UserRestService.USER_BY_ID + "{userId}")
    @Transactional
    public UserDTO getUserById(@PathVariable String userId) {
        User user = userManager.getUser(userId);
        return UserMapper.toDto(user, user.getRoles(), user.getUserGroups());
    }

    @Override
    @PutMapping(path = UserRestService.RESET_COST_CENTER_CHANGED_AT + "{userId}")
    @Transactional
    public void resetCostCenterChangedAt(@PathVariable String userId) {
        userManager.resetCostCenterChangedAt(userId);
    }
}
