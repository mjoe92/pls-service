package de.vw.paso.partlist.domain;

//todo: into record
public class PartProperty {

    private String propertyNameKey;
    private String propertyValue;
    private String propertyValueDescription;

    public String getPropertyNameKey() {
        return propertyNameKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public String getPropertyValueDescription() {
        return propertyValueDescription;
    }

    public void setPropertyNameKey(String propertyNameKey) {
        this.propertyNameKey = propertyNameKey;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setPropertyValueDescription(String propertyValueDescription) {
        this.propertyValueDescription = propertyValueDescription;
    }
}