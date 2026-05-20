package de.vw.paso.client.model.tree;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Tooltip;

import de.vw.paso.client.stueckliste.efs.views.inspector.tree.FilterableTreeItem;

public abstract class AbstractTreeItem<T> extends FilterableTreeItem<T> {

    protected Map<String, ObjectProperty<Tooltip>> propertyNameToTooltipMap = new HashMap<>();
    protected Map<String, BooleanProperty> propertyNameToChangePropertyMap = new HashMap<>();

    public abstract boolean isDeleted();

    protected abstract Object getKey();

    protected abstract Object getParentKey();

    private T userObject;

    public AbstractTreeItem(T userObject) {
        super(userObject);
        this.userObject = userObject;
    }

    public T getUserObject() {
        return userObject;
    }

    public void setUserObject(T uo) {
        userObject = uo;
    }

    protected final boolean isRoot() {
        return getParent() == null;
    }

    public BooleanProperty propertyChange(String property) {
        return propertyNameToChangePropertyMap.get(property);
    }

    public void setChange(String property, Boolean change) {
        propertyChange(property).set(change);
    }

    public Boolean isChange(String property) {
        return propertyChange(property) != null && propertyChange(property).get();
    }

    public ObjectProperty<Tooltip> getTooltip(String property) {
        return propertyNameToTooltipMap.get(property);
    }
}
