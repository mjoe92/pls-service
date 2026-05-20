package de.vw.paso.tiwh;

import java.sql.Timestamp;

import de.vw.paso.status.ImportStatus;

public interface IDatenstand {

    ImportStatus getImportStatus();

    void setImportStatus(ImportStatus importStatus);

    Timestamp getTimestampChange();

}
