package de.vw.paso.service.masterdata.partgroup;

import de.vw.paso.exception.CategoryCanNotBeDeletedException;

public interface PartGroupRestService {

  String URL = "/api/part-group";

  PartGroupListDTO loadPartGroups();

  void delete(boolean isMgr, int mgr, boolean isUgr, int ugr) throws CategoryCanNotBeDeletedException;

  PartGroupDTO addPartGroup(PartGroupDTO partGroup);

  PartGroupListDTO update(PartGroupDTO pg);
}
