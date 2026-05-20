package de.vw.paso.logic.partlist;

import de.vw.paso.repository.partlist.VehiclePartListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiclePartListManager {

  private final VehiclePartListRepository vehiclePartListRepository;

  public VehiclePartListManager(VehiclePartListRepository vehiclePartListRepository) {
    this.vehiclePartListRepository = vehiclePartListRepository;
  }

  @Transactional
  public void updateWeight(final Long vehiclePartListId, final Double weight) {
    vehiclePartListRepository.updateWeight(vehiclePartListId, weight);
  }
}
