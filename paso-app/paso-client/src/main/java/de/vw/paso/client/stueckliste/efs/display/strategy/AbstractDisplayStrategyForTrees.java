package de.vw.paso.client.stueckliste.efs.display.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javafx.scene.control.TreeItem;

import de.vw.paso.client.base.I18N;
import de.vw.paso.client.model.tree.AbstractTreeItem;
import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.client.stueckliste.efs.tree.model.IAggregatedEfsTreeModel;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public abstract class AbstractDisplayStrategyForTrees<T> {

    static final String SUMMARY_MESSAGE = I18N.getString("table.row.summary");
    static final String UNKNOWN_MESSAGE = I18N.getString("table.row.unknown");

    private final boolean showDeletedElements;

    private final Collection<Filter<EfsElementDTO>> filters;

    public abstract AbstractTreeModel<? extends TreeItem<T>, T> createDisplayModel(Collection<T> nodes);

    public abstract T getParentForCreatingNewElement(List<T> efsElements);

    public abstract boolean allowsCopy();

    public abstract boolean allowsMove();

    public abstract void updateNode(IAggregatedEfsTreeModel model, EfsElementDTO nodeToUpdate);

    protected abstract <TI extends AbstractTreeItem<T>> TI addNode(AbstractTreeModel<TI, T> model, T node);

    AbstractDisplayStrategyForTrees(boolean showDeletedElements) {
        this(showDeletedElements, new ArrayList<>());
    }

    AbstractDisplayStrategyForTrees(boolean showDeletedElements, Collection<Filter<EfsElementDTO>> filters) {
        this.showDeletedElements = showDeletedElements;
        this.filters = filters;
    }

    protected boolean isNodeValid(EfsElementDTO node) {
        if (!showDeletedElements && node.isDeleted()) {
            return false;
        }

        for (Filter<EfsElementDTO> filter : filters) {
            if (!filter.isValid(node)) {
                return false;
            }
        }

        return true;
    }

    protected Collection<EfsElementDTO> getAllChildren(EfsElementDTO element) {
        Collection<EfsElementDTO> result = new HashSet<>();
        for (EfsElementDTO child : element.getChildren()) {
            result.add(child);

            if (!child.getChildren().isEmpty()) {
                Collection<EfsElementDTO> children = getAllChildren(child);
                result.addAll(children);
            }
        }

        return result;
    }
}