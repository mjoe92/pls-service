package de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration;

import de.vw.paso.client.model.tree.AbstractTreeModel;

public class PrNumberTreeModel extends AbstractTreeModel<PrNumberTreeItem, PrNumberTreeItemObject> {

  public PrNumberTreeModel(final PrNumberTreeItemObject prNumberTreeItemObject) {
    super(prNumberTreeItemObject);
  }

  @Override
  protected PrNumberTreeItem createTreeItem(PrNumberTreeItemObject prNumberTreeItemObject) {
    PrNumberTreeItem prNumberTreeItem = new PrNumberTreeItem(prNumberTreeItemObject);

    cacheTreeItem(prNumberTreeItem);

    return prNumberTreeItem;
  }
}
