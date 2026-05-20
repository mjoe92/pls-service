package de.vw.paso.service.partlist.inspector;

import de.vw.paso.partlist.dto.EfsElementAggregateMappingListDTO;

public interface InspectorRestService {

    String URL = "/api/inspector";
    String DELETE_LIST = "/delete-list";
    String SAVE_LIST = "/save-list";
    String LOAD_AGGREGATE_MAPPING = "/load-aggregate-mapping";

    InspectorIgnoresDTO loadIgnoreEntries(Long partListId);

    void deleteIgnores(InspectorIgnoresDTO toDelete);

    void saveIgnoreEntries(InspectorIgnoresDTO toSave);

    EfsElementAggregateMappingListDTO loadAggregateMapping(Long vehiclePartListId);
}
