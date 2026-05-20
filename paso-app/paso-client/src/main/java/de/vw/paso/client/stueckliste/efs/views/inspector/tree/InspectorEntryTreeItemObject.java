package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import de.vw.paso.client.stueckliste.efs.inspector.InspectorEntry;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.utility.StringConstant;

public final class InspectorEntryTreeItemObject extends InspectorTreeItemObject {

    private final InspectorEntry entry;

    public InspectorEntryTreeItemObject(InspectorEntry entry) {
        super(entry.getType());

        this.entry = entry;
    }

    @Override
    public boolean isEntryNode() {
        return true;
    }

    @Override
    public InspectorEntryType getType() {
        return entry.getType();
    }

    @Override
    public String toString() {
        return "EntryTreeItem: " + getType() + StringConstant.SPACE_DASH_SPACE + entry.getType().getSeverity()
            + StringConstant.SPACE_DASH_SPACE + entry.getElement().getPartNumber() + StringConstant.SPACE_DASH_SPACE
            + entry.getElement().getEfsElementMara().getDescription1De();
    }

    @Override
    public InspectorEntry getEntry() {
        return entry;
    }
}
