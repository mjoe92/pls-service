package de.vw.paso.service.partlist.smartfix;

import java.util.Collection;

public interface SmartFixRestService {

    String URL = "/api/smart-fix";

    SmartFixListDTO loadAll();

    SmartFixDTO save(SmartFixDTO fix);

    void delete(Long id);

    SmartFixListDTO loadByFields(Collection<String> fields);
}
