package de.vw.paso.client.stueckliste.compare.partlist.combine.nodematcher;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface INodeIdentityProvider<T> {

  default boolean nodesEquals(EfsElementDTO element1, EfsElementDTO element2) {
    return getIdentity(element1).equals(getIdentity(element2));
  }

  T getIdentity(EfsElementDTO element);
}
