package de.vw.paso.client.stueckliste.efs.inspector;

import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.utility.StringConstant;

public class InspectorEntry {

    private final InspectorEntryType type;
    private final EfsElementDTO element;
    private final String problemGroup;
    private final String groupIdentifier;

    public InspectorEntry(EfsElementDTO element, InspectorEntryType type) {
        this(element, type, null);
    }

    public InspectorEntry(EfsElementDTO element, InspectorEntryType type, String problemGroup) {
        this(element, type, problemGroup, problemGroup);
    }

    public InspectorEntry(EfsElementDTO element, InspectorEntryType type, String problemGroup, String groupIdentifier) {
        this.element = element;
        this.type = type;
        this.problemGroup = problemGroup;
        this.groupIdentifier = groupIdentifier;
    }

    public String getLabel() {
        return switch (type) {
            case GAP, NO_MARA -> element.getNodeLabel();
            default -> {
                String desc2 = element.getEfsElementMara().getDescription2De() == null ? StringConstant.EMPTY
                    : element.getEfsElementMara().getDescription2De();

                yield element.getFormattedPartNumber() + StringConstant.SPACE_DASH_SPACE + element.getEfsElementMara()
                    .getDescription1De() + StringConstant.SPACE + desc2;
            }
        };
    }

    public InspectorEntryType getType() {
        return type;
    }

    public EfsElementDTO getElement() {
        return element;
    }

    public String getProblemGroup() {
        return problemGroup;
    }

    public String getGroupIdentifier() {
        return groupIdentifier;
    }
}
