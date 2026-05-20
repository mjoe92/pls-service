package de.vw.paso.service.masterdata.vehicleproject;

public interface VehicleProjectRestService {
  String URL = "/api/vehicle-project";
  String UPDATE_SET_VERSION = "/update-set-version";

  VehicleProjectListDTO loadVehicleProjects();

  void updateVehicleProjectArchiveState(UpdateVehicleProjectArchiveStateDTO updateDTO);

  VehicleProjectDTO updateVehicleProjectSetVersion(UpdatedVehicleProjectSetVersionDTO vehicleProjectSetVersionDTO);
}
