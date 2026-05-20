package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.service.partlist.costgroup.CostGroupDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CostGroupVMO {

    private StringProperty costGroupProperty = new SimpleStringProperty(this, "costGroupName");
    private ObjectProperty<Long> versionProperty = new SimpleObjectProperty<>(this, "version");
    private StringProperty descriptionProperty = new SimpleStringProperty(this, "description");
    private StringProperty parentProperty = new SimpleStringProperty(this, "parent");

    public final String getCostGroup() {
        return costGroupProperty.getValue();
    }

    public final void setCostGroup(final String value) {
        costGroupProperty.setValue(value);
    }

    public final StringProperty costGroupProperty() {
        return costGroupProperty;
    }

    public final Long getVersion() {
        return versionProperty.getValue();
    }

    public final void setVersion(final Long value) {
        versionProperty.setValue(value);
    }

    public final ObjectProperty<Long> versionProperty() {
        return versionProperty;
    }

    public final String getDescription() {
        return descriptionProperty.getValue();
    }

    public final void setDescription(final String value) {
        descriptionProperty.setValue(value);
    }

    public final StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public final String getParent() {
        return parentProperty.getValue();
    }

    public final void setParent(final String value) {
        parentProperty.setValue(value);
    }

    public final StringProperty parentProperty() {
        return parentProperty;
    }

    public static CostGroupDTO toCostGroupDTO(CostGroupVMO valueObject) {
        return new CostGroupDTO(valueObject.getCostGroup(), valueObject.getDescription(), valueObject.getParent(),
                valueObject.getVersion());
    }

    public static CostGroupVMO toVMO(CostGroupDTO cg) {
        CostGroupVMO vmo = new CostGroupVMO();
        vmo.costGroupProperty.set(cg.getCostGroupName());
        vmo.descriptionProperty.set(cg.getDescription());
        vmo.parentProperty.set(cg.getParentCostGroupName());
        vmo.versionProperty.set(cg.getVersion());
        return vmo;
    }

    public static List<CostGroupVMO> toVMOs(Collection<CostGroupDTO> cgs) {
        return cgs.stream().map(CostGroupVMO::toVMO).collect(Collectors.toList());
    }
}
