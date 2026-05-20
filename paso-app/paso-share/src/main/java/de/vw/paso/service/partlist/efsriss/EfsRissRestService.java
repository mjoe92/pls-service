package de.vw.paso.service.partlist.efsriss;

public interface EfsRissRestService {

    String URL = "/api/efs-riss";

    AlternativePartsForGapListDTO getAlternativePartsForGap(String nodeId, long vehicleConfigId);
}
