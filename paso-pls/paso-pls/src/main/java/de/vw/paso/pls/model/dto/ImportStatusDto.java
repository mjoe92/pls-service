package de.vw.paso.pls.model.dto;

import de.vw.paso.pls.model.ImportStatus;
import org.bson.types.ObjectId;

public record ImportStatusDto(ObjectId productDataId, ImportStatus status) {

}
