package de.vw.paso.client.stueckliste.compare.partlist.combine.nodematcher;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import lombok.Setter;

public abstract class AbstractPathNodeIdentityProvider implements INodeIdentityProvider<String> {

  private static final String PATH_SEPERATOR = "/";

  protected Cache<EfsElementDTO, String> pathCache = CacheBuilder.newBuilder().maximumSize(6000).build();

  @Setter
  private boolean checkPath = true;

  @Override
  public String getIdentity(EfsElementDTO element) {
    String identity = pathCache.getIfPresent(element);
    if (identity == null) {
      if (element.getParent() != null && checkPath) {
        identity = getIdentity(element.getParent()) + PATH_SEPERATOR + getIdOfNode(element);
      } else {
        identity = getIdOfNode(element);
      }
      pathCache.put(element, identity);
    }
    return identity;
  }

  protected abstract String getIdOfNode(EfsElementDTO element);

}
