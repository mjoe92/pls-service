package de.vw.paso.service.userproperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.logic.user.UserPropertyManager;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import de.vw.paso.user.domain.UserProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = UserPropertyRestService.URL)
public class UserPropertyRestController implements UserPropertyRestService {

    private final UserPropertyManager userPropertyManager;
    private final UserManager userManager;

    public UserPropertyRestController(UserPropertyManager userPropertyManager, UserManager userManager) {
        this.userPropertyManager = userPropertyManager;
        this.userManager = userManager;
    }

    @Override
    @GetMapping("/{type}")
    public UserPropertyDTO load(@PathVariable PropertyType type) {
        return toUserPropertyDTO(userPropertyManager.load(userManager.getCurrentUser(), type));
    }

    @Override
    @PostMapping
    public UserPropertyDTO save(@RequestBody SaveUserPropertyDTO saveUserPropertyDTO) {
        PropertyType type = saveUserPropertyDTO.type();
        String userData = saveUserPropertyDTO.userData();

        UserProperty savedUserProperty = PropertyType.RECENTLY_USED == type ?
                userPropertyManager.saveRecentlyUsed(userManager.getCurrentUser(), type, userData) :
                userPropertyManager.save(userManager.getCurrentUser(), type, userData);

        return toUserPropertyDTO(savedUserProperty);
    }

    @Override
    @PutMapping
    public UserPropertyDTO saveOrUpdate(@RequestBody SaveUserPropertyDTO saveUserPropertyDTO) {
        return toUserPropertyDTO(
                userPropertyManager.saveOrUpdate(userManager.getCurrentUser(), saveUserPropertyDTO.type(),
                        saveUserPropertyDTO.userData()));
    }

    @Override
    @PostMapping(path = SAVE_ALL)
    public UserPropertyListDTO saveAll(@RequestBody SaveAllUserPropertiesDTO toSave) {
        Collection<String> userData = toSave.userData();
        if (userData == null) {
            return new UserPropertyListDTO(List.of());
        }

        List<UserPropertyDTO> result = new ArrayList<>(userData.size());
        for (String userDatum : userData) {
            UserPropertyDTO saved = save(new SaveUserPropertyDTO(toSave.type(), userDatum));
            result.add(saved);
        }

        return new UserPropertyListDTO(result);
    }

    @Override
    @DeleteMapping
    public Long delete(@RequestParam PropertyType type, @RequestParam String userData) {
        return userPropertyManager.delete(userManager.getCurrentUser(), type, userData);
    }

    @Override
    @GetMapping(path = FAV_VEH_PROJ_IDS)
    public FavoriteVehicleProjectIds getFavoriteVehicleProjectIds() {
        return new FavoriteVehicleProjectIds(
                userPropertyManager.getFavoriteVehicleProjectIds(userManager.getCurrentUser()));
    }

    @Override
    @DeleteMapping(path = DEL_EXP_CONFIGS)
    public int deleteExpiredRecentlyUsedVehicleConfigurations() {
        return userPropertyManager.deleteExpiredRecentlyUsedVehicleConfigurations();
    }

    @Override
    @DeleteMapping(path = DEL_USER_DATA + "{userId}")
    public void deleteUserData(@PathVariable String userId) {
        userManager.requireAdminUser();

        userPropertyManager.clearPropertiesByUserId(userManager.getUser(userId), userManager.getCurrentUserId());
    }

    private UserPropertyDTO toUserPropertyDTO(UserProperty userProperty) {
        User user = userManager.getCurrentUser();

        UserPropertyDTO userPropertyDTO = new UserPropertyDTO();
        userPropertyDTO.setId(userProperty.getId());
        userPropertyDTO.setUserId(user.getId());
        userPropertyDTO.setType(userProperty.getType());
        userPropertyDTO.setUserData(userProperty.getUserData());

        return userPropertyDTO;
    }
}
