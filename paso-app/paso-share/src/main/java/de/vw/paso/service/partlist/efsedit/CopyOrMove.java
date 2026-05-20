package de.vw.paso.service.partlist.efsedit;

import de.vw.paso.partlist.domain.IPartListChildDTO;

public interface CopyOrMove {

  IPartListChildDTO newParent();

  EfsElementListDTO efsElementListDTO();
}
