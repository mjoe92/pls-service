package de.vw.paso.client.stueckliste.compare.partlist.combine.nodematcher;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public class NodeLabelPathNodeIdentityProvider extends AbstractPathNodeIdentityProvider {

  @Override
  protected String getIdOfNode(EfsElementDTO element) {
    if (element != null) return element.getNodeLabel();
    return "";
  }
}

