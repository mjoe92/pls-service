package de.vw.paso.client.stueckliste.efs.tree.model;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface IAggregatedEfsTreeModel {

  void updateNode(EfsElementDTO nodeToUpdate, boolean isNodeValid, boolean isHierachical);

}
