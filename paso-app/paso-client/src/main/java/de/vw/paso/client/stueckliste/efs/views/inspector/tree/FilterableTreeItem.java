package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

public class FilterableTreeItem<T> extends TreeItem<T> {

    private final ObjectProperty<PasoPredicate<TreeItem<T>>> predicate;
    private final ObservableList<TreeItem<T>> sourceChildren;
    private final FilteredList<TreeItem<T>> filteredChildren;

    public FilterableTreeItem(T value) {
        super(value);
        predicate = new SimpleObjectProperty<>();
        sourceChildren = FXCollections.observableArrayList();

        filteredChildren = new FilteredList<>(sourceChildren);
        filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(() -> this::applyPredicate, predicate));
        filteredChildren.addListener(this::filterChildren);
    }

    private boolean applyPredicate(TreeItem<T> child) {
        PasoPredicate<TreeItem<T>> pasoPredicate = predicate.get();
        if (child instanceof FilterableTreeItem<T> filterableChildItem) {
            filterableChildItem.predicateProperty().set(pasoPredicate);
        }

        if (pasoPredicate == null || !child.getChildren().isEmpty()) {
            return true;
        }

        return pasoPredicate.test(child);
    }

    private void filterChildren(Change<? extends TreeItem<T>> change) {
        Collection<TreeItem<T>> removedChildren = new ArrayList<>();
        Collection<TreeItem<T>> addedChildren = new ArrayList<>();
        while (change.next()) {
            removedChildren.addAll(change.getRemoved());
            addedChildren.addAll(change.getAddedSubList());
        }

        Collection<TreeItem<T>> children = getChildren();
        children.removeAll(removedChildren);
        children.addAll(addedChildren);
    }

    public ObservableList<TreeItem<T>> getSourceChildren() {
        return sourceChildren;
    }

    public FilteredList<TreeItem<T>> getFilteredChildren() {
        return filteredChildren;
    }

    public ObjectProperty<PasoPredicate<TreeItem<T>>> predicateProperty() {
        return predicate;
    }

    public boolean matchesPredicate() {
        return !predicate.isNotNull().get() || predicate.get().test(this);
    }
}