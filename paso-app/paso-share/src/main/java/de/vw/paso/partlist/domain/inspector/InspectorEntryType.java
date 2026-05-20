package de.vw.paso.partlist.domain.inspector;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The type of inspector with ordered values.
 */
public enum InspectorEntryType {

    GAP(InspectorSeverity.ERROR, false),
    MISSING_SET_KEY(InspectorSeverity.ERROR, false),
    UNKNOWN_SET_KEY(InspectorSeverity.ERROR, true),
    MISSING_COST_GROUP(InspectorSeverity.ERROR, false),
    UNKNOWN_COST_GROUP(InspectorSeverity.ERROR, true),
    GWS_BAUKASTEN(InspectorSeverity.ERROR, true),
    GWS_INCORRECT(InspectorSeverity.ERROR, true),
    GWS_INCORRECT_NO_WEIGHT(InspectorSeverity.ERROR, true),
    WEIGHT_NOT_SET(InspectorSeverity.ERROR, false),
    UNIT_GRAMM_WITHOUT_WEIGHT(InspectorSeverity.ERROR, false),
    NO_MARA(InspectorSeverity.ERROR, false),
    DUPLICATE(InspectorSeverity.ERROR, false),
    UNKNOWN_AP(InspectorSeverity.ERROR, true),
    BAUKASTEN(InspectorSeverity.WARNING, true),
    WEIGHT_DIFFERENCE(InspectorSeverity.WARNING, true),
    WEIGHT_BUT_NO_UNIT(InspectorSeverity.WARNING, false),
    WEIGHT_DEVIATION_BASED_ON_THE_UNIT(InspectorSeverity.WARNING, true),
    WAHLWEISENR_NOT_1(InspectorSeverity.WARNING, false),
    BAUKASTEN_RULE(InspectorSeverity.WARNING, false),
    MISSING_AGGREGATE_ENGINE(InspectorSeverity.WARNING, false),
    MISSING_AGGREGATE_GEARBOX(InspectorSeverity.WARNING, false);

    private final InspectorSeverity severity;
    private final boolean groupsEnabled;

    public static Collection<InspectorEntryType> valuesExcept(InspectorEntryType... types) {
        Collection<InspectorEntryType> typeList = Arrays.asList(types);

        return Arrays.stream(values()).filter(inspectorEntryType -> !typeList.contains(inspectorEntryType))
            .collect(Collectors.toList());
    }

    InspectorEntryType(InspectorSeverity severity, boolean groupsEnabled) {
        this.severity = severity;
        this.groupsEnabled = groupsEnabled;
    }

    public InspectorSeverity getSeverity() {
        return severity;
    }

    public boolean isGroupsEnabled() {
        return groupsEnabled;
    }
}
