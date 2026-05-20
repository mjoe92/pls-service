package de.vw.paso.util;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.vw.paso.partlist.domain.EfsElement;
import de.vw.paso.partlist.domain.smartfix.SmartFix;
import org.apache.commons.lang3.StringUtils;

public class SmartFixEntityUtil {

    public static Map<EfsElement, EfsElement> apply(Collection<EfsElement> elements, Collection<SmartFix> fixes) {
        Map<EfsElement, EfsElement> changedEntities = new HashMap<>();
        elements.parallelStream().forEach(efsElement -> {
            for (SmartFix fix : fixes) {
                smartFixSetKey(changedEntities, efsElement, fix);
                smartFixCostGroup(changedEntities, efsElement, fix);
            }
        });
        return changedEntities;
    }

    private static void smartFixCostGroup(Map<EfsElement, EfsElement> changedEntities, EfsElement efsElement,
            SmartFix fix) {
        if (fix.getField().equals("COST_GROUP")) {
            if (StringUtils.equals(fix.getOldValue(), efsElement.getCostGroup())) {
                EfsElement changedElement = EfsElementEntityUtil.copyEfsElement(efsElement);
                changedElement.setCostGroup(fix.getNewValue());
                changedElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));
                changedEntities.put(efsElement, changedElement);
            }
        }
    }

    private static void smartFixSetKey(Map<EfsElement, EfsElement> changedEntities, EfsElement efsElement,
            SmartFix fix) {
        if (fix.getField().equals("SET_KEY")) {
            if (StringUtils.equals(fix.getOldValue(), efsElement.getSetKey())) {
                EfsElement changedElement = EfsElementEntityUtil.copyEfsElement(efsElement);
                changedElement.setSetKey(fix.getNewValue());
                changedElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));
                changedEntities.put(efsElement, changedElement);
            }
        }
    }
}
