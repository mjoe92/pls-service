package de.vw.paso.client.valueobject;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;

public class PartGroupVMO {

    private ObjectProperty<Long> idProperty = new SimpleObjectProperty<>(this, "id");
    private ObjectProperty<Integer> categoryProperty = new SimpleObjectProperty<>(this, "category");
    private ObjectProperty<Integer> mgrProperty = new SimpleObjectProperty<>(this, "mgr");
    private ObjectProperty<Integer> mgrEndProperty = new SimpleObjectProperty<>(this, "mgr");
    private ObjectProperty<Integer> ugrProperty = new SimpleObjectProperty<>(this, "ugr");
    private StringProperty descriptionProperty = new SimpleStringProperty(this, "description");

    public PartGroupVMO() {
    }

    public PartGroupVMO(Integer category, Integer mgr, Integer ugr) {
        this.categoryProperty.set(category);
        this.mgrProperty.set(mgr);
        this.ugrProperty.set(ugr);
    }

    public Long getId() {
        return idProperty.getValue();
    }

    public void setId(Long id) {
        idProperty.set(id);
    }

    public Integer getCategory() {
        return categoryProperty.get();
    }

    public void setCategory(Integer category) {
        this.categoryProperty.set(category);
    }

    public ObjectProperty<Integer> categoryProperty() {
        return categoryProperty;
    }

    public Integer getMgr() {
        return mgrProperty.getValue();
    }

    public void setMgr(Integer v) {
        mgrProperty.setValue(v);
    }

    public ObjectProperty<Integer> mgrProperty() {
        return mgrProperty;
    }

    public Integer getMgrEnd() {
        return mgrEndProperty.get();
    }

    public void setMgrEnd(Integer mgrEnd) {
        mgrEndProperty.set(mgrEnd);
    }

    public ObjectProperty<Integer> mgrEndPropertyProperty() {
        return mgrEndProperty;
    }

    public Integer getUgr() {
        return ugrProperty.getValue();
    }

    public void setUgr(Integer v) {
        ugrProperty.setValue(v);
    }

    public ObjectProperty<Integer> ugrProperty() {
        return ugrProperty;
    }

    public String getDescription() {
        return descriptionProperty.getValue();
    }

    public void setDescription(String v) {
        descriptionProperty.setValue(v);
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public static PartGroupDTO toPartGroup(PartGroupVMO valueObject) {
        PartGroupDTO pg = new PartGroupDTO();
        pg.setId(valueObject.getId());
        pg.setCategory(valueObject.getCategory());
        pg.setMgr(valueObject.getMgr());
        pg.setMgrEnd(valueObject.getMgrEnd());
        pg.setUgr(valueObject.getUgr());
        pg.setDescription(valueObject.getDescription());
        return pg;
    }

    public static PartGroupVMO toVMO(PartGroupDTO pg) {
        PartGroupVMO vmo = new PartGroupVMO();
        vmo.idProperty.set(pg.getId());
        vmo.categoryProperty.set(pg.getCategory());
        vmo.mgrProperty.set(pg.getMgr());
        vmo.mgrEndProperty.set(pg.getMgrEnd());
        vmo.ugrProperty.set(pg.getUgr());
        vmo.descriptionProperty.set(pg.getDescription());
        return vmo;
    }

    public static List<PartGroupVMO> toVMOs(List<PartGroupDTO> pgs) {
        return pgs.stream().map(PartGroupVMO::toVMO).collect(Collectors.toList());
    }

    public boolean isCategory() {
        return getMgr() == null;
    }

    public boolean isMgr() {
        return getMgr() != null && getUgr() == null;
    }

    public boolean isUgr() {
        return getUgr() != null;
    }

    @Override
    public String toString() {
        return String.format("Partgroup id:%s, category:%s, mgr:%s, mgrEnd:%S, ugr:%s, description:%s", getId(),
                getCategory(), getMgr(), getMgrEnd(), getUgr(), getDescription());
    }
}
