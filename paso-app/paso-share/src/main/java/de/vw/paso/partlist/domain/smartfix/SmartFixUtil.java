package de.vw.paso.partlist.domain.smartfix;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.smartfix.SmartFixDTO;
import de.vw.paso.utility.EfsElementUtil;
import org.apache.commons.lang3.StringUtils;

public class SmartFixUtil {

    public static Collection<EfsElementDTO> apply(Collection<EfsElementDTO> elements, Collection<SmartFixDTO> fixes) {
        Collection<EfsElementDTO> changedEntities = new ArrayList<>(fixes.size() * 2);
        for (EfsElementDTO efsElement : elements) {
            for (SmartFixDTO fix : fixes) {
                if (fix.getField().equals("SET_KEY") && StringUtils.equals(fix.getOldValue(), efsElement.getSetKey())) {
                    EfsElementDTO changedElement = EfsElementUtil.copyEfsElement(efsElement);
                    changedElement.setSetKey(fix.getNewValue());
                    changedElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));
                    changedEntities.add(changedElement);
                }

                if (fix.getField().equals("COST_GROUP") && StringUtils.equals(fix.getOldValue(),
                        efsElement.getCostGroup())) {
                    EfsElementDTO changedElement = EfsElementUtil.copyEfsElement(efsElement);
                    changedElement.setCostGroup(fix.getNewValue());
                    changedElement.setTimestampChange(new Timestamp(System.currentTimeMillis()));
                    changedEntities.add(changedElement);
                }
            }
        }

        return changedEntities;
    }
}
