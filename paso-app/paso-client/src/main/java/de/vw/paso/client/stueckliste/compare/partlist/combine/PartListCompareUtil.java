package de.vw.paso.client.stueckliste.compare.partlist.combine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.vw.paso.client.stueckliste.compare.partlist.EfsMaraProperties;
import de.vw.paso.client.stueckliste.compare.partlist.EfsProperty;
import de.vw.paso.client.stueckliste.compare.partlist.PartListCompareRow;
import de.vw.paso.client.stueckliste.compare.partlist.PartListCompareStatus;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class PartListCompareUtil {

    private static final List<EfsProperty<?>> PROPERTIES_TO_COMPARE = new ArrayList<>(11);

    static {
        PROPERTIES_TO_COMPARE.add(EfsMaraProperties.PARTNUMBER);
        PROPERTIES_TO_COMPARE.add(EfsProperty.AP);
        PROPERTIES_TO_COMPARE.add(EfsProperty.SET_KEY);
        PROPERTIES_TO_COMPARE.add(EfsProperty.COST_GROUP);
        PROPERTIES_TO_COMPARE.add(EfsProperty.QUANTITY);
        PROPERTIES_TO_COMPARE.add(EfsProperty.QUANTITY_UNIT);
        PROPERTIES_TO_COMPARE.add(EfsProperty.WEIGHT_CONTROL_FLAG);
        PROPERTIES_TO_COMPARE.add(EfsProperty.NODE_WEIGHT);
        PROPERTIES_TO_COMPARE.add(EfsProperty.WEIGHT);
        PROPERTIES_TO_COMPARE.add(EfsProperty.PR_NUMBER_RULE);
    }

    public static boolean calculatePropertyChanges(PartListCompareRow compareRow) {
        boolean hasChanges = false;
        for (EfsProperty<?> property : PROPERTIES_TO_COMPARE) {
            long valueCount = 0L;
            Collection<Object> uniqueValues = new HashSet<>();
            for (EfsElementDTO element : compareRow.getElementMap().values()) {
                Object apply = property.getGetter().apply(element);
                if (uniqueValues.add(apply)) {
                    valueCount++;
                }
            }

            if (valueCount != 1) {
                compareRow.setStatusForProperty(property, PartListCompareStatus.CHANGED);
                hasChanges = true;
            }
        }

        return hasChanges;
    }
}
