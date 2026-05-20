package de.vw.paso.client.stueckliste.fzgkonfig.content.modell;

import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.client.model.tree.AbstractFlatTreeItem;
import de.vw.paso.service.modelimport.ModelDTO;

/**
 * @author eryllan
 * @version $Revision: $
 * @created 17.11.2015
 */
public class ModellItem extends AbstractFlatTreeItem<ModelDTO> {

    private final ModelDTO model;
    private BooleanProperty selected;
    private final StringProperty propertyModelKey;
    private final StringProperty propertyModelVersion;
    private final StringProperty propertyStatus;
    private final StringProperty propertyDescription;
    private final StringProperty propertyBeginDate;

    public ModellItem(ModelDTO model) {
        super(model);
        this.model = model;
        propertyModelKey = new SimpleStringProperty(model.getModelKey());
        propertyModelVersion = new SimpleStringProperty(model.getModelVersion());
        propertyStatus = new SimpleStringProperty(model.getStatus());
        propertyDescription = new SimpleStringProperty(model.getDescription());
        propertyBeginDate = new SimpleStringProperty(
                (model.getBeginDate() == null) ? null : model.getBeginDate().toString());
    }

    public ModelDTO getModel() {
        return model;
    }

    /***************************************************
     *
     * Delegate methods
     *
     ***************************************************/

    @Override
    public Object getParentKey() {
        final String groupedModelKey = getModel().getModelKey();

        if (groupedModelKey == null || groupedModelKey.charAt(3) == '*') {
            return null;
        }

        return groupedModelKey.substring(0, 3) + "*" + groupedModelKey.substring(4);
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public Object getKey() {
        return getModel().getModelKey();
    }

    public Long getId() {
        return getModel().getId();
    }

    public String getModellschluessel() {
        return getModel().getModelKey();
    }

    public String getBezeichnung() {
        return getModel().getDescription();
    }

    public String getModellVersion() {
        return getModel().getModelVersion();
    }

    public Date getEinsatzdatum() {
        return model.getBeginDate();
    }

    public String getStatus() {
        return getModel().getStatus();
    }

    /***************************************************
     *
     * Properties
     *
     ***************************************************/

    final public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty();

        }
        return this.selected;
    }

    public StringProperty propertyModelKey() {
        return propertyModelKey;
    }

    public StringProperty propertyModelVersion() {
        return propertyModelVersion;
    }

    public StringProperty propertyStatus() {
        return propertyStatus;
    }

    public StringProperty propertyDescription() {
        return propertyDescription;
    }

    public StringProperty propertyBeginDate() {
        return propertyBeginDate;
    }

    public String getPropertyModelKey() {
        return propertyModelKey.get();
    }

    public void setPropertyModelKey(String propertyModelKey) {
        this.propertyModelKey.set(propertyModelKey);
    }

    public String getPropertyModelVersion() {
        return propertyModelVersion.get();
    }

    public void setPropertyModelVersion(String propertyModelVersion) {
        this.propertyModelVersion.set(propertyModelVersion);
    }

    public String getPropertyStatus() {
        return propertyStatus.get();
    }

    public void setPropertyStatus(String propertyStatus) {
        this.propertyStatus.set(propertyStatus);
    }

    public String getPropertyDescription() {
        return propertyDescription.get();
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription.set(propertyDescription);
    }

    public String getPropertyBeginDate() {
        return propertyBeginDate.get();
    }

    public void setPropertyBeginDate(String propertyBeginDate) {
        this.propertyBeginDate.set(propertyBeginDate);
    }

    public final boolean isSelected() {
        return this.selectedProperty().get();
    }

    public final void setSelected(final boolean selected) {
        this.selectedProperty().set(selected);
    }

}
