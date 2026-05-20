package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

public final class InspectorProblemGroupTreeItemObject extends InspectorTreeItemObject {

    private final String label;
    private final String groupId;

    public InspectorProblemGroupTreeItemObject(InspectorEntryType type, String label, String groupId) {
        super(type);

        this.label = label;
        this.groupId = groupId;
    }

    @Override
    public boolean isGroupNode() {
        return true;
    }

    @Override
    public String toString() {
        return label;
    }

    public String label() {
        return label;
    }

    public String groupId() {
        return groupId;
    }
}
