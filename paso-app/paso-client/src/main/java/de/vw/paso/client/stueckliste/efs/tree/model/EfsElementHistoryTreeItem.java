package de.vw.paso.client.stueckliste.efs.tree.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.client.stueckliste.efs.views.historie.cell.AenderungsartTreeTableCell;
import de.vw.paso.service.partlist.efsedit.IEfsElementForDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;
import de.vw.paso.utility.EfsWeightCalculator;
import de.vw.paso.utility.StringConstant;

public class EfsElementHistoryTreeItem extends AbstractEfsElementTreeItem<IEfsElementForDTO> {

    private static final EfsWeightCalculator CALCULATOR = new EfsWeightCalculator();

    private final ObjectProperty<Long> propertyRevision;
    private final StringProperty propertyTypeChange;

    public EfsElementHistoryTreeItem(IEfsElementForDTO element,
            ObjectProperty<AbstractEfsElementMaraDTO> propertyEfsElementMara) {
        super(element, propertyEfsElementMara);

        propertyRevision = new SimpleObjectProperty<>(
                Math.max(getUserObject().getRevision(), propertyEfsElementMara.get().getRevision()));
        propertyTypeChange = new SimpleStringProperty(AenderungsartTreeTableCell.TYPE_UNDEFINED);
    }

    @Override
    protected void initCalculatedWeights() {
        Double completeWeight = CALCULATOR.calculateWeightOfElement(getUserObject());
        getUserObject().setWeight(completeWeight);
        setWeightAll(completeWeight);
        setWeightPrio(getUserObject().getEfsElementMara().getPrioritizedWeight());
    }

    @Override
    protected Object getKey() {
        return getRevision() + StringConstant.EMPTY + super.getKey();
    }

    @Override
    protected Object getParentKey() {
        return getRevision() + StringConstant.EMPTY + (
                (getUserObject().getParent() != null && getUserObject().getRevision()
                        .equals(getUserObject().getParent().getRevision())) ? getUserObject().getParent().getId()
                        : null);
    }

    public StringProperty propertyTypeChange() {
        return propertyTypeChange;
    }

    public void setTypeChange(String typeChange) {
        propertyTypeChange().set(typeChange);
    }

    public ObjectProperty<Long> propertyRevision() {
        return propertyRevision;
    }

    public Long getRevision() {
        return propertyRevision().get();
    }
}
