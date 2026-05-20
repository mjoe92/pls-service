package de.vw.paso.client.stueckliste.efs.inspector;

import java.util.Collection;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import de.vw.paso.client.stueckliste.efs.inspector.rule.AbstractInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.AggregateInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.ApRuleChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.BaukastenRuleChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.CostGroupInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.DuplicateInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.GWSInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.GapInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.NoMaraInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.SetKeyInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.WahlweiseInspectorChecker;
import de.vw.paso.client.stueckliste.efs.inspector.rule.WeightInspectorChecker;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class Inspector {

    private static final AbstractInspectorChecker[] RULES = { new WeightInspectorChecker(),
        new SetKeyInspectorChecker(), new CostGroupInspectorChecker(), new GapInspectorChecker(),
        new NoMaraInspectorChecker(), new DuplicateInspectorChecker(), new WahlweiseInspectorChecker(),
        new BaukastenRuleChecker(), new ApRuleChecker(), new AggregateInspectorChecker(), new GWSInspectorChecker() };

    public ListMultimap<InspectorEntryType, InspectorEntry> checkElements(Collection<EfsElementDTO> efsElements,
        EfsElementDTO rootEfsElement, VehicleConfigDTO vehicleConfig) {
        ListMultimap<InspectorEntryType, InspectorEntry> resultMap = MultimapBuilder.ListMultimapBuilder.hashKeys()
            .arrayListValues().build();

        for (AbstractInspectorChecker rule : RULES) {
            if (efsElements != null) {
                rule.checkElements(efsElements, resultMap, rootEfsElement, vehicleConfig);
            }
        }

        return resultMap;
    }
}
