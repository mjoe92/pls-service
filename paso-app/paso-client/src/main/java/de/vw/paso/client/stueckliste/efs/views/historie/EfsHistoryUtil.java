package de.vw.paso.client.stueckliste.efs.views.historie;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import de.vw.paso.client.stueckliste.efs.tree.model.AbstractEfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementHistoryTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItem;
import de.vw.paso.client.stueckliste.efs.tree.model.EfsElementTreeItemPropertyNames;
import de.vw.paso.client.util.ReflectionUtil;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efselementhistory.EfsElementDTOWrapper;
import de.vw.paso.service.partlist.efselementhistory.EfsElementHistoryDTO;

public class EfsHistoryUtil {

    public static EfsElementHistoryTreeItem getLowerRevision(Collection<EfsElementHistoryTreeItem> historyTreeItems,
            EfsElementHistoryTreeItem abstractEfsElement) {
        List<EfsElementHistoryTreeItem> lowerRevisions = getLowerRevisions(historyTreeItems, abstractEfsElement);

        // the comparison element must be the first one, since the list is already filtered and sorted in descending order
        return lowerRevisions.isEmpty() ? null : lowerRevisions.getFirst();
    }

    private static List<EfsElementHistoryTreeItem> getLowerRevisions(
            Collection<EfsElementHistoryTreeItem> historyTreeItems, EfsElementHistoryTreeItem treeItem) {
        // Effectively final
        Long efsElementId;
        if (treeItem.getUserObject() instanceof EfsElementHistoryDTO history) {
            efsElementId = history.getEfsElement().getId();
        } else if (treeItem.getUserObject() instanceof EfsElementDTO) {
            efsElementId = treeItem.getUserObject().getId();
        } else {
            throw new RuntimeException("Invalid instance of EfsElement");
        }

        // filter the list by EfsElementId and only search for elements smaller than the current revision.
        List<EfsElementHistoryTreeItem> lowerRevisions = historyTreeItems.stream().filter(p ->
                        (p.getUserObject() instanceof EfsElementHistoryDTO
                                ? ((EfsElementHistoryDTO) p.getUserObject()).getEfsElement().getId().equals(efsElementId)
                                : p.getUserObject().getId().equals(efsElementId)) && p.getRevision() < treeItem.getRevision())
                .collect(Collectors.toList());

        // sorted by revision (descending)
        Comparator<EfsElementHistoryTreeItem> comparator = Comparator.comparing(EfsElementHistoryTreeItem::getRevision);
        lowerRevisions.sort(comparator.reversed());

        return lowerRevisions;
    }

    public static Boolean hasLowerRevision(Collection<EfsElementHistoryTreeItem> collection,
            EfsElementHistoryTreeItem efsElementTreeItem) {
        EfsElementHistoryTreeItem lowerRevision = getLowerRevision(collection, efsElementTreeItem);
        return lowerRevision != null;
    }

    public static void compareAbstractEfsElements(Collection<EfsElementHistoryTreeItem> historyEfsElements,
            EfsElementTreeItem efsElementTreeItem) {
        ObservableList<EfsElementHistoryTreeItem> observableList = FXCollections.observableArrayList(
                historyEfsElements);
        FilteredList<EfsElementHistoryTreeItem> filteredHistory = new FilteredList<>(observableList);
        filteredHistory.setPredicate(ti -> {
            if (efsElementTreeItem.getUserObject().getId() == null) {
                return false;
            }

            return ti.getUserObject() instanceof EfsElementHistoryDTO ? efsElementTreeItem.getUserObject().getId()
                    .equals(((EfsElementHistoryDTO) ti.getUserObject()).getEfsElement().getId())
                    : efsElementTreeItem.getUserObject().getId().equals(ti.getUserObject().getId());
        });

        // compare all properties with history
        for (EfsElementHistoryTreeItem historyTreeItem : filteredHistory) {
            for (String propertyName : AbstractEfsElementTreeItem.getPropertyNamesCompare()) {
                Method method = ReflectionUtil.getGetter(efsElementTreeItem.getClass(), propertyName);
                if (method == null) {
                    continue;
                }

                try {
                    Object objEfs = method.invoke(efsElementTreeItem);
                    Object objEfsHistory = method.invoke(historyTreeItem);

                    if (objEfsHistory == null && objEfs == null) {
                        efsElementTreeItem.setChange(propertyName, false);
                    } else if ((objEfsHistory == null || !objEfsHistory.equals(objEfs)) && (
                            !propertyName.equals(EfsElementTreeItemPropertyNames.WEIGHT_ALL) && !propertyName.equals(
                                    EfsElementTreeItemPropertyNames.WEIGHT_NODE) && !propertyName.equals(
                                    EfsElementTreeItemPropertyNames.WEIGHT_PRIO))) {
                        efsElementTreeItem.setChange(propertyName, true);

                        if (propertyName.equals(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_TE)
                                || propertyName.equals(EfsElementTreeItemPropertyNames.WEIGHT_CALCULATED_TE)
                                || propertyName.equals(EfsElementTreeItemPropertyNames.WEIGHT_ESTIMATED_TE)
                                || propertyName.equals(EfsElementTreeItemPropertyNames.WEIGHT_WEIGHTED_PROD)) {
                            efsElementTreeItem.setChange(EfsElementTreeItemPropertyNames.WEIGHT_ALL, true);
                            efsElementTreeItem.setChange(EfsElementTreeItemPropertyNames.WEIGHT_NODE, true);
                            efsElementTreeItem.setChange(EfsElementTreeItemPropertyNames.WEIGHT_PRIO, true);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error while invoking method " + method.getName() + " on object " + historyTreeItem);
                }
            }
        }
    }

    public static void removeCellHighlightFromChanges(Collection<EfsElementTreeItem> treeItems,
            List<EfsElementDTOWrapper> historyDTOS) {
        for (EfsElementTreeItem treeItem : treeItems) {
            EfsElementDTO abstractEfsElement = treeItem.getUserObject();

            if (abstractEfsElement.isDeleted()) {
                continue;
            }

            if (treeItem.getUserObject().getId() == null) {
                continue;
            }

            for (EfsElementDTOWrapper history : historyDTOS) {
                if (!treeItem.getUserObject().getId()
                        .equals(((EfsElementHistoryDTO) history.getEfsElement()).getEfsElement().getId())) {
                    continue;
                }

                for (String property : AbstractEfsElementTreeItem.getPropertyNamesCompare()) {
                    if (treeItem.isChange(property)) {
                        treeItem.setChange(property, false);
                    }
                }
            }
        }
    }
}
