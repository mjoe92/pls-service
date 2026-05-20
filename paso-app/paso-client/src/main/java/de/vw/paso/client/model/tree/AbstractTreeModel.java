package de.vw.paso.client.model.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

public abstract class AbstractTreeModel<T extends AbstractTreeItem<U>, U> {

    protected abstract T createTreeItem(U userObject);

    private final Map<Object, T> mapTreeItems = new HashMap<>();
    private T root;

    public AbstractTreeModel(U rootUserObject) {
        this.root = createTreeItem(rootUserObject);
    }

    public T getRoot() {
        return root;
    }

    public void setRoot(U rootUserObject) {
        setRoot(createTreeItem(rootUserObject));
    }

    public void setRoot(T root) {
        clearCache();
        this.root = root;
    }

    protected List<T> addElements(List<U> children) {
        List<T> treeItems = new ArrayList<>();
        children.forEach(userObject -> {
            T treeItem = createTreeItem(userObject);
            cacheTreeItem(treeItem);
            treeItems.add(treeItem);
        });
        treeItems.forEach(treeItem -> addElement(treeItem, true));
        return treeItems;
    }

    public T addElement(U userObject, boolean addHierachically) {
        T treeItem = createTreeItem(userObject);
        addElement(treeItem, addHierachically);
        return treeItem;
    }

    public void addElement(T treeItem, boolean addHierachically) {
        if (addHierachically) {
            T parent = getTreeItem(treeItem.getParentKey());
            if (parent == null) {
                parent = getRoot();
            }
            addElement(parent, treeItem);
        } else {
            addElement(getRoot(), treeItem);
        }
    }

    public void addElement(T parent, T child) {
        parent.getSourceChildren().add(child);
        cacheTreeItem(child);
    }

    public void removeAllElements() {
        getRoot().getChildren().clear();
        clearCache();
    }

    protected void removeElements(List<T> children) {
        children.forEach(this::removeElement);
    }

    public void removeElement(T child) {
        removeElement(child.getParent(), child);
    }

    private void removeElement(TreeItem<U> parent, T child) {
        parent.getChildren().remove(child);
        removeFromCache(child);
    }

    protected void refreshElement(Object key, U element) {
        T treeItem = getTreeItem(key);
        treeItem.setValue(element);
        treeItem.setUserObject(element);
    }

    public U getUserObject(Object key) {
        T treeItem = getTreeItem(key);
        return treeItem != null ? treeItem.getUserObject() : null;
    }

    public T getTreeItem(Object key) {
        return mapTreeItems.get(key);
    }

    protected void cacheTreeItem(T treeItem) {
        this.mapTreeItems.put(treeItem.getKey(), treeItem);
    }

    private void removeFromCache(T treeItem) {
        this.mapTreeItems.remove(treeItem.getKey());
    }

    private void clearCache() {
        this.mapTreeItems.clear();
    }

    public final Collection<T> getTreeItems() {
        return mapTreeItems.values();
    }
}
