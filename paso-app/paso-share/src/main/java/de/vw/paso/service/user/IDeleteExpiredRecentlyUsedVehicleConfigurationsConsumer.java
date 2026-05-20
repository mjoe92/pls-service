package de.vw.paso.service.user;

import de.vw.paso.exception.ServiceConsumer;

public interface IDeleteExpiredRecentlyUsedVehicleConfigurationsConsumer extends ServiceConsumer {

  void deleteExpiredRecentlyUsedVehicleConfigurations();

}
