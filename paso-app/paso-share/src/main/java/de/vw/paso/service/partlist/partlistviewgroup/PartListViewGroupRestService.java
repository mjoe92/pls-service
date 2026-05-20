package de.vw.paso.service.partlist.partlistviewgroup;

import de.vw.paso.partlist.domain.PartListViewMode;

public interface PartListViewGroupRestService {

  String URL = "/api/part-list-view-group";

  PartListViewGroupListDTO loadPartListViewGroupsByPartListViewMode(PartListViewMode viewMode);
}
