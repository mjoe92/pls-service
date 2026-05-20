package de.vw.paso.service.partlist.efsedit;

public record CopyOrMoveEfsElementDTO(EfsElementDTO newParent, EfsElementListDTO efsElementListDTO)
  implements CopyOrMove { }
