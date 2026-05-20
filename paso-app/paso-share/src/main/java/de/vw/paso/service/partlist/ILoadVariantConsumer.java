package de.vw.paso.service.partlist;

import de.vw.paso.exception.ServiceConsumer;

public interface ILoadVariantConsumer extends ServiceConsumer {

  void loadVariants(String nodeId, Long vehicleConfigId);

}
