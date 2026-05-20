package de.vw.paso.client.stueckliste.efs.views.inspector.comparator;

import java.util.Comparator;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.stueckliste.efs.views.inspector.tree.InspectorTreeItemObject;
import org.apache.commons.lang3.StringUtils;

public class InspectorItemComparator implements Comparator<TreeItem<InspectorTreeItemObject>> {

    @Override
    public int compare(TreeItem<InspectorTreeItemObject> firstItem, TreeItem<InspectorTreeItemObject> secondItem) {
        InspectorTreeItemObject first = firstItem.getValue();
        InspectorTreeItemObject second = secondItem.getValue();

        if (first.isTypeNode() || second.isTypeNode()) {
            int result = second.getType().getSeverity().compareTo(first.getType().getSeverity());
            if (result != 0) {
                return result;
            }

            return Integer.compare(first.getType().ordinal(), second.getType().ordinal());
        }

        if (first.isGroupNode() || second.isGroupNode()) {
            return StringUtils.compare(first.toString(), second.toString());
        }

        if (first.isEntryNode() || second.isEntryNode()) {
            Long firstTisSort = first.getEntry().getElement().getTisSort();
            Long secondTisSort = second.getEntry().getElement().getTisSort();
            if (firstTisSort == null && secondTisSort == null) {
                return 0;
            }

            if (firstTisSort == null) {
                return -1;
            }

            return secondTisSort == null ? 1 : firstTisSort.compareTo(secondTisSort);
        }

        return 0;
    }
}
