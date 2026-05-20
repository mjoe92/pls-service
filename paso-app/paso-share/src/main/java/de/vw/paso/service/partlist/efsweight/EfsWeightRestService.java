package de.vw.paso.service.partlist.efsweight;

public interface EfsWeightRestService {
  String URL = "/api/efs-weight";

  Double updateVehiclePartListWeight(Long vehiclePartListId);
}
