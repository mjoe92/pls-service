package de.vw.paso.service.partlist.efsedit;

import de.vw.paso.service.user.VehiclePartListDTO;

public record CopyOrMoveVehiclePartListDTO(VehiclePartListDTO newParent, EfsElementListDTO efsElementListDTO)
  implements CopyOrMove { }
