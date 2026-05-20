package de.vw.paso.client.valueobject;

import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableStringValue;

import de.vw.paso.service.masterdata.pst.PstDTO;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PstVMO {

    private ObjectProperty<PstDTO> pstProperty = new SimpleObjectProperty<>(this, "pst");

    public PstVMO(PstDTO pstDTO) {
        pstProperty.setValue(pstDTO);
    }

    public void setPstProperty(PstDTO pstProperty) {
        this.pstProperty.set(pstProperty);
    }

    public PstDTO getPstProperty() {
        if (Objects.nonNull(pstProperty)) {
            return pstProperty.get();
        }
        return null;
    }

    public ObservableStringValue getDescription() {
        return new SimpleStringProperty(getPstProperty() != null ? getPstProperty().getDescEng() : null);
    }

    public String getDescriptionDe() {
        return getPstProperty() != null ? getPstProperty().getDescDe() : null;
    }

    public String getDescriptionEng() {
        return getPstProperty() != null ? getPstProperty().getDescEng() : null;
    }

    public ObservableStringValue getNameAsObservableValue() {
        return new SimpleStringProperty(getPstProperty() != null ? getPstProperty().getName() : null);
    }

    public String getName() {
        return getPstProperty() != null ? getPstProperty().getName() : null;
    }

    public ObservableLongValue getParentIdAsObservableValue() {
        return new SimpleLongProperty(getPstProperty() != null ? getPstProperty().getParentId() : null);
    }

    public Long getParentId() {
        return getPstProperty() != null ? getPstProperty().getParentId() : null;
    }

    public Long getId() {
        return getPstProperty() != null ? getPstProperty().getId() : null;
    }
}
