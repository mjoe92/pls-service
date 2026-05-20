package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.service.partlist.setkey.SetKeyDTO;

public class SetKeyVMO {

    private final ObjectProperty<SetKeyDTO> setKeyProperty;

    public SetKeyVMO() {
        setKeyProperty = new SimpleObjectProperty<>(this, "setKey");
    }

    public final String getSetKeyString() {
        SetKeyDTO setKey = setKeyProperty.getValue();
        return setKey == null ? null : setKey.getSetKeyName();
    }

    public final void setSetKey(String value) {
        SetKeyDTO setKey = new SetKeyDTO();
        setValue(value, val -> setKeyProperty.getValue().setSetKeyName(value), va -> setKey.setSetKeyName(value),
            setKey);
    }

    public final ObjectProperty<SetKeyDTO> setKeyProperty() {
        return setKeyProperty;
    }

    public final Long getVersion() {
        SetKeyDTO setKey = setKeyProperty.getValue();
        return setKey == null ? null : setKey.getSetVersionId();
    }

    public final void setVersion(Long version) {
        SetKeyDTO setKey = new SetKeyDTO();
        setValue(version, value -> setKeyProperty.getValue().setSetVersionId(version),
            value -> setKey.setSetVersionId(version), setKey);
    }

    public final String getDescription() {
        return setKeyProperty.getValue() != null ? setKeyProperty.getValue().getDescription() : null;
    }

    public final void setDescription(String value) {
        SetKeyDTO setKey = new SetKeyDTO();
        setValue(value, (val) -> setKeyProperty.getValue().setDescription(value), (va) -> setKey.setDescription(value),
            setKey);
    }

    public final StringProperty descriptionProperty() {
        SimpleStringProperty description = new SimpleStringProperty();
        if (setKeyProperty.getValue() != null) {
            description.setValue(setKeyProperty.getValue().getDescription());
        }

        return description;
    }

    public final String getParent() {
        SetKeyDTO setKey = setKeyProperty.getValue();
        return setKey == null ? null : setKey.getParentName();
    }

    public final void setParent(String value) {
        SetKeyDTO setKey = new SetKeyDTO();
        setValue(value, (val) -> setKeyProperty.getValue().setParentName(value), (va) -> setKey.setParentName(value),
            setKey);
    }

    private <T> void setValue(T value, Consumer<T> update, Consumer<T> createSetKey, SetKeyDTO setKey) {
        if (setKeyProperty.getValue() == null) {
            createSetKey.accept(value);
            setKeyProperty.setValue(setKey);
        } else {
            update.accept(value);
        }
    }

    public void setSetVersionId(Long id) {
        setKeyProperty.getValue().setSetVersionId(id);
    }

    public final StringProperty parentProperty() {
        SimpleStringProperty parent = new SimpleStringProperty(this, "parent");
        SetKeyDTO setKey = setKeyProperty.getValue();
        if (setKey != null) {
            parent.setValue(setKey.getParentName());
        }

        return parent;
    }

    public static SetKeyDTO toSetKey(SetKeyVMO valueObject) {
        return new SetKeyDTO(valueObject.getSetKeyString(), valueObject.getDescription(), valueObject.getParent(),
            valueObject.getVersion());
    }

    public static List<SetKeyVMO> toVMOs(Collection<SetKeyDTO> setKeys) {
        return setKeys.stream().map(SetKeyVMO::toVMO).toList();
    }

    public static SetKeyVMO toVMO(SetKeyDTO setKey) {
        SetKeyVMO vmo = new SetKeyVMO();
        vmo.setKeyProperty.set(setKey);
        return vmo;
    }

    public static SetKeyDTO toDTO(SetKeyVMO setKeyVMO) {
        return new SetKeyDTO(setKeyVMO.getSetKeyString(), setKeyVMO.getDescription(), setKeyVMO.getParent(),
            setKeyVMO.getVersion());
    }
}
