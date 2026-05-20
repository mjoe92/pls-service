package de.vw.paso.client.stueckliste.efs.tree.model;

import javafx.beans.property.ObjectProperty;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;
import de.vw.paso.utility.EfsElementResolver;

public class EfsElementTreeItem extends AbstractEfsElementTreeItem<EfsElementDTO> {

    @Override
    public EfsElementDTO getUserObject() {
        EfsElementDTO superUserObject = super.getUserObject();
        if (superUserObject == null) {
            return null;
        }

        if (superUserObject.getId() == null) {
            return superUserObject;
        }

        return EfsElementResolver.getElement(superUserObject.getId());
    }

    public EfsElementTreeItem(EfsElementDTO element, ObjectProperty<AbstractEfsElementMaraDTO> propertyEfsElementMara) {
        super(element, propertyEfsElementMara);
    }

    @Override
    protected void initCalculatedWeights() {
        Double completeWeight = getUserObject().getWeight();
        setWeightAll(completeWeight);
        setWeightNode(getUserObject().getNodeWeight());
        setWeightPrio(getUserObject().getEfsElementMara().getPrioritizedWeight());
    }

    public void updateMaraProperties(AbstractEfsElementMaraDTO newValue) {
        // if a MARA element changes, all UserObjects must be updated
        getUserObject().setEfsElementMara((EfsElementMaraDTO) newValue);

        setDescription1(getUserObject().getEfsElementMara().getDescription1De());
        setDescription2(getUserObject().getEfsElementMara().getDescription2De());
        setDrawingDate(getUserObject().getEfsElementMara().getDrawingDate());
        setDrawingStatus(getUserObject().getEfsElementMara().getDrawingStatus());
        setWeightAll(getUserObject().getWeight());
        setWeightNode(getUserObject().getNodeWeight());
        setWeightPrio(getUserObject().getEfsElementMara().getPrioritizedWeight());
        setWeightCalculatedTe(getUserObject().getEfsElementMara().getWeightCalculatedTe());
        setWeightEstimatedTe(getUserObject().getEfsElementMara().getWeightEstimatedTe());
        setWeightWeightedTe(getUserObject().getEfsElementMara().getWeightWeightedTe());
        setWeightWeightedProd(getUserObject().getEfsElementMara().getWeightWeightedProd());
    }

    public void setMaraProperty(ObjectProperty<AbstractEfsElementMaraDTO> propertyEfsElementMara) {
        //propertyEfsElementMara.removeListener(changeListenerMara);
        this.propertyEfsElementMara = propertyEfsElementMara;
    }
}
