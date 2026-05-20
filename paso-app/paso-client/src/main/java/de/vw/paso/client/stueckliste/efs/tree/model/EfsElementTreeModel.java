package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementMaraDTO;

public class EfsElementTreeModel extends AbstractTreeModel<EfsElementTreeItem, EfsElementDTO>
        implements IAggregatedEfsTreeModel {

    private Map<String, ObjectProperty<AbstractEfsElementMaraDTO>> mapMaraItems;

    private EfsElementTreeItem motor;
    private EfsElementTreeItem getriebe;

    private List<EfsElementDTO> aggregates;

    public EfsElementTreeModel(EfsElementDTO efsElement) {
        super(efsElement);
    }

    @Override
    protected EfsElementTreeItem createTreeItem(EfsElementDTO element) {
        ObjectProperty<AbstractEfsElementMaraDTO> efsElementMara = null;
        EfsElementMaraDTO currentEfsElementMara = element.getEfsElementMara();
        if (currentEfsElementMara != null) {
            efsElementMara = getMaraProperty(currentEfsElementMara.getPartNumber());
            if (efsElementMara == null) {
                efsElementMara = createMaraProperty(currentEfsElementMara.getPartNumber(), currentEfsElementMara);
            }
        }

        EfsElementTreeItem treeItem = new EfsElementTreeItem(element, efsElementMara);

        cacheTreeItem(treeItem);
        checkAggregate(treeItem);

        return treeItem;
    }

    @Override
    public void removeElement(EfsElementTreeItem efsElement) {
        super.removeElement(efsElement);

        if (efsElement == motor) {
            motor = null;
        } else if (efsElement == getriebe) {
            getriebe = null;
        }

        if (aggregates != null) {
            EfsElementDTO element = efsElement.getUserObject();
            aggregates.remove(element);
        }
    }

    @Override
    public void removeAllElements() {
        super.removeAllElements();

        motor = null;
        getriebe = null;
        aggregates = null;
    }

    private void refreshElement(EfsElementDTO element) {
        element = refreshMaraElement(element);
        super.refreshElement(element.getId(), element);
    }

    private EfsElementDTO refreshMaraElement(EfsElementDTO efsElement) {
        EfsElementMaraDTO efsElementMara = efsElement.getEfsElementMara();
        String partNumber = efsElementMara.getPartNumber();

        ObjectProperty<AbstractEfsElementMaraDTO> maraProperty = getMaraProperty(partNumber);
        if (maraProperty == null) {
            createMaraProperty(efsElement.getEfsElementMara().getPartNumber(), efsElementMara);
            maraProperty = getMaraProperty(partNumber);
        }

        EfsElementTreeItem treeItem = getTreeItem(efsElement.getId());

        treeItem.setMaraProperty(maraProperty);

        maraProperty.set(efsElementMara);

        treeItem.updateMaraProperties(maraProperty.get());

        return efsElement;
    }

    private void checkAggregate(EfsElementTreeItem treeItem) {
        if (treeItem == null) {
            return;
        }

        EfsElementDTO element = treeItem.getUserObject();
        if (element.isMotor()) {
            if (motor == null) {
                motor = treeItem;
            }

            if (aggregates == null) {
                aggregates = new ArrayList<>();
            }

            aggregates.add(element);
        }

        if (element.isGetriebe()) {
            if (getriebe == null) {
                getriebe = treeItem;
            }

            if (aggregates == null) {
                aggregates = new ArrayList<>();
            }

            aggregates.add(element);
        }
    }

    public List<EfsElementDTO> getAggregateElements() {
        if (aggregates == null) {
            return List.of();
        }

        return aggregates;
    }

    public EfsElementTreeItem getMotor() {
        return motor;
    }

    public EfsElementTreeItem getGetriebe() {
        return getriebe;
    }

    private Map<String, ObjectProperty<AbstractEfsElementMaraDTO>> getMapMaraItems() {
        if (mapMaraItems == null) {
            mapMaraItems = new HashMap<>();
        }
        return mapMaraItems;
    }

    private ObjectProperty<AbstractEfsElementMaraDTO> getMaraProperty(String teilenummer) {
        return getMapMaraItems().get(teilenummer);
    }

    private ObjectProperty<AbstractEfsElementMaraDTO> createMaraProperty(String teilenummer,
            AbstractEfsElementMaraDTO efsElementMara) {
        if (!getMapMaraItems().containsKey(teilenummer)) {
            ObjectProperty<AbstractEfsElementMaraDTO> efsElementMaraProperty = new SimpleObjectProperty<>(
                    efsElementMara);
            getMapMaraItems().put(teilenummer, efsElementMaraProperty);
        }

        return getMapMaraItems().get(teilenummer);
    }

    @Override
    public void updateNode(EfsElementDTO newEfsElement, boolean isNodeValid, boolean isHierarchical) {
        EfsElementTreeItem treeItem = getTreeItem(newEfsElement.getId());
        if (treeItem != null && !isNodeValid) {
            removeElements(List.of(treeItem));
        }

        if (!isNodeValid) {
            return;
        }

        if (treeItem == null) {
            addElement(newEfsElement, isHierarchical);
            return;
        }

        TreeItem<EfsElementDTO> parentItem = getTreeItem(treeItem.getParentKey());
        boolean oldNewParentsIdentical = Objects.equals(parentItem.getValue().getId(), newEfsElement.getParentId());
        if (!isHierarchical || oldNewParentsIdentical) {
            refreshElement(newEfsElement);
            return;
        }

        // move tree nodes and remove from old parent
        TreeItem<EfsElementDTO> oldParentTreeItem = treeItem.getParent();
        oldParentTreeItem.getChildren().remove(treeItem);

        // add to new Parent
        EfsElementTreeItem newParentTreeItem = getTreeItem(newEfsElement.getParentId());
        newParentTreeItem.getChildren().add(treeItem);

        // set new object
        treeItem.setUserObject(newEfsElement);
        treeItem.setValue(newEfsElement);
    }
}