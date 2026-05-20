package de.vw.paso.client.stueckliste.efs.views.inspector.solver;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.stueckliste.efs.views.inspector.event.InspectorIgnoreEntriesChangeEvent;
import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import de.vw.paso.client.util.EventBus;
import de.vw.paso.delegate.stueckliste.inspector.InspectorRestClientHolder;
import de.vw.paso.partlist.domain.inspector.InspectorEntryType;
import de.vw.paso.service.partlist.inspector.InspectorIgnoreDTO;
import de.vw.paso.service.partlist.inspector.InspectorIgnoresDTO;

public class IgnoreEntrySolver extends AbstractSolver {

    @Override
    public String getTitleKey() {
        return "ignore.toggle";
    }

    @Override
    public boolean solve() {
        sendIgnoreEntries();

        EventBus.getInstance().post(new InspectorIgnoreEntriesChangeEvent());

        return true;
    }

    @Override
    public boolean disable() {
        return getEntries().isEmpty();
    }

    private void sendIgnoreEntries() {
        var entries = getEntries();
        boolean noIgnoredEntry = entries.stream().noneMatch(item -> item.getValue().isIgnored());

        Collection<InspectorIgnoreDTO> toIgnore = new ArrayList<>(entries.size());
        for (TreeItem<InspectorTreeItemObject> inspectorEntry : entries) {
            InspectorTreeItemObject entry = inspectorEntry.getValue();
            if (entry.isIgnored() == noIgnoredEntry) {
                continue;
            }

            entry.setIgnored(noIgnoredEntry);

            InspectorEntryType type = entry.getType();
            Long elementId = entry.getEntry().getElement().getId();
            InspectorIgnoreDTO element = new InspectorIgnoreDTO(type, elementId);

            toIgnore.add(element);
        }

        InspectorIgnoresDTO data = new InspectorIgnoresDTO(toIgnore);
        if (noIgnoredEntry) {
            InspectorRestClientHolder.getInstance().saveIgnoreEntries(data);
        } else {
            InspectorRestClientHolder.getInstance().deleteIgnores(data);
        }
    }
}
