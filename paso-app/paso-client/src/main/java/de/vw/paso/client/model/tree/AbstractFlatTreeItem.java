package de.vw.paso.client.model.tree;

/**
 * Purpose of this Item is to identify treeItems that are in a flat hirarchical tree structure similar to PRNumbers
 *
 */
public class AbstractFlatTreeItem<U> extends AbstractTreeItem<U> {

  public AbstractFlatTreeItem(U userObject) {
    super(userObject);
  }

  @Override
  public boolean isDeleted() {
    return false;
  }

  @Override
  protected Object getKey() {
    return null;
  }

  @Override
  protected Object getParentKey() {
    return null;
  }
}
