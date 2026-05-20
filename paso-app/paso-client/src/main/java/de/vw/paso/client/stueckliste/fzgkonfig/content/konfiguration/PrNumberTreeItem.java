package de.vw.paso.client.stueckliste.fzgkonfig.content.konfiguration;

import de.vw.paso.client.model.tree.AbstractFlatTreeItem;


public class PrNumberTreeItem extends AbstractFlatTreeItem<PrNumberTreeItemObject> {

  /**************************************************************************
   *
   * Constructor
   *
   *************************************************************************
   * @param prNumberTreeItemObject*/
  public PrNumberTreeItem(PrNumberTreeItemObject prNumberTreeItemObject) {
    super(prNumberTreeItemObject);
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
