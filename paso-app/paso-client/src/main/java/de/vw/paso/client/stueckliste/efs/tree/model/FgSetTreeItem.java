package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.service.partlist.setkey.SetKeyDTO;

public class FgSetTreeItem extends AbstractTreeItem<FgSetTreeObject> {

    private static final List<String> PROPERTY_NAMES_COMPARE = new ArrayList<>();

    static {
        PROPERTY_NAMES_COMPARE.add(FgSetTreeItemPropertyNames.SET_KEY);
        PROPERTY_NAMES_COMPARE.add(FgSetTreeItemPropertyNames.DESCRIPTION);
        PROPERTY_NAMES_COMPARE.add(FgSetTreeItemPropertyNames.PLATFORM);
        PROPERTY_NAMES_COMPARE.add(FgSetTreeItemPropertyNames.SYSTEM);
        PROPERTY_NAMES_COMPARE.add(FgSetTreeItemPropertyNames.HUT);
        PROPERTY_NAMES_COMPARE.add(FgSetTreeItemPropertyNames.WEIGHT_ALL);
    }

    private final BooleanProperty propertyUnknownRoot;
    private final BooleanProperty summaryRow;
    private final StringProperty propertySetKey;
    private final StringProperty propertyDescription;
    private final ObjectProperty<Double> propertyWeightPlatform;
    private final ObjectProperty<Double> propertyWeightSystem;
    private final ObjectProperty<Double> propertyWeightHut;
    private final ObjectProperty<Double> propertyWeightAll;
    private final ObjectProperty<Integer> propertyNumPlatform;
    private final ObjectProperty<Integer> propertyNumSystem;
    private final ObjectProperty<Integer> propertyNumHut;
    private final ObjectProperty<Integer> propertyNumAll;

    FgSetTreeItem(final FgSetTreeObject treeItem) {
        super(treeItem);

        initialize();

        final SetKeyDTO setKeyDTO = treeItem.getAggregationObject();

        propertyUnknownRoot = new SimpleBooleanProperty(false);
        summaryRow = new SimpleBooleanProperty(false);
        propertySetKey = new SimpleStringProperty(setKeyDTO.getSetKeyName());
        propertyDescription = new SimpleStringProperty(setKeyDTO.getDescription());
        propertyWeightPlatform = new SimpleObjectProperty<>(treeItem.getPlatform());
        propertyWeightSystem = new SimpleObjectProperty<>(treeItem.getSystem());
        propertyWeightHut = new SimpleObjectProperty<>(treeItem.getHut());
        propertyWeightAll = new SimpleObjectProperty<>(treeItem.getWeightAll());
        propertyNumPlatform = new SimpleObjectProperty<>(0);
        propertyNumSystem = new SimpleObjectProperty<>(0);
        propertyNumHut = new SimpleObjectProperty<>(0);
        propertyNumAll = new SimpleObjectProperty<>(0);
    }

    private void initialize() {
        PROPERTY_NAMES_COMPARE.forEach(propertyName -> {
            createChangePropertyByName(propertyName);
            createTooltipPropertyByName(propertyName);
        });
    }

    private void createChangePropertyByName(String propertyName) {
        propertyNameToChangePropertyMap.put(propertyName, new SimpleBooleanProperty(false));
    }

    private void createTooltipPropertyByName(String propertyName) {
        propertyNameToTooltipMap.put(propertyName, new SimpleObjectProperty<>());
    }

    @Override
    protected Object getKey() {
        final SetKeyDTO setKeyDTO = getUserObject().getAggregationObject();

        if (setKeyDTO == null) {
            return null;
        }

        return setKeyDTO.getSetKeyName();
    }

    @Override
    protected Object getParentKey() {
        final SetKeyDTO setKeyDTO = getUserObject().getAggregationObject();

        if ((setKeyDTO == null) || (setKeyDTO.getParentName() == null)) {
            return null;
        }

        return setKeyDTO.getParentName();
    }

    @Override
    public boolean isLeaf() {
        return propertySummaryRow().get() || (!propertyUnknownRoot.get() && super.isLeaf());
    }

    public BooleanProperty propertyUnknownRoot() {
        return propertyUnknownRoot;
    }

    public BooleanProperty propertySummaryRow() {
        return summaryRow;
    }

    public StringProperty propertySetKey() {
        return propertySetKey;
    }

    public StringProperty propertyDescription() {
        return propertyDescription;
    }

    public ObjectProperty<Double> propertyWeightPlatform() {
        return propertyWeightPlatform;
    }

    public ObjectProperty<Double> propertyWeightSystem() {
        return propertyWeightSystem;
    }

    public ObjectProperty<Double> propertyWeightHut() {
        return propertyWeightHut;
    }

    public ObjectProperty<Double> propertyWeightAll() {
        return propertyWeightAll;
    }

    public ObjectProperty<Integer> propertyNumPlatform() {
        return propertyNumPlatform;
    }

    public ObjectProperty<Integer> propertyNumSystem() {
        return propertyNumSystem;
    }

    public ObjectProperty<Integer> propertyNumHut() {
        return propertyNumHut;
    }

    public ObjectProperty<Integer> propertyNumAll() {
        return propertyNumAll;
    }

    public Double getWeightPlatform() {
        return propertyWeightPlatform.get();
    }

    public void setWeightPlatform(final Double weight) {
        propertyWeightPlatform.set(weight);
    }

    public Double getWeightSystem() {
        return propertyWeightSystem.get();
    }

    public void setWeightSystem(final Double weight) {
        propertyWeightSystem.set(weight);
    }

    public Double getWeightHut() {
        return propertyWeightHut.get();
    }

    public void setWeightHut(final Double weight) {
        propertyWeightHut.set(weight);
    }

    public Double getWeightAll() {
        return propertyWeightAll.get();
    }

    public void setWeightAll(final Double weight) {
        propertyWeightAll.set(weight);
    }

    public Integer getNumPlatform() {
        return propertyNumPlatform.get();
    }

    public void setNumPlatform(Integer num) {
        propertyNumPlatform.setValue(num);
    }

    public Integer getNumSystem() {
        return propertyNumSystem.get();
    }

    public void setNumSystem(Integer num) {
        propertyNumSystem.setValue(num);
    }

    public Integer getNumHut() {
        return propertyNumHut.get();
    }

    public void setNumHut(Integer num) {
        propertyNumHut.setValue(num);
    }

    public Integer getNumAll() {
        return propertyNumAll.get();
    }

    public void setNumAll(Integer num) {
        propertyNumAll.setValue(num);
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

}
