package de.vw.paso.client.stueckliste.efs.tree.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.stueckliste.util.PartGroupUtil;
import de.vw.paso.client.valueobject.PartGroupVMO;

public class PartGroupTreeItem extends AbstractTreeItem<PartGroupTreeObject> {

    private static final List<String> PROPERTY_NAMES_COMPARE = new ArrayList<>();

    static {
        PROPERTY_NAMES_COMPARE.add(PartGroupTreeItemPropertyNames.PART_GROUP);
        PROPERTY_NAMES_COMPARE.add(PartGroupTreeItemPropertyNames.DESCRIPTION);
        PROPERTY_NAMES_COMPARE.add(PartGroupTreeItemPropertyNames.PLATFORM);
        PROPERTY_NAMES_COMPARE.add(PartGroupTreeItemPropertyNames.SYSTEM);
        PROPERTY_NAMES_COMPARE.add(PartGroupTreeItemPropertyNames.HUT);
        PROPERTY_NAMES_COMPARE.add(PartGroupTreeItemPropertyNames.WEIGHT_ALL);
    }

    private final BooleanProperty propertyUnknownRoot;
    private final BooleanProperty summaryRow;
    private final ObjectProperty<Integer> propertyPartGroupCategory;
    private final ObjectProperty<Integer> propertyPartGroup;
    private final ObjectProperty<Integer> propertyPartGroupUgr;
    private final StringProperty propertyDescription;
    private final ObjectProperty<Double> propertyWeightPlatform;
    private final ObjectProperty<Double> propertyWeightSystem;
    private final ObjectProperty<Double> propertyWeightHut;
    private final ObjectProperty<Double> propertyWeightAll;
    private final ObjectProperty<Integer> propertyNumPlatform;
    private final ObjectProperty<Integer> propertyNumSystem;
    private final ObjectProperty<Integer> propertyNumHut;
    private final ObjectProperty<Integer> propertyNumAll;

    public PartGroupTreeItem(PartGroupTreeObject treeItem) {
        super(treeItem);

        initialize();

        PartGroupVMO partGroupVMO = treeItem.getAggregationObject();

        propertyUnknownRoot = new SimpleBooleanProperty(false);
        summaryRow = new SimpleBooleanProperty(false);
        propertyPartGroupCategory = partGroupVMO.categoryProperty();
        propertyPartGroup = partGroupVMO.mgrProperty();
        propertyPartGroupUgr = partGroupVMO.ugrProperty();
        propertyDescription = partGroupVMO.descriptionProperty();
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
        PROPERTY_NAMES_COMPARE.forEach(this::createChangePropertyByName);
    }

    private void createChangePropertyByName(String propertyName) {
        propertyNameToChangePropertyMap.put(propertyName, new SimpleBooleanProperty(false));
    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return (!propertyUnknownRoot.get() && super.isLeaf());
    }

    @Override
    protected Object getKey() {
        PartGroupVMO partGroup = getUserObject().getAggregationObject();

        if (partGroup == null) {
            return null;
        }

        return PartGroupUtil.getKeyForPartGroup(PartGroupVMO.toPartGroup(partGroup));
    }

    @Override
    protected Object getParentKey() {
        PartGroupVMO partGroup = getUserObject().getAggregationObject();

        return PartGroupUtil.getParentKeyForPartGroup(PartGroupVMO.toPartGroup(partGroup));
    }

    public BooleanProperty propertyUnknownRoot() {
        return propertyUnknownRoot;
    }

    public BooleanProperty propertySummaryRow() {
        return summaryRow;
    }

    public ObjectProperty<Integer> propertyPartGroupCategory() {
        return propertyPartGroupCategory;
    }

    public ObjectProperty<Integer> propertyPartGroup() {
        return propertyPartGroup;
    }

    public ObjectProperty<Integer> propertyPartGroupUgr() {
        return propertyPartGroupUgr;
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

}
