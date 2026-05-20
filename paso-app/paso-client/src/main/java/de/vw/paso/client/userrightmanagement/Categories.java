package de.vw.paso.client.userrightmanagement;

import de.vw.paso.client.base.I18N;

public enum Categories {
    USER("user-management"), RIGHT("right-management"), GROUP("user-group-management");

    private final String name;

    Categories(String name) {
        this.name = I18N.getString(name);
    }

    @Override
    public String toString() {
        return name;
    }
}