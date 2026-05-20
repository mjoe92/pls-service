package de.vw.paso.user;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PropertyType {

    FAVORITE_PROJECTS(Long.class),
    RECENTLY_USED(Long.class),
    RECENTLY_USED_SET_VERSION_ID(Long.class),
    LAST_EXPORT_LOCATION(String.class),
    LAST_LOGIN(String.class),
    PREFERRED_LANGUAGE(String.class),
    NO_ROLE_SINCE(String.class),
    DEFAULT_TABLE_CONFIG(Long.class);

    private final Class<? extends Serializable> dataType;

}
