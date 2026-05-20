package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;

//todo: replace VMO -> mapper
public class SalesRegionVMO {

    private final StringProperty salesRegionProperty;
    private final ObjectProperty<Integer> relevantProperty;
    private final StringProperty descriptionEnProperty;
    private final StringProperty descriptionDeProperty;

    public SalesRegionVMO() {
        salesRegionProperty = new SimpleStringProperty(this, "salesRegion");
        relevantProperty = new SimpleObjectProperty<>(this, "relevant");
        descriptionEnProperty = new SimpleStringProperty(this, "descriptionEn");
        descriptionDeProperty = new SimpleStringProperty(this, "descriptionDe");
    }

    public SalesRegionVMO(Integer relevant) {
        this();
        setRelevant(relevant);
    }

    public final String getSalesRegion() {
        return salesRegionProperty.getValue();
    }

    public final void setSalesRegion(String value) {
        salesRegionProperty.setValue(value);
    }

    public final StringProperty salesRegionProperty() {
        return salesRegionProperty;
    }

    public final Integer getRelevant() {
        return relevantProperty.getValue();
    }

    public final void setRelevant(Integer value) {
        relevantProperty.setValue(value);
    }

    public final ObjectProperty<Integer> relevantProperty() {
        return relevantProperty;
    }

    public final String getDescriptionEn() {
        return descriptionEnProperty.getValue();
    }

    public final void setDescriptionEn(String value) {
        descriptionEnProperty.setValue(value);
    }

    public final StringProperty descriptionEnProperty() {
        return descriptionEnProperty;
    }

    public final String getDescriptionDe() {
        return descriptionDeProperty.getValue();
    }

    public final void setDescriptionDe(String value) {
        descriptionDeProperty.setValue(value);
    }

    public final StringProperty descriptionDeProperty() {
        return descriptionDeProperty;
    }

    public static SalesRegionVMO toVMO(SalesRegionDTO sg) {
        SalesRegionVMO vmo = new SalesRegionVMO();
        vmo.setSalesRegion(sg.id());
        vmo.setRelevant(sg.relevant());
        vmo.setDescriptionDe(sg.descriptionDe());
        vmo.setDescriptionEn(sg.descriptionEn());
        return vmo;
    }

    public static List<SalesRegionVMO> toVMOs(Collection<SalesRegionDTO> sg) {
        return sg.stream().map(SalesRegionVMO::toVMO).collect(Collectors.toList());
    }

    public static SalesRegionDTO toSalesRegion(SalesRegionVMO vmo) {
        return new SalesRegionDTO(vmo.getSalesRegion(), vmo.getRelevant(), vmo.getDescriptionDe(),
                vmo.getDescriptionEn());
    }
}
