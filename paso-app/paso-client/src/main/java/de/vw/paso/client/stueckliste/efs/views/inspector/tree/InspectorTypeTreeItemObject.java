package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import de.vw.paso.partlist.domain.inspector.InspectorEntryType;

public final class InspectorTypeTreeItemObject extends InspectorTreeItemObject {

    public InspectorTypeTreeItemObject(InspectorEntryType type) {
        super(type);
    }

    @Override
    public boolean isTypeNode() {
        return true;
    }

    @Override
    public String toString() {
        return "TypeTreeItem: " + getType();
    }
}
