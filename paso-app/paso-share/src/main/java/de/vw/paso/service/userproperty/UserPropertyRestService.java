package de.vw.paso.service.userproperty;

import de.vw.paso.user.PropertyType;

public interface UserPropertyRestService {

  String URL = "/api/user-property";
  String SAVE_ALL = "/save-all";
  String FAV_VEH_PROJ_IDS = "/fav-veh-proj-ids";
  String DEL_EXP_CONFIGS = "/del-exp-configs";
  String DEL_USER_DATA = "/del-user-data/";

  UserPropertyDTO load(PropertyType type);

  UserPropertyDTO save(SaveUserPropertyDTO saveUserPropertyDTO);

  UserPropertyDTO saveOrUpdate(SaveUserPropertyDTO saveUserPropertyDTO);

  UserPropertyListDTO saveAll(SaveAllUserPropertiesDTO saveAllUserPropertiesDTO);

  Long delete(PropertyType type, String userData);

  FavoriteVehicleProjectIds getFavoriteVehicleProjectIds();

  int deleteExpiredRecentlyUsedVehicleConfigurations();

  void deleteUserData(String userId);
}
