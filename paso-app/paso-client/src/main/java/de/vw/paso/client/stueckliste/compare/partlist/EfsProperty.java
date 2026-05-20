package de.vw.paso.client.stueckliste.compare.partlist;

import java.util.function.Function;

import de.vw.paso.client.base.I18N;
import de.vw.paso.partlist.domain.WeightControlFlag;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.AbstractEfsElementDTO;

public class EfsProperty<T> {

    public static final EfsProperty<String> NODE_LABEL = new EfsProperty<>("efs.property.nodeLabel", "nodeLabel",
        AbstractEfsElementDTO::getNodeLabel);
    public static final EfsProperty<String> NODE_VALUE = new EfsProperty<>("node.value", "nodeValue",
        AbstractEfsElementDTO::getNodeValue);
    public static final EfsProperty<Long> TI_SORT = new EfsProperty<>("efs.property.tiSort", "tisSort",
        AbstractEfsElementDTO::getTisSort);
    public static final EfsProperty<String> AP = new EfsProperty<>("efs.property.ap", "ap",
        AbstractEfsElementDTO::getAp);
    public static final EfsProperty<String> SET_KEY = new EfsProperty<>("efs.property.setKey", "setKey",
        AbstractEfsElementDTO::getSetKey);
    public static final EfsProperty<String> COST_GROUP = new EfsProperty<>("efs.property.costGroup", "costGroupName",
        AbstractEfsElementDTO::getCostGroup);
    public static final EfsProperty<Integer> QUANTITY = new EfsProperty<>("efs.property.quantity", "quantity",
        AbstractEfsElementDTO::getQuantity);
    public static final EfsProperty<String> QUANTITY_UNIT = new EfsProperty<>("efs.property.quantityUnit",
        "quantityUnit", AbstractEfsElementDTO::getQuantityUnit);
    public static final EfsProperty<String> WEIGHT_CONTROL_FLAG = new EfsProperty<>("efs.property.weightControlFLag",
        "weightControlFlag", EfsProperty::getWeightControlFlag);
    public static final EfsProperty<Double> NODE_WEIGHT = new EfsProperty<>("node.weight", "nodeWeight",
        AbstractEfsElementDTO::getNodeWeight);
    public static final EfsProperty<Double> WEIGHT = new EfsProperty<>("efs.property.weight", "weight",
        AbstractEfsElementDTO::getWeight);
    public static final EfsProperty<String> PR_NUMBER_RULE = new EfsProperty<>("efs.property.prNumberRule",
        "prNumberRule", AbstractEfsElementDTO::getPrNumberRule);

    private static String getWeightControlFlag(EfsElementDTO efsElementDTO) {
        WeightControlFlag weightControlFlag = efsElementDTO.getWeightControlFlag();
        return weightControlFlag == null ? null : weightControlFlag.getValue();
    }

    private final String propertyId;
    private final String columnId;
    protected Function<EfsElementDTO, T> getter;

    public EfsProperty(String propertyId, String columnId, Function<EfsElementDTO, T> getter) {
        this(propertyId, columnId);
        this.getter = getter;
    }

    public EfsProperty(String propertyId, String columnId) {
        this.propertyId = propertyId;
        this.columnId = columnId;
    }

    public String getPropertyName() {
        return I18N.getString(propertyId);
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getColumnId() {
        return columnId;
    }

    public Function<EfsElementDTO, T> getGetter() {
        return getter;
    }
}
