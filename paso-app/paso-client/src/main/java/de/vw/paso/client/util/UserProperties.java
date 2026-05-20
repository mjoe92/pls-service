package de.vw.paso.client.util;

import java.util.Properties;

import de.vw.paso.delegate.stueckliste.user.UserRestClientHolder;
import de.vw.paso.delegate.stueckliste.userproperty.UserPropertyRestClientHolder;
import de.vw.paso.service.user.UserDTO;
import de.vw.paso.service.userproperty.SaveUserPropertyDTO;
import de.vw.paso.user.PropertyType;

public class UserProperties extends Properties {

    private static final String USER = "USER";
    private static final String PASO_JWT = "PASO_JWT";
    private static final String PREFERRED_LANGUAGE = "PREFERRED_LANGUAGE";

    private static final UserProperties instance = new UserProperties();

    private UserProperties() {
    }

    public static UserDTO getUser() {
        return (UserDTO) instance.get(USER);
    }

    public static String getUserId() {
        UserDTO user = getUser();
        return user == null ? null : user.getId();
    }

    public static void reloadUser() {
        UserDTO persistedUser = UserRestClientHolder.getInstance().getUserById(getUserId());
        setUser(persistedUser);
    }

    public static void setUser(UserDTO user) {
        instance.put(USER, user);
    }

    public static void setPasoJwt(String tokenString) {
        instance.put(PASO_JWT, tokenString);
    }

    public static String getPasoJwt() {
        return instance.getProperty(PASO_JWT);
    }

    public static void setRecentlyUsedSetVersionId(final Long setVersionId) {
        UserPropertyRestClientHolder.getInstance()
            .saveOrUpdate(new SaveUserPropertyDTO(PropertyType.RECENTLY_USED_SET_VERSION_ID, setVersionId.toString()));
    }

    public static Long getRecentlyUsedSetVersionId() {
        String recentlyUsedVersionId = UserPropertyRestClientHolder.getInstance()
            .load(PropertyType.RECENTLY_USED_SET_VERSION_ID).getUserData();
        return recentlyUsedVersionId == null ? null : Long.parseLong(recentlyUsedVersionId);
    }

    public static void setPreferredLanguage(final String preferredLanguage) {
        instance.put(PREFERRED_LANGUAGE, preferredLanguage);
    }

    public static String getPreferredLanguage() {
        return instance.getProperty(PREFERRED_LANGUAGE);
    }
}
