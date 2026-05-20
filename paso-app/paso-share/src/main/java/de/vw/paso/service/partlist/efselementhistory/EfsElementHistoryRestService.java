package de.vw.paso.service.partlist.efselementhistory;

public interface EfsElementHistoryRestService {

  String URL = "/api/efs-element-history";
  String LOAD_REVISIONS = "/revisions/";
  String REVERT_TO_REVISION = "/revert-to-revision";

  EfsElementAndMaraAndHistoryListDTO loadHistoryList(Long efsElementId);

  EfsElementAndMaraAndHistoryListDTO loadRevisions(Long vehiclePartListId);

  EfsElementCollection revertToRevision(RevertToRevisionDTO revertToRevisionDTO);
}
