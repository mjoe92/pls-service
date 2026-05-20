package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

public abstract class InspectorTreeItemObject {

    private final InspectorEntryType type;

    private boolean ignored;

    protected InspectorTreeItemObject(InspectorEntryType type) {
        this.type = type;
        this.ignored = true;
    }

    public InspectorEntryType getType() {
        return type;
    }

    public InspectorEntry getEntry() {
        return null;
    }

    public boolean isTypeNode() {
        return false;
    }

    public boolean isGroupNode() {
        return false;
    }

    public boolean isEntryNode() {
        return false;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
