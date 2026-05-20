package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VehicleProjectVMO {

    private ObjectProperty<Long> idProperty = new SimpleObjectProperty<>(this, "id");
    private StringProperty projectNameProperty = new SimpleStringProperty(this, "projectName");
    private StringProperty descriptionProperty = new SimpleStringProperty(this, "description");
    private StringProperty productKeyProperty = new SimpleStringProperty(this, "produtKey");
    private StringProperty setVersionNameProperty = new SimpleStringProperty(this, "setVersionName");
    private StringProperty salesKeyProperty = new SimpleStringProperty(this, "salesKey");
    private ObjectProperty<Integer> firstModelYearProperty = new SimpleObjectProperty<>(this, "firtModelYear");
    private StringProperty platformProperty = new SimpleStringProperty(this, "platform");
    private ObjectProperty<String> brandNameProperty = new SimpleObjectProperty<>(this, "brandName");
    private ObjectProperty<Boolean> archiveProperty = new SimpleObjectProperty<>(this, "archive");

    public Long getId() {
        return idProperty.getValue();
    }

    public void setId(final Long value) {
        idProperty.setValue(value);
    }

    public ObjectProperty<Long> idProperty() {
        return idProperty;
    }

    public String getProjectName() {
        return projectNameProperty.getValue();
    }

    public void setProjectName(final String value) {
        projectNameProperty.setValue(value);
    }

    public StringProperty projectNameProperty() {
        return projectNameProperty;
    }

    public String getDescription() {
        return descriptionProperty.getValue();
    }

    public void setDescription(final String value) {
        descriptionProperty.setValue(value);
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public String getProductKey() {
        return productKeyProperty.getValue();
    }

    public void setProductKey(final String value) {
        productKeyProperty.setValue(value);
    }

    public StringProperty setVersionNameProperty() {
        return setVersionNameProperty;
    }

    public void setSetVersionName(String setVersionName) {
        this.setVersionNameProperty.setValue(setVersionName);
    }

    public StringProperty productKeyProperty() {
        return productKeyProperty;
    }

    public String getSalesKey() {
        return salesKeyProperty.getValue();
    }

    public void setSalesKey(final String value) {
        salesKeyProperty.setValue(value);
    }

    public StringProperty salesKeyProperty() {
        return salesKeyProperty;
    }

    public Integer getFirstModelYear() {
        return firstModelYearProperty.getValue();
    }

    public void setFirstModelYear(final Integer value) {
        firstModelYearProperty.setValue(value);
    }

    public ObjectProperty<Integer> firstModelYearProperty() {
        return firstModelYearProperty;
    }

    public String getPlatform() {
        return platformProperty.getValue();
    }

    public void setPlatform(final String value) {
        platformProperty.setValue(value);
    }

    public StringProperty platformProperty() {
        return platformProperty;
    }

    public void setBrandNameProperty(String brandName) {
        this.brandNameProperty.setValue(brandName);
    }

    public ObjectProperty<String> brandNameProperty() {
        return brandNameProperty;
    }

    public String brandNamePropertyValue() {
        return brandNameProperty.getValue();
    }

    public Boolean isArchive() {
        return archiveProperty.getValue();
    }

    public void setArchive(final Boolean value) {
        archiveProperty.setValue(value);
    }

    public ObjectProperty<Boolean> archiveProperty() {
        return archiveProperty;
    }

    public static VehicleProjectVMO toVMO(VehicleProjectDTO vp) {
        VehicleProjectVMO vmo = new VehicleProjectVMO();
        vmo.setId(vp.getId());
        vmo.setProjectName(vp.getProjectName());
        vmo.setDescription(vp.getDescription());
        vmo.setProductKey(vp.getProductKey());
        vmo.setSetVersionName(vp.getProductDTO().getSetVersionDTO().getName());
        vmo.setSalesKey(vp.getSalesKey());
        vmo.setFirstModelYear(vp.getFirstModelYear());
        vmo.setPlatform(vp.getPlatform());
        vmo.setBrandNameProperty(vp.getBrandCode().getBrandName());
        vmo.setArchive(vp.isArchive());
        return vmo;
    }

    public static List<VehicleProjectVMO> toVMOs(Collection<VehicleProjectDTO> vp) {
        return vp.stream().map(VehicleProjectVMO::toVMO).collect(Collectors.toList());
    }
}
