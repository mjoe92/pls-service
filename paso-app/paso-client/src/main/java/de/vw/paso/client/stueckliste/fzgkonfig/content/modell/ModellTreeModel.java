package de.vw.paso.client.stueckliste.fzgkonfig.content.modell;

import de.vw.paso.client.model.tree.AbstractTreeModel;
import de.vw.paso.service.modelimport.ModelDTO;

public class ModellTreeModel extends AbstractTreeModel<ModellItem, ModelDTO> {

  public ModellTreeModel() {
    super(new ModelDTO());
  }

  @Override
  protected ModellItem createTreeItem(final ModelDTO element) {
    final ModellItem treeItem = new ModellItem(element);

    cacheTreeItem(treeItem);

    return treeItem;
  }

  @Override
  public void removeAllElements() {
    //nothing to do
  }
}
