package de.vw.paso.partlist.domain;

import java.io.Serializable;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public interface IPartListChildDTO extends Serializable {

    Long getVehiclePartListId();

    EfsElementDTO asParent();
}
