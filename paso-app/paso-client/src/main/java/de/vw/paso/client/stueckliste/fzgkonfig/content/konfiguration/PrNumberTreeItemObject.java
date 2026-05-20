package de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;
import lombok.Getter;

public class PrNumberTreeItemObject {

    @Getter
    private PrNumberFamilyDTO prNumberFamily;

    @Getter
    private PrNumberDTO prNumber;

    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public PrNumberTreeItemObject(PrNumberFamilyDTO prNumberFamily) {
        this.prNumberFamily = prNumberFamily;
    }

    public PrNumberTreeItemObject(PrNumberDTO prNumber) {
        this.prNumber = prNumber;
    }

    public boolean isFamily() {
        return prNumberFamily != null;
    }

    public BooleanProperty selected() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
