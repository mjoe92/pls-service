package de.vw.paso.service.user;

public interface UserRestService {

    String URL = "/api/user-service";
    String ACTIVE_USERS = "/active-users";
    String ENABLE_USER = "/enable-user/";
    String DISABLE_USER = "/disable-user/";
    String USER_BY_ID = "/id/";
    String RESET_COST_CENTER_CHANGED_AT = "/reset-cost-center-changed-at/";

    UserListDTO getAllUser();

    UserListDTO getAllActiveUsers();

    void enableUser(String id);

    void disableUser(String id);

    UserDTO getUserById(String userId);

    void resetCostCenterChangedAt(String userId);
}