package de.vw.paso.client.smartfix;

import de.vw.paso.client.base.I18N;

public enum SmartFixField {
    SET_KEY("smartfix.field.setkey"), COST_GROUP("smartfix.field.costgroup");

    private final String key;

    SmartFixField(String resource) {
        this.key = resource;
    }

    public String getMessage() {
        return I18N.getString(key);
    }
}
