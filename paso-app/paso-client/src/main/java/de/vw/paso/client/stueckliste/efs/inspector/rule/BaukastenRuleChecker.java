package de.vw.paso.client.stueckliste.efs.inspector.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import org.apache.commons.lang3.StringUtils;

public class BaukastenRuleChecker extends AbstractInspectorChecker {

    private Map<EfsElementDTO, String> parentRuleMap = new HashMap<>();

    @Override
    public void checkElements(Collection<EfsElementDTO> efsElements,
            ListMultimap<InspectorEntryType, InspectorEntry> resultMap, EfsElementDTO rootEfsElement,
            VehicleConfigDTO vehicleConfig) {
        for (EfsElementDTO element : efsElements) {
            if (element.getBaukasten() != null && element.getBaukasten() == 1) {
                EfsElementDTO parent = element.getParent();
                if (parent != null) {
                    String rule = getEbkParentRule(element);
                    if (!StringUtils.equals(rule, element.getPrNumberRule())) {
                        resultMap.put(InspectorEntryType.BAUKASTEN_RULE,
                                new InspectorEntry(element, InspectorEntryType.BAUKASTEN_RULE));
                    }
                }
            }
        }
    }

    private String getEbkParentRule(EfsElementDTO element) {
        if (parentRuleMap.containsKey(element) || element.getParent() == null) {
            return parentRuleMap.get(element);
        }

        EfsElementDTO parent = element.getParent();
        String rule;
        if (parent.getBaukasten() == 0) {
            rule = parent.getPrNumberRule();
        } else {
            rule = getEbkParentRule(parent);
        }
        parentRuleMap.put(element, rule);
        return rule;
    }
}
