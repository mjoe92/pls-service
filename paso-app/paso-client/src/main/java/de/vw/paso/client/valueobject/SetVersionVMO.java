package de.vw.paso.client.valueobject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.service.masterdata.setversion.AddSetVersionRequestDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.setversion.UpdateSetVersionRequestDTO;

public class SetVersionVMO {

    private final StringProperty nameProperty;
    private final StringProperty lastModifiedByProperty;
    private final ObjectProperty<Date> lastModifiedAtProperty;

    private final Long id;

    private Long copyFromSetVersionId;

    public SetVersionVMO() {
        this(null);
    }

    private SetVersionVMO(Long id) {
        nameProperty = new SimpleStringProperty(this, "SetVersionName");
        lastModifiedByProperty = new SimpleStringProperty(this, "LastModifiedBy");
        lastModifiedAtProperty = new SimpleObjectProperty<>(this, "LastModifedAt");

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCopyFromSetVersionId(Long copyFromSetVersionId) {
        this.copyFromSetVersionId = copyFromSetVersionId;
    }

    public Long getCopyFromSetVersionId() {
        return copyFromSetVersionId;
    }

    public final String getSetVersionName() {
        return nameProperty.getValue();
    }

    public final void setSetVersionName(final String value) {
        nameProperty.setValue(value);
    }

    public final StringProperty setVersionNameProperty() {
        return nameProperty;
    }

    public final void setLastModifiedBy(final String value) {
        lastModifiedByProperty.setValue(value);
    }

    public final StringProperty lastModifiedByProperty() {
        return lastModifiedByProperty;
    }

    public final void setLastModifiedAt(final Date value) {
        lastModifiedAtProperty.setValue(value);
    }

    public final ObjectProperty<Date> lastModifiedAtProperty() {
        return lastModifiedAtProperty;
    }

    @Override
    public String toString() {
        return getSetVersionName();
    }

    public static SetVersionVMO toVMO(SetVersion setVersion) {
        SetVersionVMO vmo = new SetVersionVMO(setVersion.getId());
        vmo.setSetVersionName(setVersion.getName());
        String userChange =
                setVersion.getUserChange() == null ? setVersion.getUserCreate() : setVersion.getUserChange();
        vmo.setLastModifiedBy(userChange);
        Date lastChange = setVersion.getTimestampChange() == null ? setVersion.getTimestampCreate() :
                setVersion.getTimestampChange();
        vmo.setLastModifiedAt(lastChange);
        return vmo;
    }

    public static List<SetVersionVMO> dtosToVMOs(Collection<SetVersionDTO> list) {
        return list.stream().map(SetVersionVMO::dtoToVMO).toList();
    }

    public static SetVersionVMO dtoToVMO(SetVersionDTO setVersionDTO) {
        SetVersionVMO vmo = new SetVersionVMO(setVersionDTO.getId());
        vmo.setSetVersionName(setVersionDTO.getName());
        vmo.setLastModifiedBy(setVersionDTO.getUserChange());
        vmo.setLastModifiedAt(setVersionDTO.getTimestampChange());
        return vmo;
    }

    public AddSetVersionRequestDTO toAddSetVersionRequest() {
        return new AddSetVersionRequestDTO(this.getSetVersionName(), this.getCopyFromSetVersionId());
    }

    public UpdateSetVersionRequestDTO toUpdateSetVersionRequest() {
        return new UpdateSetVersionRequestDTO(this.getSetVersionName());
    }
}
